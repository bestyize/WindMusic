import { reactive, readonly } from 'vue'
import { ApiError, artistOf, resolveLink, shareIdOf, type Song } from '../api/music'
import { isPlayableLink, type QualityCode } from '../domain/quality'

export interface Track {
  shareId: string
  title: string
  artist: string
  cover: string | null
  url: string
  /** LRC 文本，分享页加载后才有 */
  lyric?: string
}

interface PlayerState {
  track: Track | null
  playing: boolean
  loading: boolean
  currentTime: number
  duration: number
  /** 最近一次失败的原因，展示给用户 */
  error: string | null
}

/** 试听依次尝试的音质：先用体积小的，拿不到再换 */
const PREVIEW_QUALITIES: QualityCode[] = ['PQ', 'LQ', 'HQ']

const state = reactive<PlayerState>({
  track: null,
  playing: false,
  loading: false,
  currentTime: 0,
  duration: 0,
  error: null,
})

let audio: HTMLAudioElement | null = null

function audioElement(): HTMLAudioElement {
  if (audio) return audio
  audio = new Audio()
  audio.preload = 'metadata'
  audio.addEventListener('play', () => (state.playing = true))
  audio.addEventListener('pause', () => (state.playing = false))
  audio.addEventListener('ended', () => (state.playing = false))
  audio.addEventListener('timeupdate', () => (state.currentTime = audio!.currentTime))
  audio.addEventListener('durationchange', () => (state.duration = audio!.duration || 0))
  audio.addEventListener('waiting', () => (state.loading = true))
  audio.addEventListener('playing', () => (state.loading = false))
  audio.addEventListener('error', () => {
    state.loading = false
    state.playing = false
    if (audio!.getAttribute('src')) state.error = '音频加载失败，这首歌可能暂时无法试听。'
  })
  return audio
}

async function start(track: Track, autoplay: boolean) {
  const el = audioElement()
  state.track = track
  state.error = null
  state.currentTime = 0
  state.duration = 0
  el.src = track.url
  if (!autoplay) return
  try {
    state.loading = true
    await el.play()
  } catch {
    // 浏览器拦截自动播放时保持暂停，等用户点击播放
    state.loading = false
  }
}

/** 解析试听链接并播放搜索结果里的歌曲。 */
async function playSong(song: Song) {
  const shareId = shareIdOf(song)
  if (state.track?.shareId === shareId) return toggle()
  state.loading = true
  state.error = null
  try {
    let url: string | undefined
    for (const quality of PREVIEW_QUALITIES) {
      url = await resolveLink(song, quality)
      if (isPlayableLink(url)) break
    }
    if (!isPlayableLink(url)) {
      state.loading = false
      state.error = `《${song.songName ?? '这首歌'}》暂时没有可试听的音源，换个平台搜搜看。`
      return
    }
    await start(
      { shareId, title: song.songName ?? '未知歌曲', artist: artistOf(song), cover: song.picUrl, url },
      true,
    )
  } catch (e) {
    state.loading = false
    state.error = e instanceof ApiError ? e.message : '试听失败，请稍后再试。'
  }
}

/** 分享页载入的歌曲。浏览器通常不允许直接自动播放，所以只准备好，等用户点击。 */
function load(track: Track) {
  // 从搜索页点进来时正在播放同一首歌：不打断播放，只补上歌词。
  // 搜索结果里的歌名、歌手、封面比分享接口从歌词头部猜出来的更准，保留原来的。
  if (state.track?.shareId === track.shareId) {
    state.track = { ...state.track, lyric: track.lyric }
    return
  }
  void start(track, false)
}

function toggle() {
  const el = audioElement()
  if (!state.track) return
  if (el.paused) {
    state.error = null
    el.play().catch(() => (state.error = '播放失败，请再点一次。'))
  } else {
    el.pause()
  }
}

function seek(seconds: number) {
  const el = audioElement()
  if (Number.isFinite(seconds)) el.currentTime = Math.max(0, Math.min(seconds, state.duration || seconds))
}

function dismissError() {
  state.error = null
}

export function usePlayer() {
  return { state: readonly(state), playSong, load, toggle, seek, dismissError }
}
