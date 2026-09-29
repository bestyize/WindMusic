<script setup lang="ts">
import { RouterView, useRoute } from 'vue-router'
import PlayerDeck from './components/PlayerDeck.vue'
import ToastStack from './components/ToastStack.vue'
import WechatDialog from './components/WechatDialog.vue'

const route = useRoute()
</script>

<template>
  <RouterView />
  <!-- 分享页有自己的大播放控件，底部播放条只在搜索页显示 -->
  <PlayerDeck v-if="route.name !== 'share'" />
  <ToastStack />
  <WechatDialog />
</template>
