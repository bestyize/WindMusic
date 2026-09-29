<script setup lang="ts">
import { ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ApiError, searchSongs, SOURCES, type Song, type SourceCode } from '../api/music'
import SongRow from '../components/SongRow.vue'
import { openWechat } from '../components/wechat'

const route = useRoute()
const router = useRouter()

const DEFAULT_SOURCE: SourceCode = 'KW'

const keyword = ref('')
const source = ref<SourceCode>(DEFAULT_SOURCE)
const songs = ref<Song[]>([])
const status = ref<'idle' | 'loading' | 'done' | 'error'>('idle')
const error = ref<{ message: string; limited: boolean } | null>(null)
/** 结果对应的关键词，避免输入框改了之后标题跟着变 */
const searched = ref('')

const isSource = (value: unknown): value is SourceCode => SOURCES.some((s) => s.code === value)

// 关键词和平台放在地址栏里（?keyword=晴天&src=KW），刷新、后退、分享都能还原
watch(
  () => [route.query.keyword, route.query.src] as const,
  async ([q, src]) => {
    const kw = typeof q === 'string' ? q.trim() : ''
    keyword.value = kw
    source.value = isSource(src) ? src : DEFAULT_SOURCE
    if (!kw) {
      status.value = 'idle'
      songs.value = []
      return
    }
    status.value = 'loading'
    error.value = null
    searched.value = kw
    try {
      songs.value = await searchSongs(kw, source.value)
      status.value = 'done'
    } catch (e) {
      songs.value = []
      status.value = 'error'
      error.value =
        e instanceof ApiError
          ? { message: e.message, limited: e.limited }
          : { message: '搜索失败，请稍后再试。', limited: false }
    }
  },
  { immediate: true },
)

function submit() {
  const kw = keyword.value.trim()
  if (!kw) return
  void router.push({ query: { keyword: kw, src: source.value } })
}

function pickSource(code: SourceCode) {
  source.value = code
  if (keyword.value.trim()) submit()
}

const sourceName = (code: SourceCode) => SOURCES.find((s) => s.code === code)?.name ?? code
</script>

<template>
  <div class="page" :class="{ 'page--idle': status === 'idle' }">
    <header class="console">
      <RouterLink to="/" class="brand" aria-label="无名音乐首页">
        <span class="brand__mark">Wind</span>
        <span class="brand__name">无名音乐</span>
      </RouterLink>

      <h1 v-if="status === 'idle'" class="console__lead">搜一首歌，挑一档音质，直接下载。</h1>

      <form class="search" role="search" @submit.prevent="submit">
        <label class="visually-hidden" for="keyword">歌名、歌手或专辑</label>
        <input
          id="keyword"
          v-model="keyword"
          class="search__input"
          type="search"
          enterkeyhint="search"
          autocomplete="off"
          placeholder="歌名、歌手或专辑"
          :autofocus="status === 'idle'"
        />
        <button class="search__go" type="submit">搜索</button>
      </form>

      <div class="sources" role="radiogroup" aria-label="音乐平台">
        <button
          v-for="s in SOURCES"
          :key="s.code"
          type="button"
          role="radio"
          class="sources__item"
          :aria-checked="source === s.code"
          @click="pickSource(s.code)"
        >
          {{ s.name }}
        </button>
      </div>
    </header>

    <main class="results" :aria-busy="status === 'loading'">
      <template v-if="status === 'loading'">
        <p class="results__note">正在{{ sourceName(source) }}搜索「{{ searched }}」…</p>
      </template>

      <template v-else-if="status === 'error' && error">
        <div class="results__empty" role="alert">
          <p>{{ error.message }}</p>
          <button v-if="error.limited" type="button" class="link-button" @click="openWechat">看公众号里的最新说明</button>
        </div>
      </template>

      <template v-else-if="status === 'done'">
        <div v-if="songs.length === 0" class="results__empty">
          <p>{{ sourceName(source) }}没有找到「{{ searched }}」。</p>
          <p class="results__hint">换个平台试试，或者只搜歌名。</p>
        </div>
        <section v-else class="panel" :aria-label="`「${searched}」的搜索结果`">
          <h2 class="panel__head">
            {{ sourceName(source) }}上的「{{ searched }}」<span>{{ songs.length }} 首</span>
          </h2>
          <ol class="panel__list">
            <SongRow v-for="song in songs" :key="`${song.songSrc}_${song.songId}`" :song="song" />
          </ol>
        </section>
      </template>

      <template v-else>
        <dl class="legend" aria-label="音质说明">
          <div><dt>HQ</dt><dd>320k MP3，手机日常听</dd></div>
          <div><dt>SQ</dt><dd>无损 FLAC，CD 音质</dd></div>
          <div><dt>ZQ</dt><dd>Hi-Res 24bit，耳机党用</dd></div>
        </dl>
      </template>
    </main>

    <footer class="foot">
      <button type="button" class="link-button" @click="openWechat">公众号「从来不想」</button>
      <span>音源来自各音乐平台，仅供个人试听学习。</span>
    </footer>
  </div>
</template>

<style scoped>
.page {
  max-width: var(--page-max);
  margin: 0 auto;
  padding: 1.25rem var(--gutter) calc(var(--deck-height) + 3rem + env(safe-area-inset-bottom));
}

/* 顶部控制台：铝面板上的搜索区 */
.console {
  display: grid;
  grid-template-columns: auto minmax(0, 1fr);
  grid-template-areas:
    'brand search'
    '. sources';
  column-gap: 1.5rem;
  row-gap: 0.75rem;
  align-items: center;
  padding-bottom: 1.25rem;
}

.page--idle .console {
  grid-template-columns: minmax(0, 1fr);
  grid-template-areas: 'brand' 'lead' 'search' 'sources';
  padding-block: clamp(2rem, 12vh, 7rem) 2.5rem;
  row-gap: 1.1rem;
}

.brand {
  grid-area: brand;
  display: flex;
  align-items: baseline;
  gap: 0.5rem;
  text-decoration: none;
}

.brand__mark {
  font-family: var(--font-label);
  font-weight: 800;
  font-size: var(--fs-xl);
  line-height: 1;
  color: var(--accent);
}

.brand__name {
  color: var(--ink-soft);
  font-size: var(--fs-sm);
}

.console__lead {
  grid-area: lead;
  margin: 0;
  max-width: 16em;
  font-size: clamp(var(--fs-xl), 5vw, var(--fs-2xl));
  line-height: 1.25;
  font-weight: 700;
  letter-spacing: -0.01em;
}

.search {
  grid-area: search;
  display: flex;
  border-radius: var(--radius);
  background: var(--surface);
  box-shadow: 0 0 0 1px var(--line), 0 6px 20px var(--shadow);
}

.search:focus-within {
  box-shadow: 0 0 0 2px var(--accent), 0 6px 24px color-mix(in srgb, var(--accent) 18%, transparent);
}

.search__input {
  flex: 1;
  min-width: 0;
  padding: 0.7rem 1rem;
  border: 0;
  background: transparent;
  font-size: var(--fs-md);
  outline: none;
}

.page--idle .search__input {
  padding-block: 0.95rem;
  font-size: var(--fs-lg);
}

.search__go {
  flex: none;
  margin: 0.3rem;
  padding: 0 1.25rem;
  border: 0;
  border-radius: calc(var(--radius) - 4px);
  background: var(--accent);
  color: var(--accent-ink);
  font-weight: 600;
  transition: background-color 120ms;
}

.search__go:hover {
  background: var(--accent-hover);
}

.sources {
  grid-area: sources;
  display: flex;
  flex-wrap: wrap;
  gap: 0.4rem;
}

.sources__item {
  padding: 0.25rem 0.8rem;
  border: 1px solid var(--line);
  border-radius: 999px;
  background: transparent;
  color: var(--ink-soft);
  font-size: var(--fs-sm);
}

.sources__item[aria-checked='true'] {
  border-color: var(--accent);
  background: var(--accent);
  color: var(--accent-ink);
}

/* 结果面板：一整块白色面板，行之间用细线分隔 */
.panel {
  border-radius: var(--radius);
  background: var(--surface);
  box-shadow: 0 0 0 1px var(--line), 0 10px 30px var(--shadow);
  overflow: hidden;
}

.panel__head {
  display: flex;
  justify-content: space-between;
  gap: 1rem;
  margin: 0;
  padding: 0.8rem var(--gutter);
  font-size: var(--fs-sm);
  font-weight: 600;
}

.panel__head span {
  color: var(--ink-faint);
  font-weight: 400;
}

.panel__list {
  margin: 0;
  padding: 0;
  list-style: none;
}

.results__note,
.results__empty {
  padding: 3rem 0;
  text-align: center;
  color: var(--ink-soft);
}

.results__empty p {
  margin: 0 0 0.5rem;
}

.results__hint {
  color: var(--ink-faint);
  font-size: var(--fs-sm);
}

.legend {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(12rem, 1fr));
  gap: 1.5rem;
  margin: 0;
}

.legend div {
  display: grid;
  gap: 0.2rem;
  padding-top: 0.75rem;
  border-top: 3px solid var(--lv-hq);
}

.legend div:nth-child(2) {
  border-color: var(--lv-sq);
}

.legend div:nth-child(3) {
  border-color: var(--lv-zq);
}

.legend dt {
  font-family: var(--font-label);
  font-weight: 800;
  font-size: var(--fs-xl);
  line-height: 1;
}

.legend dd {
  margin: 0;
  color: var(--ink-soft);
  font-size: var(--fs-sm);
}

.foot {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem 1.25rem;
  margin-top: 2.5rem;
  color: var(--ink-faint);
  font-size: var(--fs-xs);
}

.link-button {
  padding: 0;
  border: 0;
  background: none;
  color: var(--accent);
  font-size: inherit;
  text-decoration: underline;
  text-underline-offset: 3px;
}

@media (max-width: 44rem) {
  .console {
    grid-template-columns: minmax(0, 1fr);
    grid-template-areas: 'brand' 'search' 'sources';
  }

  .panel {
    margin-inline: calc(var(--gutter) * -1);
    border-radius: 0;
  }

  .sources {
    flex-wrap: nowrap;
    overflow-x: auto;
    margin-inline: calc(var(--gutter) * -1);
    padding-inline: var(--gutter);
    scrollbar-width: none;
  }

  .sources__item {
    flex: none;
  }
}
</style>
