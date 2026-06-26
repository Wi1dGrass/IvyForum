<template>
  <div class="channel-nav">
    <div class="nav-section">
      <div class="search">
        <el-input v-model="kw" placeholder="搜索论坛…" :prefix-icon="Search" clearable @keyup.enter="goSearch" />
      </div>
      <router-link to="/" class="nav-link" :class="{ active: route.path === '/' }">首页</router-link>
      <router-link to="/hot" class="nav-link" :class="{ active: route.path === '/hot' }">🔥 热门榜</router-link>
    </div>

    <div class="nav-group">
      <div class="group-title">频道</div>
      <template v-for="c in tree" :key="c.channelId">
        <router-link :to="`/channel/${c.channelId}`" class="nav-link parent" :class="{ active: route.params.id === String(c.channelId) }">
          <el-icon v-if="c.icon"><component :is="c.icon" /></el-icon>
          <span>{{ c.name }}</span>
        </router-link>
        <router-link
          v-for="sub in c.children"
          :key="sub.channelId"
          :to="`/channel/${sub.channelId}`"
          class="nav-link child"
          :class="{ active: route.params.id === String(sub.channelId) }"
        >{{ sub.name }}</router-link>
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Search } from '@element-plus/icons-vue'
import { useChannelStore } from '@/stores/channel'

const route = useRoute()
const router = useRouter()
const store = useChannelStore()
const tree = computed(() => store.tree)

const kw = ref<string>(route.name === 'search' ? String(route.query.keyword || '') : '')
function goSearch() {
  if (!kw.value.trim()) return
  router.push({ name: 'search', query: { keyword: kw.value.trim() } })
}
</script>

<style scoped lang="scss">
.channel-nav { padding: 16px 12px; }
.search { margin-bottom: 12px; }
.nav-section .nav-link { display: block; padding: 8px 12px; margin-bottom: 4px; border-radius: 8px; font-size: 14px; color: var(--sf-text-2, #475569); font-weight: 500;
  &:hover { background: var(--sf-bg-soft, #eef2f9); color: var(--sf-primary, #4361ee); }
  &.active { background: var(--sf-primary, #4361ee); color: #fff; }
}
.nav-group { margin-top: 18px; }
.group-title { font-size: 12px; color: var(--sf-text-3, #94a3b8); padding: 0 12px 8px; font-weight: 600; letter-spacing: .04em; }
.nav-link.parent { display: flex; align-items: center; gap: 8px; padding: 7px 12px; border-radius: 8px; font-size: 14px; color: var(--sf-text-2, #475569); margin-bottom: 2px;
  &:hover { background: var(--sf-bg-soft, #eef2f9); }
  &.active { background: rgba(67,97,238,.1); color: var(--sf-primary, #4361ee); font-weight: 600; }
}
.nav-link.child { padding: 6px 12px 6px 32px; border-radius: 8px; font-size: 13px; color: var(--sf-text-3, #94a3b8); margin-bottom: 2px; display: block;
  &:hover { background: var(--sf-bg-soft, #eef2f9); color: var(--sf-primary); }
  &.active { color: var(--sf-primary, #4361ee); font-weight: 600; background: rgba(67,97,238,.08); }
}
</style>