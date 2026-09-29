<script setup lang="ts">
import { computed } from 'vue'
import { formatTime } from '../domain/lrc'

const props = defineProps<{
  current: number
  duration: number
  /** 深色背景上使用 */
  onDark?: boolean
}>()

const emit = defineEmits<{ seek: [seconds: number] }>()

const percent = computed(() => (props.duration > 0 ? (props.current / props.duration) * 100 : 0))
</script>

<template>
  <div class="seek" :class="{ 'seek--dark': onDark }">
    <span class="seek__time">{{ formatTime(current) }}</span>
    <input
      class="seek__range"
      type="range"
      min="0"
      :max="duration || 0"
      step="0.1"
      :value="current"
      :disabled="!duration"
      :style="{ '--p': `${percent}%` }"
      aria-label="播放进度"
      :aria-valuetext="`${formatTime(current)} / ${formatTime(duration)}`"
      @input="emit('seek', Number(($event.target as HTMLInputElement).value))"
    />
    <span class="seek__time">{{ formatTime(duration) }}</span>
  </div>
</template>

<style scoped>
.seek {
  --track: var(--line);
  --fill: var(--accent);
  display: flex;
  align-items: center;
  gap: 0.6rem;
  color: var(--ink-soft);
}

.seek--dark {
  --track: var(--deck-line);
  --fill: var(--accent-on-dark);
  color: var(--deck-soft);
}

.seek__time {
  flex: none;
  font-family: var(--font-label);
  font-weight: 500;
  font-size: var(--fs-sm);
  font-variant-numeric: tabular-nums;
}

.seek__range {
  flex: 1;
  min-width: 0;
  height: 1.25rem;
  margin: 0;
  appearance: none;
  background: transparent;
  cursor: pointer;
}

.seek__range:disabled {
  cursor: default;
}

.seek__range::-webkit-slider-runnable-track {
  height: 4px;
  border-radius: 2px;
  background: linear-gradient(var(--fill), var(--fill)) 0 / var(--p) 100% no-repeat, var(--track);
}

.seek__range::-moz-range-track {
  height: 4px;
  border-radius: 2px;
  background: var(--track);
}

.seek__range::-moz-range-progress {
  height: 4px;
  border-radius: 2px;
  background: var(--fill);
}

.seek__range::-webkit-slider-thumb {
  appearance: none;
  width: 12px;
  height: 12px;
  margin-top: -4px;
  border-radius: 50%;
  background: var(--fill);
}

.seek__range::-moz-range-thumb {
  width: 12px;
  height: 12px;
  border: 0;
  border-radius: 50%;
  background: var(--fill);
}

.seek__range:disabled::-webkit-slider-thumb {
  visibility: hidden;
}
</style>
