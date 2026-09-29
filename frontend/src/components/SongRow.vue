<script setup lang="ts">
import { computed, ref } from 'vue'
import { RouterLink } from 'vue-router'
import { ApiError, artistOf, resolveLink, shareIdOf, type Song } from '../api/music'
import { isPlayableLink, type QualityCode } from '../domain/quality'
import { usePlayer } from '../player/usePlayer'
import Cover from './Cover.vue'
import PlayButton from './PlayButton.vue'
import QualityMeter from './QualityMeter.vue'
import { useToast } from './useToast'

const props = defineProps<{ song: Song }>()

const player = usePlayer()
const toast = useToast()
const busy = ref<QualityCode | null>(null)

const title = computed(() => props.song.songName || '未知歌曲')
const shareId = computed(() => shareIdOf(props.song))
const isCurrent = computed(() => player.state.track?.shareId === shareId.value)

async function download(quality: QualityCode) {
  // 先同步打开空白页，解析完成后再跳转，避免被浏览器当作弹窗拦截
  const tab = window.open('', '_blank')
  busy.value = quality
  try {
    const link = await resolveLink(props.song, quality)
    if (!isPlayableLink(link)) {
      tab?.close()
      toast.show(`《${title.value}》暂时拿不到 ${quality} 音源，试试其他音质。`)
      return
    }
    if (tab) {
      tab.opener = null
      tab.location.href = link
    } else {
      location.href = link
    }
  } catch (e) {
    tab?.close()
    const limited = e instanceof ApiError && e.limited
    toast.show(e instanceof ApiError ? e.message : '下载失败，请稍后再试。', { withWechat: limited })
  } finally {
    busy.value = null
  }
}
</script>

<template>
  <li class="row" :class="{ 'is-current': isCurrent }">
    <PlayButton
      class="row__play"
      :size="2.25"
      :playing="isCurrent && player.state.playing"
      :loading="isCurrent && player.state.loading"
      :label="`${isCurrent && player.state.playing ? '暂停' : '试听'}《${title}》`"
      @click="player.playSong(song)"
    />
    <RouterLink class="row__cover" :to="{ path: '/player', query: { shareId } }" :aria-label="`打开《${title}》播放页`">
      <Cover :src="song.picUrl" :alt="''" />
    </RouterLink>
    <div class="row__text">
      <RouterLink class="row__title" :to="{ path: '/player', query: { shareId } }">{{ title }}</RouterLink>
      <p class="row__meta">
        <span>{{ artistOf(song) || '未知歌手' }}</span>
        <span v-if="song.albumName" class="row__album">{{ song.albumName }}</span>
      </p>
    </div>
    <QualityMeter class="row__meter" :max="song.quality" :song-name="title" :busy="busy" @download="download" />
  </li>
</template>

<style scoped>
.row {
  display: grid;
  grid-template-columns: auto 3.25rem minmax(0, 1fr) auto;
  grid-template-areas: 'play cover text meter';
  align-items: center;
  gap: 0.9rem;
  padding: 0.75rem var(--gutter);
  border-top: 1px solid var(--line);
}

.row.is-current {
  background: linear-gradient(90deg, var(--accent-tint), transparent 70%);
}

.row__play {
  grid-area: play;
  color: var(--accent);
}

.row__cover {
  grid-area: cover;
  width: 3.25rem;
  aspect-ratio: 1;
  border-radius: 8px;
  overflow: hidden;
}

.row__text {
  grid-area: text;
  min-width: 0;
}

.row__title {
  display: block;
  overflow: hidden;
  font-weight: 600;
  text-decoration: none;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.row__title:hover {
  text-decoration: underline;
  text-underline-offset: 3px;
}

.row__meta {
  display: flex;
  gap: 0.75rem;
  margin: 0;
  overflow: hidden;
  color: var(--ink-soft);
  font-size: var(--fs-sm);
  white-space: nowrap;
}

.row__meta > span {
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 歌手优先显示完整，专辑名放不下时截断 */
.row__meta > span:first-child {
  flex: none;
  max-width: 70%;
}

.row__album {
  color: var(--ink-faint);
}

.row__meter {
  grid-area: meter;
  width: 19rem;
}

@media (max-width: 44rem) {
  .row {
    grid-template-columns: 3rem minmax(0, 1fr) auto;
    grid-template-areas:
      'cover text play'
      'meter meter meter';
    row-gap: 0.6rem;
  }

  .row__cover {
    width: 3rem;
  }

  .row__meter {
    width: 100%;
  }
}
</style>
