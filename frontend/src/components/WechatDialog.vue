<script setup lang="ts">
import { useTemplateRef, watch } from 'vue'
import { wechatOpen as open } from './wechat'

const dialog = useTemplateRef<HTMLDialogElement>('dialog')

watch(open, (value) => {
  if (value && !dialog.value?.open) dialog.value?.showModal()
  if (!value && dialog.value?.open) dialog.value.close()
})
</script>

<template>
  <dialog ref="dialog" class="wechat" aria-labelledby="wechat-title" @close="open = false" @click.self="open = false">
    <div class="wechat__body">
      <h2 id="wechat-title">关注公众号「从来不想」</h2>
      <p>音源失效、接口调整、访问限制放开，都会第一时间在这里通知。</p>
      <img src="/img/wechatpublic.jpg" alt="公众号 dxstime 的二维码" width="500" height="272" />
      <button type="button" class="wechat__close" @click="open = false">知道了</button>
    </div>
  </dialog>
</template>

<style scoped>
.wechat {
  border: 0;
  padding: 0;
  border-radius: var(--radius);
  width: min(28rem, calc(100vw - 2rem));
  box-shadow: 0 1.5rem 3rem var(--shadow), 0 0 0 1px var(--line);
}

.wechat::backdrop {
  background: var(--overlay);
}

.wechat__body {
  padding: 1.5rem;
}

h2 {
  margin: 0 0 0.25rem;
  font-size: var(--fs-lg);
}

p {
  margin: 0 0 1rem;
  color: var(--ink-soft);
  font-size: var(--fs-sm);
}

img {
  width: 100%;
  height: auto;
  border-radius: 4px;
}

.wechat__close {
  margin-top: 1rem;
  width: 100%;
  padding: 0.6rem;
  border: 0;
  border-radius: var(--radius);
  background: var(--accent);
  color: var(--accent-ink);
  font-weight: 600;
}
</style>
