<template>
  <div class="admin-layout">
    <aside class="aside">
      <router-link to="/" class="brand">
        <span class="logo">校</span><span>校园论坛 · 管理后台</span>
      </router-link>
      <nav class="menu">
        <router-link v-for="m in menus" :key="m.path" :to="m.path" class="menu-item" :class="{ active: route.path === m.path }">
          <el-icon><component :is="m.icon" /></el-icon><span>{{ m.title }}</span>
        </router-link>
      </nav>
    </aside>
    <main class="main">
      <header class="topbar">
        <div class="title">{{ currentTitle }}</div>
        <div class="right">
          <el-button text @click="router.push('/')">回到前台</el-button>
          <el-avatar :size="32" :src="user?.avatar || ''">{{ user?.nickname?.[0] }}</el-avatar>
        </div>
      </header>
      <section class="content">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in"><component :is="Component" /></transition>
        </router-view>
      </section>
    </main>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { User, Files, PriceTag, Warning, Document } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const user = computed(() => userStore.user)

const menus = [
  { path: '/admin/users', title: '用户管理', icon: User },
  { path: '/admin/channels', title: '频道管理', icon: Files },
  { path: '/admin/tags', title: '标签管理', icon: PriceTag },
  { path: '/admin/reports', title: '举报审核', icon: Warning },
  { path: '/admin/posts', title: '帖子管理', icon: Document }
]
const currentTitle = computed(() => menus.find(m => route.path === m.path)?.title || '管理后台')
</script>

<style scoped lang="scss">
.admin-layout { display: flex; min-height: 100vh; }
.aside { width: 220px; flex: none; background: linear-gradient(180deg, #1e293b 0%, #334155 100%); color: #fff; padding: 20px 0; position: sticky; top: 0; height: 100vh; }
.brand { display: flex; align-items: center; gap: 10px; padding: 0 20px 20px; color: #fff; font-weight: 700; border-bottom: 1px solid rgba(255,255,255,.08); }
.logo { width: 32px; height: 32px; border-radius: 8px; background: var(--sf-gradient); display: flex; align-items: center; justify-content: center; }
.menu { padding: 16px 12px; display: flex; flex-direction: column; gap: 4px; }
.menu-item { display: flex; align-items: center; gap: 10px; padding: 10px 14px; border-radius: 8px; color: rgba(255,255,255,.75); font-size: 14px;
  &:hover { background: rgba(255,255,255,.08); color: #fff; }
  &.active { background: var(--sf-primary); color: #fff; }
}
.main { flex: 1; display: flex; flex-direction: column; background: var(--sf-bg, #f4f7fb); }
.topbar { height: 60px; background: #fff; border-bottom: 1px solid var(--sf-border); display: flex; align-items: center; justify-content: space-between; padding: 0 24px; }
.title { font-size: 18px; font-weight: 700; }
.right { display: flex; align-items: center; gap: 12px; }
.content { padding: 24px; }
</style>