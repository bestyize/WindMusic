<script setup lang="ts">
import { computed } from 'vue'
import { availableQualities, QUALITY_TIERS, type QualityCode } from '../domain/quality'

const props = defineProps<{
  /** 平台报告的最高音质 */
  max: string | null
  songName: string
  /** 正在解析链接的音质 */
  busy?: QualityCode | null
}>()

const emit = defineEmits<{ download: [quality: QualityCode] }>()

const available = computed(() => availableQualities(props.max))
</script>

<template>
  <div class="meter" role="group" :aria-label="`下载《${songName}》`">
    <button
      v-for="tier in QUALITY_TIERS"
      :key="tier.code"
      type="button"
      class="meter__seg"
      :class="[`meter__seg--${tier.code.toLowerCase()}`, { 'is-busy': busy === tier.code }]"
      :disabled="!available.has(tier.code) || !!busy"
      :aria-label="`下载${tier.label}`"
      :title="available.has(tier.code) ? `下载${tier.label}` : `这首歌没有${tier.label}`"
      @click="emit('download', tier.code)"
    >
      <span class="meter__lamp" aria-hidden="true"></span>
      <span class="meter__code">{{ tier.code }}</span>
      <span class="meter__short">{{ tier.short }}</span>
    </button>
  </div>
</template>

<style scoped>
.meter {
  display: grid;
  grid-template-columns: repeat(6, minmax(2.75rem, 1fr));
  gap: 3px;
}

.meter__seg {
  --lv: var(--lv-off);
  display: grid;
  justify-items: center;
  gap: 0.15rem;
  padding: 0.3rem 0 0.35rem;
  border: 0;
  border-radius: 3px;
  background: transparent;
  color: var(--ink);
  transition: background-color 120ms;
}

.meter__seg--lq { --lv: var(--lv-lq); }
.meter__seg--pq { --lv: var(--lv-pq); }
.meter__seg--hq { --lv: var(--lv-hq); }
.meter__seg--sq { --lv: var(--lv-sq); }
.meter__seg--zq { --lv: var(--lv-zq); }
.meter__seg--eq { --lv: var(--lv-eq); }

.meter__lamp {
  width: 100%;
  height: 0.45rem;
  border-radius: 2px;
  background: var(--lv);
  box-shadow: inset 0 -1px 0 rgb(0 0 0 / 0.12);
}

.meter__code {
  font-family: var(--font-label);
  font-weight: 700;
  font-size: var(--fs-md);
  line-height: 1;
  letter-spacing: 0.02em;
}

.meter__short {
  font-size: 0.6875rem;
  line-height: 1;
  color: var(--ink-faint);
}

.meter__seg:not(:disabled):hover {
  background: color-mix(in srgb, var(--lv) 16%, transparent);
}

.meter__seg:disabled {
  cursor: default;
}

.meter__seg:disabled:not(.is-busy) .meter__lamp {
  background: var(--lv-off);
  box-shadow: none;
}

.meter__seg:disabled:not(.is-busy) .meter__code,
.meter__seg:disabled:not(.is-busy) .meter__short {
  color: var(--lv-off-ink);
}

.is-busy .meter__lamp {
  animation: blink 0.6s steps(2, jump-none) infinite;
}

@keyframes blink {
  50% {
    opacity: 0.25;
  }
}
</style>
