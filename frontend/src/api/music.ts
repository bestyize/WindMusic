import type { QualityCode } from '../domain/quality'

export type SourceCode = 'KW' | 'WY' | 'QQ' | 'MG' | 'KG'

export const SOURCES: readonly { code: SourceCode; name: string }[] = [
  { code: 'KW', name: '酷我' },
  { code: 'WY', name: '网易云' },
  { code: 'QQ', name: 'QQ 音乐' },
  { code: 'MG', name: '咪咕' },
  { code: 'KG', name: '酷狗' },
]

/** 与后端 SongBean 对应，只列出前端用到的字段。 */
export interface Song {
  songSrc: string
  songId: string
  songName: string | null
  singersName: string[] | null
  albumName: string | null
  picUrl: string | null
  quality: string | null
  downloadLinkMap: Partial<Record<QualityCode, string>>
}

export interface PlayerInfo {
  title: string
  author: string
  pic: string
  url: string
  lyc: string
}

export class ApiError extends Error {
  /** 触发了按 IP 的访问次数限制 */
  readonly limited: boolean

  constructor(message: string, limited = false) {
    super(message)
    this.limited = limited
  }
}

async function post<T>(path: string, params: Record<string, string>): Promise<T> {
  let res: Response
  try {
    res = await fetch(path, { method: 'POST', body: new URLSearchParams(params) })
  } catch {
    throw new ApiError('网络连接失败，请检查网络后重试。')
  }
  if (!res.ok) throw new ApiError(`服务暂时不可用（HTTP ${res.status}），请稍后再试。`)
  const data: unknown = await res.json()
  // 限流时接口返回 {"status":-1,"msg":"..."}，而不是正常的数据
  if (data && typeof data === 'object' && !Array.isArray(data) && (data as { status?: number }).status === -1) {
    throw new ApiError((data as { msg?: string }).msg ?? '访问次数过多，请稍后再试。', true)
  }
  return data as T
}

export function searchSongs(keyword: string, src: SourceCode, num = 20): Promise<Song[]> {
  return post('/api/next/search', { keyword, src, num: String(num) })
}

export async function resolveLink(song: Pick<Song, 'songSrc' | 'songId'>, quality: QualityCode): Promise<string | undefined> {
  const result = await post<Song>('/api/next/download', { src: song.songSrc, songid: song.songId, quality })
  return result.downloadLinkMap?.[quality]
}

export async function fetchPlayerInfo(shareId: string): Promise<PlayerInfo | undefined> {
  const list = await post<PlayerInfo[]>('/api/next/player', { shareId })
  return list[0]
}

export const shareIdOf = (song: Pick<Song, 'songSrc' | 'songId'>) => `${song.songSrc}_${song.songId}`

export const artistOf = (song: Pick<Song, 'singersName'>) =>
  (song.singersName ?? []).filter(Boolean).slice(0, 2).join(' / ')
