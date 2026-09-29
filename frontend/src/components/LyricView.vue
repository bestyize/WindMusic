<script setup lang="ts">
import { computed, nextTick, useTemplateRef, watch } from 'vue'
import { activeLineIndex, parseLrc } from '../domain/lrc'

const props = defineProps<{ lrc: string | undefined; time: number }>()
const emit = defineEmits<{ seek: [seconds: number] }>()

const lines = computed(() => parseLrc(props.lrc))
const active = computed(() => activeLineIndex(lines.value, props.time))
const box = useTemplateRef<HTMLOListElement>('box')

const reduceMotion = window.matchMedia('(prefers-reduced-motion: reduce)')

// 当前行保持在歌词区上方三分之一处
watch(active, async (index) => {
  await nextTick()
  const el = box.value
  const line = el?.children[index] as HTMLElement | undefined
  if (!el || !line) return
  el.scrollTo({
    top: line.offsetTop - el.clientHeight / 3,
    behavior: reduceMotion.matches ? 'auto' : 'smooth',
  })
})
</script>

<template>
  <ol v-if="lines.length" ref="box" class="lyric" aria-label="歌词">
    <li
      v-for="(line, i) in lines"
      :key="`${line.time}-${i}`"
      :class="{ 'is-active': i === active }"
      :aria-current="i === active ? 'true' : undefined"
    >
      <button type="button" :aria-label="`从这句开始播放：${line.text}`" @click="emit('seek', line.time)">
        {{ line.text }}
      </button>
    </li>
  </ol>
  <p v-else class="lyric-empty">这首歌暂时没有歌词。</p>
</template>

<style scoped>
.lyric {
  position: relative;
  margin: 0;
  padding: 0;
  height: 100%;
  overflow-y: auto;
  list-style: none;
  scrollbar-width: none;
  mask-image: linear-gradient(transparent, #000 12%, #000 80%, transparent);
}

.lyric::-webkit-scrollbar {
  display: none;
}

.lyric::after {
  content: '';
  display: block;
  height: 60%;
}

.lyric li {
  padding: 0.3rem 0;
}

.lyric button {
  padding: 0;
  border: 0;
  background: none;
  color: var(--deck-soft);
  font-size: var(--fs-lg);
  line-height: 1.5;
  text-align: left;
  transition: color 200ms;
}

.lyric button:hover {
  color: var(--deck-ink);
}

.lyric .is-active button {
  color: var(--deck-ink);
  font-weight: 600;
}

.lyric-empty {
  margin: 0;
  color: var(--deck-soft);
}
</style>
