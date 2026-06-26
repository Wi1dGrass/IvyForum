<template>
  <div v-loading="loading">
    <h2 class="sf-page-title mb-16">我的收藏</h2>
    <div class="collect sf-card" v-for="c in list" :key="c.collectId" @click="$router.push(`/post/${c.postId}`)">
      <el-icon class="ico"><Star /></el-icon>
      <span class="title line-2">{{ c.title }}</span>
      <span class="time">{{ formatTime(c.createTime) }}</span>
    </div>
    <div v-if="!loading && !list.length" class="sf-empty">还没有收藏任何帖子</div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { Star } from '@element-plus/icons-vue'
import { collectApi } from '@/api/collect'
import { formatTime } from '@/utils/date'
import type { CollectItem } from '@/types'

const list = ref<CollectItem[]>([])
const loading = ref(true)
onMounted(async () => { list.value = await collectApi.list(); loading.value = false })
</script>

<style scoped lang="scss">
.collect { display: flex; align-items: center; gap: 12px; padding: 14px 20px; margin-bottom: 12px; cursor: pointer; transition: transform .2s ease, box-shadow .2s ease;
  &:hover { transform: translateY(-2px); box-shadow: var(--sf-shadow-hover); }
}
.ico { color: var(--sf-warning, #f4b740); font-size: 18px; }
.title { flex: 1; min-width: 0; font-weight: 600; }
.time { font-size: 12px; color: var(--sf-text-3, #94a3b8); }
</style>