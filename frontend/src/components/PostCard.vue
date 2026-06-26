<template>
  <router-link :to="`/post/${post.postId}`" class="post-card sf-card">
    <div class="head">
      <el-avatar :size="36" :src="post.authorAvatar || ''">
        {{ post.authorNickname?.[0] }}
      </el-avatar>
      <div class="meta">
        <div class="author">
          <span>{{ post.authorNickname }}</span>
          <span v-if="post.isTop" class="sf-badge top">置顶</span>
          <span v-if="post.isEssence" class="sf-badge essence">精</span>
        </div>
        <div class="sub">
          <span>{{ post.channelName }}</span>
          <span class="dot">·</span>
          <span>{{ formatTime(post.createTime) }}</span>
        </div>
      </div>
    </div>

    <h3 class="title line-2">{{ post.title }}</h3>

    <div class="body" v-if="post.contentAbstract || post.firstImage">
      <div class="abstract line-3" v-if="post.contentAbstract">{{ post.contentAbstract }}</div>
      <div class="thumb-wrap" v-if="post.firstImage">
        <img :src="post.firstImage" alt="" @error="(e:any)=>(e.target.style.display='none')" />
      </div>
    </div>

    <div class="tags" v-if="post.tags?.length">
      <TagChip v-for="t in post.tags" :key="t.tagId" :tag="t" />
    </div>

    <div class="foot">
      <span class="stat"><el-icon><View /></el-icon>{{ formatCount(post.viewCount) }}</span>
      <span class="stat"><el-icon><Pointer /></el-icon>{{ formatCount(post.likeCount) }}</span>
      <span class="stat"><el-icon><ChatLineRound /></el-icon>{{ formatCount(post.commentCount) }}</span>
      <span class="stat"><el-icon><Star /></el-icon>{{ formatCount(post.collectCount) }}</span>
    </div>
  </router-link>
</template>

<script setup lang="ts">
import { View, Pointer, ChatLineRound, Star } from '@element-plus/icons-vue'
import TagChip from './TagChip.vue'
import { formatTime, formatCount } from '@/utils/date'
import type { PostListItem } from '@/types'

defineProps<{ post: PostListItem }>()
</script>

<style scoped lang="scss" xmlns="http://www.w3.org/1999/html">
.post-card {
  display: block; padding: 18px 20px; margin-bottom: 16px;
  transition: transform .2s ease, box-shadow .2s ease;
  &:hover { transform: translateY(-2px); box-shadow: var(--sf-shadow-hover, 0 10px 30px rgba(67,97,238,.16)); }
}
.head { display: flex; align-items: center; gap: 10px; }
.meta { display: flex; flex-direction: column; gap: 2px; }
.author { font-weight: 600; color: var(--sf-text-1, #1f2937); display: flex; align-items: center; gap: 6px; }
.sub { font-size: 12px; color: var(--sf-text-3, #94a3b8); display: flex; gap: 6px; align-items: center; }
.dot { color: var(--sf-text-4, #cbd5e1); }
.title { margin: 10px 0 8px; font-size: 17px; font-weight: 600; color: var(--sf-text-1, #1f2937); }
.body { display: flex; flex-direction: column; gap: 10px; margin-bottom: 10px; }
.abstract { font-size: 13px; color: var(--sf-text-2, #475569); line-height: 1.6; flex: 1; min-width: 0; }
.line-3 { display: -webkit-box; -webkit-box-orient: vertical; -webkit-line-clamp: 3; overflow: hidden; }
.thumb-wrap { width: 100%; max-height: 200px; border-radius: 8px; overflow: hidden; background: var(--sf-bg-soft, #eef2f9); }
.thumb-wrap img { width: 100%; height: 200px; object-fit: cover; }
.tags { display: flex; flex-wrap: wrap; gap: 6px; margin-bottom: 12px; }
.foot { display: flex; gap: 18px; color: var(--sf-text-3, #94a3b8); font-size: 13px; }
.stat { display: inline-flex; align-items: center; gap: 4px; }
.foot .el-icon { font-size: 15px; }
</style>