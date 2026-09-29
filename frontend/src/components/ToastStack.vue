<script setup lang="ts">
import { useToast } from './useToast'
import { openWechat } from './wechat'

const { toasts, dismiss } = useToast()
</script>

<template>
  <div class="toasts" role="status" aria-live="polite">
    <div v-for="toast in toasts" :key="toast.id" class="toast">
      <span>{{ toast.message }}</span>
      <button v-if="toast.withWechat" type="button" class="toast__action" @click="openWechat(); dismiss(toast.id)">
        看公众号通知
      </button>
      <button type="button" class="toast__close" aria-label="关闭提示" @click="dismiss(toast.id)">×</button>
    </div>
  </div>
</template>

<style scoped>
.toasts {
  position: fixed;
  z-index: 30;
  left: 50%;
  bottom: calc(var(--deck-height) + 1rem + env(safe-area-inset-bottom));
  transform: translateX(-50%);
  display: grid;
  gap: 0.5rem;
  width: min(32rem, calc(100vw - 2rem));
}

.toast {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.7rem 0.75rem 0.7rem 1rem;
  border-radius: var(--radius);
  background: var(--deck);
  color: var(--deck-ink);
  font-size: var(--fs-sm);
  box-shadow: 0 0.75rem 2rem var(--overlay);
}

.toast > span {
  flex: 1;
}

.toast__action {
  flex: none;
  border: 1px solid var(--deck-line);
  border-radius: 4px;
  background: transparent;
  padding: 0.2rem 0.6rem;
  font-size: var(--fs-xs);
}

.toast__close {
  flex: none;
  border: 0;
  background: transparent;
  font-size: var(--fs-lg);
  line-height: 1;
  opacity: 0.7;
}
</style>
