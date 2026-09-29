import { ref } from 'vue'

/** 公众号弹窗是否打开，全局唯一。 */
export const wechatOpen = ref(false)

export function openWechat() {
  wechatOpen.value = true
}
