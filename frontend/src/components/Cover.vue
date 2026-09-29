<script setup lang="ts">
import { ref, watch } from 'vue'

const props = defineProps<{ src: string | null | undefined; alt: string }>()

const FALLBACK = '/img/wind-icon-256.png'
const broken = ref(false)
watch(
  () => props.src,
  () => (broken.value = false),
)

/** 后端会用相对路径 img/... 表示默认封面 */
const resolved = (src: string | null | undefined) =>
  !src || broken.value || src.length < 10 ? FALLBACK : src
</script>

<template>
  <img class="cover" :src="resolved(src)" :alt="alt" loading="lazy" referrerpolicy="no-referrer" @error="broken = true" />
</template>

<style scoped>
.cover {
  width: 100%;
  height: 100%;
  object-fit: cover;
  background: var(--line);
}
</style>
