<template>
  <div class="loading" v-loading="loading">
    <div class="header">
      <div class="header-bg"></div>
      <div class="header-text">
        <h2>🔥 热门榜</h2>
        <p>综合浏览、点赞、评论与时间衰减计算 · 每30分钟自动刷新</p>
      </div>
    </div>

    <div class="rank-list">
      <div v-for="(p, i) in list" :key="p.postId" class="rank-card" @click="$router.push(`/post/${p.postId}`)">
        <div class="rank-left">
          <div v-if="i < 3" class="medal" :class="`m${i+1}`">
            <span class="medal-icon">{{ ['🥇','🥈','🥉'][i] }}</span>
            <span class="medal-no">{{ i+1 }}</span>
          </div>
          <div v-else class="rank-no">{{ i+1 }}</div>
        </div>

        <div class="rank-body">
          <div class="rank-title line-2">{{ p.title }}</div>
          <div class="rank-summary" v-if="p.contentAbstract">
            <div class="abstract line-2">{{ p.contentAbstract }}</div>
            <div class="thumb" v-if="p.firstImage">
              <img :src="p.firstImage" alt="" @error="(e:any)=>(e.target.style.display='none')" />
            </div>
          </div>
          <div class="rank-meta">
            <span class="channel-tag">{{ p.channelName }}</span>
            <span>{{ p.authorNickname }}</span>
          </div>
          <div class="rank-stats">
            <span class="stat-item hot-stat"><el-icon><TrendCharts /></el-icon>热度 {{ p.hotScore != null ? Math.round(p.hotScore) : 0 }}</span>
            <span class="stat-item"><el-icon><View /></el-icon>{{ p.viewCount }}</span>
            <span class="stat-item"><el-icon><Pointer /></el-icon>{{ p.likeCount }}</span>
            <span class="stat-item"><el-icon><ChatLineRound /></el-icon>{{ p.commentCount }}</span>
          </div>
        </div>

      </div>
    </div>

    <div v-if="!loading && !list.length" class="sf-empty">暂无热度数据</div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { View, Pointer, ChatLineRound, TrendCharts } from '@element-plus/icons-vue'
import { postApi } from '@/api/post'
import type { HotPostItem } from '@/types'

const list = ref<HotPostItem[]>([])
const loading = ref(true)

onMounted(async () => { list.value = await postApi.hot(); loading.value = false })
</script>

<style scoped lang="scss">
.header {
  position: relative; overflow: hidden; border-radius: var(--sf-radius, 12px);
  padding: 32px 28px; margin-bottom: 20px; background: var(--sf-card, #fff);
  border: 1px solid var(--sf-border-soft, #f0f3f9);
}
.header-bg {
  position: absolute; inset: 0;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);
  &::after {
    content: ''; position: absolute; inset: 0;
    background: radial-gradient(circle at 80% 20%, rgba(247,127,0,.25), transparent 60%),
                radial-gradient(circle at 20% 80%, rgba(67,97,238,.2), transparent 50%);
  }
}
.header-text { position: relative; z-index: 1; color: #fff; }
.header-text h2 { font-size: 26px; font-weight: 800; margin-bottom: 6px; }
.header-text p { opacity: .75; font-size: 14px; }

.rank-list { display: flex; flex-direction: column; gap: 12px; }

.rank-card {
  display: flex; align-items: center; gap: 16px;
  background: var(--sf-card, #fff); border-radius: var(--sf-radius, 12px);
  padding: 16px 20px; cursor: pointer;
  border: 1px solid var(--sf-border-soft, #f0f3f9);
  transition: transform .2s ease, box-shadow .2s ease;
  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 24px rgba(67,97,238,.12);
  }
}

.rank-left { flex: none; width: 44px; text-align: center; }
.medal {
  display: flex; flex-direction: column; align-items: center; gap: 1px;
  &.m1 .medal-icon { font-size: 26px; }
  &.m2 .medal-icon { font-size: 24px; }
  &.m3 .medal-icon { font-size: 22px; }
}
.medal-no { font-size: 10px; font-weight: 700; color: var(--sf-text-3, #94a3b8); margin-top: -2px; }
.rank-no {
  width: 36px; height: 36px; border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  font-weight: 700; font-size: 14px;
  background: var(--sf-bg-soft, #eef2f9); color: var(--sf-text-2, #475569);
}

.rank-body { flex: 1; min-width: 0; }
.rank-title { font-size: 16px; font-weight: 600; color: var(--sf-text-1, #1f2937); }
.rank-summary { display: flex; flex-direction: column; gap: 8px; margin-top: 8px; }
.abstract { font-size: 13px; color: var(--sf-text-2, #475569); line-height: 1.5; }
.line-2 { display: -webkit-box; -webkit-box-orient: vertical; -webkit-line-clamp: 2; overflow: hidden; }
.thumb { width: 100%; max-height: 160px; border-radius: 6px; overflow: hidden; background: var(--sf-bg-soft, #eef2f9); }
.thumb img { width: 100%; height: 160px; object-fit: cover; }
.rank-meta { display: flex; align-items: center; gap: 8px; margin-top: 6px; font-size: 13px; color: var(--sf-text-3, #94a3b8); }
.channel-tag {
  font-size: 11px; padding: 1px 8px; border-radius: 999px;
  background: rgba(67,97,238,.1); color: var(--sf-primary, #4361ee); font-weight: 500;
}
.rank-stats { display: flex; gap: 16px; margin-top: 8px; font-size: 12px; color: var(--sf-text-3, #94a3b8); }
.stat-item { display: inline-flex; align-items: center; gap: 3px; }
.hot-stat { color: var(--sf-accent, #f77f00); font-weight: 600; }
</style>