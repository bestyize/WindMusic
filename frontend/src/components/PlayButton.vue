<script setup lang="ts">
defineProps<{
  playing: boolean
  loading?: boolean
  /** 按钮直径，rem */
  size?: number
  label?: string
}>()
</script>

<template>
  <button
    type="button"
    class="play"
    :class="{ 'is-loading': loading }"
    :style="{ '--size': `${size ?? 2.5}rem` }"
    :aria-label="label ?? (playing ? '暂停' : '播放')"
    :aria-pressed="playing"
  >
    <svg v-if="playing" viewBox="0 0 24 24" aria-hidden="true">
      <rect x="6" y="5" width="4" height="14" rx="1" />
      <rect x="14" y="5" width="4" height="14" rx="1" />
    </svg>
    <svg v-else viewBox="0 0 24 24" aria-hidden="true">
      <path d="M8 5.5v13a1 1 0 0 0 1.5.86l10.4-6.5a1 1 0 0 0 0-1.72L9.5 4.64A1 1 0 0 0 8 5.5z" />
    </svg>
  </button>
</template>

<style scoped>
.play {
  position: relative;
  flex: none;
  display: grid;
  place-items: center;
  width: var(--size);
  height: var(--size);
  padding: 0;
  border: 0;
  border-radius: 50%;
  background: currentColor;
}

.play svg {
  width: 46%;
  height: 46%;
  fill: var(--play-glyph, var(--accent-ink));
}

.play.is-loading::after {
  content: '';
  position: absolute;
  inset: -3px;
  border: 2px solid transparent;
  border-top-color: currentColor;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}
</style>
