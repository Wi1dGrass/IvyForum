<template>
  <div class="layout">
    <header class="header">
      <div class="bar container">
        <router-link to="/" class="brand">
          <span class="logo">校</span>
          <span class="name">校园论坛</span>
        </router-link>

        <div class="search">
          <el-input v-model="kw" placeholder="搜索帖子标题或正文…" :prefix-icon="Search" clearable @keyup.enter="goSearch" class="search-input" />
        </div>

        <div class="actions">
          <el-button v-if="isLoggedIn" type="primary" :icon="EditPen" @click="router.push('/post/new')">发帖</el-button>

          <template v-if="isLoggedIn">
            <router-link to="/notifications" class="bell">
              <el-badge :value="unread || ''" :hidden="!unread" :max="99" type="danger">
                <el-icon :size="22"><Bell /></el-icon>
              </el-badge>
            </router-link>

            <el-dropdown trigger="click" @command="onCmd">
              <div class="user">
                <el-avatar :size="34" :src="user?.avatar || ''">{{ user?.nickname?.[0] }}</el-avatar>
                <span class="uname">{{ user?.nickname }}</span>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item :command="`/user/${user?.userId}`">个人主页</el-dropdown-item>
                  <el-dropdown-item command="/profile">编辑资料</el-dropdown-item>
                  <el-dropdown-item command="/my/posts">我的发帖</el-dropdown-item>
                  <el-dropdown-item command="/my/collects">我的收藏</el-dropdown-item>
                  <el-dropdown-item v-if="isAdmin" divided command="/admin">管理后台</el-dropdown-item>
                  <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <el-button text @click="router.push('/login')">登录</el-button>
            <el-button type="primary" plain @click="router.push('/register')">注册</el-button>
          </template>
        </div>
      </div>
    </header>

    <div class="body container">
      <aside class="sidebar">
        <div class="sidebar-inner sf-card"><ChannelNav /></div>
      </aside>
      <main class="content">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Search, EditPen, Bell } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { useNotificationStore } from '@/stores/notification'
import { useChannelStore } from '@/stores/channel'
import ChannelNav from '@/components/ChannelNav.vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const notifyStore = useNotificationStore()
const channelStore = useChannelStore()

const user = computed(() => userStore.user)
const isLoggedIn = computed(() => userStore.isLoggedIn)
const isAdmin = computed(() => userStore.isAdmin)
const unread = computed(() => notifyStore.unreadCount)

const kw = ref((route.name === 'search' ? route.query.keyword as string : '') || '')
function goSearch() { if (kw.value.trim()) router.push({ name: 'search', query: { keyword: kw.value.trim() } }) }

function onCmd(cmd: string) {
  if (cmd === 'logout') { userStore.logout(); router.push('/'); return }
  router.push(cmd)
}

onMounted(async () => {
  await channelStore.load()
  if (userStore.isLoggedIn) {
    await userStore.fetchMe()
    notifyStore.startPoll()
  }
})
onUnmounted(() => notifyStore.stopPoll())
</script>

<style scoped lang="scss" xmlns="http://www.w3.org/1999/html">
.layout { min-height: 100%; }
.header {
  position: sticky; top: 0; z-index: 50; height: var(--sf-header-h, 64px);
  background: rgba(255,255,255,.85); backdrop-filter: blur(10px);
  border-bottom: 1px solid var(--sf-border, #e6eaf3);
}
.bar { height: 100%; display: flex; align-items: center; gap: 20px; }
.brand { display: flex; align-items: center; gap: 10px; flex: none; }
.logo { width: 36px; height: 36px; border-radius: 10px; background: var(--sf-gradient, linear-gradient(135deg,#4361ee,#6c8aff)); color: #fff; display: flex; align-items: center; justify-content: center; font-weight: 700; }
.name { font-size: 18px; font-weight: 700; color: var(--sf-text-1, #1f2937); }
.search { flex: 1; max-width: 520px; }
:deep(.search-input .el-input__wrapper) { border-radius: 999px; background: var(--sf-bg-soft, #eef2f9); box-shadow: none; }
.actions { display: flex; align-items: center; gap: 14px; margin-left: auto; }
.bell { display: flex; color: var(--sf-text-2, #475569); &:hover { color: var(--sf-primary, #4361ee); } }
.user { display: flex; align-items: center; gap: 8px; cursor: pointer; }
.uname { font-size: 13px; font-weight: 600; color: var(--sf-text-1, #1f2937); }

.body { display: flex; gap: 20px; padding: 20px; align-items: flex-start; }
.sidebar { width: var(--sf-sidebar-w, 240px); flex: none; position: sticky; top: calc(var(--sf-header-h, 64px) + 20px); }
.sidebar-inner { position: sticky; top: 0; }
.content { flex: 1; min-width: 0; }

@media (max-width: 900px) {
  .sidebar { display: none; }
  .body { padding: 12px; }
  .name { display: none; }
}
</style>