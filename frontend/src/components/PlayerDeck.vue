<script setup lang="ts">
import { RouterLink } from 'vue-router'
import { usePlayer } from '../player/usePlayer'
import Cover from './Cover.vue'
import PlayButton from './PlayButton.vue'
import SeekBar from './SeekBar.vue'

const { state, toggle, seek, dismissError } = usePlayer()
</script>

<template>
  <section class="deck" aria-label="播放器">
    <div class="deck__inner">
      <template v-if="state.track">
        <RouterLink
          class="deck__cover"
          :to="{ path: '/player', query: { shareId: state.track.shareId } }"
          :aria-label="`打开《${state.track.title}》播放页`"
        >
          <Cover :src="state.track.cover" alt="" />
        </RouterLink>
        <div class="deck__text">
          <p class="deck__title">{{ state.track.title }}</p>
          <p class="deck__artist">{{ state.track.artist || '未知歌手' }}</p>
        </div>
        <PlayButton class="deck__play" :playing="state.playing" :loading="state.loading" @click="toggle" />
        <SeekBar class="deck__seek" on-dark :current="state.currentTime" :duration="state.duration" @seek="seek" />
      </template>
      <p v-else class="deck__idle">点播放键试听，点音质条直接下载。</p>
    </div>
    <p v-if="state.error" class="deck__error" role="alert">
      {{ state.error }}
      <button type="button" aria-label="关闭提示" @click="dismissError">×</button>
    </p>
  </section>
</template>

<style scoped>
.deck {
  position: fixed;
  z-index: 20;
  inset: auto 0 0;
  background: var(--deck);
  color: var(--deck-ink);
  padding-bottom: env(safe-area-inset-bottom);
}

.deck__inner {
  display: grid;
  grid-template-columns: 3rem minmax(0, 14rem) auto minmax(0, 1fr);
  align-items: center;
  gap: 0.9rem;
  max-width: var(--page-max);
  min-height: var(--deck-height);
  margin: 0 auto;
  padding: 0 var(--gutter);
}

.deck__cover {
  width: 3rem;
  aspect-ratio: 1;
  border-radius: 8px;
  overflow: hidden;
}

.deck__text {
  min-width: 0;
}

.deck__text p {
  margin: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.deck__title {
  font-weight: 600;
}

.deck__artist {
  color: var(--deck-soft);
  font-size: var(--fs-sm);
}

.deck__play {
  color: var(--accent-on-dark);
  --play-glyph: var(--deck);
}

.deck__idle {
  grid-column: 1 / -1;
  margin: 0;
  color: var(--deck-soft);
  font-size: var(--fs-sm);
}

.deck__error {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 0.5rem;
  margin: 0;
  padding: 0.4rem var(--gutter);
  background: var(--danger);
  font-size: var(--fs-sm);
}

.deck__error button {
  border: 0;
  background: transparent;
  font-size: var(--fs-lg);
  line-height: 1;
}

@media (max-width: 44rem) {
  .deck__inner {
    grid-template-columns: 3rem minmax(0, 1fr) auto;
    grid-template-areas:
      'cover text play'
      'seek seek seek';
    row-gap: 0.25rem;
    padding-block: 0.6rem 0.4rem;
  }

  .deck__cover { grid-area: cover; }
  .deck__text { grid-area: text; }
  .deck__play { grid-area: play; }
  .deck__seek { grid-area: seek; }
}
</style>
