import { reactive, readonly } from 'vue'

export interface Toast {
  id: number
  message: string
  /** 访问受限时附带公众号入口 */
  withWechat?: boolean
}

const toasts = reactive<Toast[]>([])
let seq = 0

function show(message: string, options: { withWechat?: boolean } = {}) {
  const toast = { id: ++seq, message, ...options }
  toasts.push(toast)
  setTimeout(() => dismiss(toast.id), options.withWechat ? 8000 : 4000)
}

function dismiss(id: number) {
  const index = toasts.findIndex((t) => t.id === id)
  if (index >= 0) toasts.splice(index, 1)
}

export function useToast() {
  return { toasts: readonly(toasts), show, dismiss }
}
