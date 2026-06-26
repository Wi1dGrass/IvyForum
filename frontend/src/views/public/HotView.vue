<template>
  <div class="loading" v-loading="loading">
    <h2 class="sf-page-title mb-16">🔥 热门榜</h2>
    <p class="sub mb-16">综合浏览、点赞、评论与时间衰减计算，定时刷新</p>

    <div class="rank sf-card" v-for="p in list" :key="p.postId" @click="$router.push(`/post/${p.postId}`)">
      <span class="no" :class="rankClass(p.rankNo)">{{ p.rankNo }}</span>
      <div class="info">
        <div class="t line-2">{{ p.title }}</div>
        <div class="meta">
          <span>{{ p.channelName }}</span><span class="dot">·</span>
          <span>{{ p.authorNickname }}</span><span class="dot">·</span>
          <span>🔥 热度 {{ Math.round(p.hotScore) }}</span>
        </div>
      </div>
      <div class="stats">
        <span><el-icon><View /></el-icon>{{ p.viewCount }}</span>
        <span><el-icon><Pointer /></el-icon>{{ p.likeCount }}</span>
        <span><el-icon><ChatLineRound /></el-icon>{{ p.commentCount }}</span>
      </div>
    </div>
    <div v-if="!loading && !list.length" class="sf-empty">暂无热度数据</div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { View, Pointer, ChatLineRound } from '@element-plus/icons-vue'
import { postApi } from '@/api/post'
import type { HotPostItem } from '@/types'

const list = ref<HotPostItem[]>([])
const loading = ref(true)
function rankClass(n: number) { return n <= 3 ? `top${n}` : '' }
onMounted(async () => { list.value = await postApi.hot(); loading.value = false })
</script>

<style scoped lang="scss">
.sub { color: var(--sf-text-3, #94a3b8); font-size: 13px; }
.rank { display: flex; align-items: center; gap: 16px; padding: 16px 20px; margin-bottom: 12px; cursor: pointer; transition: transform .2s ease, box-shadow .2s ease;
  &:hover { transform: translateY(-2px); box-shadow: var(--sf-shadow-hover); }
}
.no { width: 28px; height: 28px; border-radius: 8px; flex: none; display: flex; align-items: center; justify-content: center; font-weight: 800; background: var(--sf-bg-soft, #eef2f9); color: var(--sf-text-3, #94a3b8);
  &.top1 { background: #ff4d4f; color: #fff; }
  &.top2 { background: #ff7a45; color: #fff; }
  &.top3 { background: #ffa940; color: #fff; }
}
.info { flex: 1; min-width: 0; }
.t { font-size: 16px; font-weight: 600; color: var(--sf-text-1, #1f2937); }
.meta { font-size: 12px; color: var(--sf-text-3, #94a3b8); display: flex; align-items: center; gap: 6px; margin-top: 4px; }
.dot { color: var(--sf-text-4, #cbd5e1); }
.stats { display: flex; gap: 14px; color: var(--sf-text-3, #94a3b8); font-size: 13px; }
.stats span { display: inline-flex; align-items: center; gap: 4px; }
</style>