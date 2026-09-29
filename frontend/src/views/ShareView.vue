<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { ApiError, fetchPlayerInfo, SOURCES } from '../api/music'
import Cover from '../components/Cover.vue'
import LyricView from '../components/LyricView.vue'
import PlayButton from '../components/PlayButton.vue'
import SeekBar from '../components/SeekBar.vue'
import { openWechat } from '../components/wechat'
import { isPlayableLink } from '../domain/quality'
import { usePlayer } from '../player/usePlayer'

const route = useRoute()
const player = usePlayer()

const shareId = computed(() => (typeof route.query.shareId === 'string' ? route.query.shareId : ''))
const status = ref<'loading' | 'ready' | 'error'>('loading')
const error = ref<{ message: string; limited: boolean } | null>(null)

const track = computed(() => (player.state.track?.shareId === shareId.value ? player.state.track : null))
const sourceName = computed(() => {
  const code = shareId.value.split('_')[0]
  return SOURCES.find((s) => s.code === code)?.name
})

watch(
  shareId,
  async (id) => {
    if (!/^[A-Z]{2}_.+/.test(id)) {
      status.value = 'error'
      error.value = { message: '分享链接不完整，找不到对应的歌曲。', limited: false }
      return
    }
    status.value = 'loading'
    try {
      const info = await fetchPlayerInfo(id)
      if (!info || !isPlayableLink(info.url)) {
        status.value = 'error'
        error.value = { message: '这首歌暂时没有可播放的音源。', limited: false }
        return
      }
      player.load({
        shareId: id,
        title: info.title || '未知歌曲',
        artist: info.author,
        cover: info.pic,
        url: info.url,
        lyric: info.lyc,
      })
      status.value = 'ready'
      document.title = `${info.title || '未知歌曲'}${info.author ? ` - ${info.author}` : ''} | 无名音乐`
    } catch (e) {
      status.value = 'error'
      error.value =
        e instanceof ApiError
          ? { message: e.message, limited: e.limited }
          : { message: '加载失败，请稍后再试。', limited: false }
    }
  },
  { immediate: true },
)
</script>

<template>
  <div class="share">
    <div class="share__backdrop" aria-hidden="true">
      <Cover v-if="track" :src="track.cover" alt="" />
    </div>

    <header class="share__top">
      <RouterLink to="/" class="share__home">← 搜索更多歌曲</RouterLink>
      <span v-if="sourceName" class="share__src">来自{{ sourceName }}</span>
    </header>

    <main v-if="status === 'ready' && track" class="share__stage">
      <div class="share__disc">
        <Cover :src="track.cover" :alt="`《${track.title}》封面`" />
      </div>

      <div class="share__info">
        <h1 class="share__title">{{ track.title }}</h1>
        <p class="share__artist">{{ track.artist || '未知歌手' }}</p>

        <div class="share__transport">
          <PlayButton class="share__play" :size="3.5" :playing="player.state.playing" :loading="player.state.loading" @click="player.toggle" />
          <SeekBar class="share__seek" on-dark :current="player.state.currentTime" :duration="player.state.duration" @seek="player.seek" />
        </div>
        <p v-if="player.state.error" class="share__error" role="alert">{{ player.state.error }}</p>

        <div class="share__lyric">
          <LyricView :lrc="track.lyric" :time="player.state.currentTime" @seek="player.seek" />
        </div>
      </div>
    </main>

    <main v-else class="share__message" :aria-busy="status === 'loading'">
      <p v-if="status === 'loading'">正在载入歌曲…</p>
      <template v-else-if="error">
        <p role="alert">{{ error.message }}</p>
        <button v-if="error.limited" type="button" class="share__link" @click="openWechat">看公众号里的最新说明</button>
        <RouterLink v-else to="/" class="share__link">去搜索这首歌</RouterLink>
      </template>
    </main>
  </div>
</template>

<style scoped>
.share {
  position: relative;
  isolation: isolate;
  min-height: 100dvh;
  overflow: hidden;
  background: var(--deck);
  color: var(--deck-ink);
}

.share__backdrop {
  position: absolute;
  z-index: -1;
  inset: -10%;
  opacity: 0.35;
  filter: blur(60px) saturate(1.3);
}

.share__backdrop::after {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(180deg, color-mix(in srgb, var(--deck) 40%, transparent), var(--deck) 85%);
}

.share__top {
  display: flex;
  justify-content: space-between;
  gap: 1rem;
  max-width: 64rem;
  margin: 0 auto;
  padding: 1.25rem var(--gutter);
  font-size: var(--fs-sm);
}

.share__home {
  color: var(--deck-ink);
  text-decoration: none;
}

.share__home:hover {
  text-decoration: underline;
  text-underline-offset: 3px;
}

.share__src {
  color: var(--deck-soft);
}

.share__stage {
  display: grid;
  grid-template-columns: minmax(0, 22rem) minmax(0, 1fr);
  gap: clamp(2rem, 6vw, 4.5rem);
  align-items: start;
  max-width: 64rem;
  margin: 0 auto;
  padding: clamp(1rem, 6vh, 4rem) var(--gutter) 2rem;
}

.share__disc {
  aspect-ratio: 1;
  border-radius: var(--radius);
  overflow: hidden;
  box-shadow: 0 2rem 4rem rgb(0 0 0 / 0.4);
}

.share__info {
  display: flex;
  flex-direction: column;
  min-width: 0;
  height: min(34rem, calc(100dvh - 9rem));
}

.share__title {
  margin: 0;
  font-size: clamp(var(--fs-xl), 4vw, var(--fs-2xl));
  line-height: 1.2;
  overflow-wrap: anywhere;
}

.share__artist {
  margin: 0.4rem 0 0;
  color: var(--deck-soft);
  font-size: var(--fs-lg);
}

.share__transport {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-top: 1.75rem;
}

.share__play {
  color: var(--accent-on-dark);
  --play-glyph: var(--deck);
}

.share__seek {
  flex: 1;
}

.share__error {
  margin: 0.75rem 0 0;
  color: var(--danger-soft);
  font-size: var(--fs-sm);
}

.share__lyric {
  flex: 1;
  min-height: 0;
  margin-top: 2rem;
}

.share__message {
  display: grid;
  justify-items: center;
  gap: 0.75rem;
  padding: 25vh var(--gutter) 0;
  text-align: center;
  color: var(--deck-soft);
}

.share__message p {
  margin: 0;
}

.share__link {
  padding: 0;
  border: 0;
  background: none;
  color: var(--deck-ink);
  font-size: var(--fs-sm);
  text-decoration: underline;
  text-underline-offset: 3px;
}

@media (max-width: 44rem) {
  .share__stage {
    grid-template-columns: minmax(0, 1fr);
    gap: 1.5rem;
    padding-top: 0.5rem;
  }

  .share__disc {
    width: min(70vw, 18rem);
    justify-self: center;
  }

  .share__info {
    height: auto;
  }

  .share__lyric {
    height: 40dvh;
  }
}
</style>
