<template>
  <div>
    <div class="head sf-card">
      <h2 class="c-title">搜索结果</h2>
      <p class="desc">关键词「{{ keyword }}」相关帖子</p>
    </div>
    <PostListPanel :keyword="keyword" />
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import PostListPanel from '@/components/PostListPanel.vue'

const route = useRoute()
const keyword = computed(() => {
  const k = route.query.keyword
  return typeof k === 'string' ? k : ''
})
</script>

<style scoped lang="scss">
.head { padding: 20px 22px; margin-bottom: 16px; }
.c-title { font-size: 22px; font-weight: 700; }
.desc { color: var(--sf-text-3, #94a3b8); margin-top: 4px; font-size: 13px; }
</style>