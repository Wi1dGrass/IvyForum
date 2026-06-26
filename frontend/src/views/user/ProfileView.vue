<template>
  <div v-loading="loading" class="profile">
    <div class="cover sf-card">
      <div class="bg"></div>
      <div class="info">
        <el-avatar :size="80" :src="user?.avatar || ''" class="avatar">{{ user?.nickname?.[0] }}</el-avatar>
        <div class="main">
          <div class="name">
            {{ user?.nickname }}
            <span class="sf-badge" :class="`role-${(user?.role || 'STUDENT').toLowerCase()}`">{{ roleText }}</span>
          </div>
          <div class="sign">{{ user?.signature || '这个人很神秘，什么都没写' }}</div>
        </div>
        <div class="actions">
          <el-button v-if="isMe" type="primary" plain @click="$router.push('/profile')">编辑资料</el-button>
        </div>
      </div>
      <div class="stats">
        <div class="stat"><span>{{ stats.postCount }}</span><em>发帖</em></div>
        <div class="stat"><span>{{ stats.likeCount }}</span><em>获赞</em></div>
        <div class="stat"><span>{{ stats.collectCount }}</span><em>收藏</em></div>
      </div>
    </div>

    <el-tabs v-model="tab" class="tabs">
      <el-tab-pane label="TA 的发帖" name="posts" />
    </el-tabs>

    <div v-if="tab === 'posts'" v-loading="postsLoading">
      <PostCard v-for="p in posts" :key="p.postId" :post="p" />
      <div v-if="!postsLoading && !posts.length" class="sf-empty">暂无发帖</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { userApi } from '@/api/user'
import { useUserStore } from '@/stores/user'
import PostCard from '@/components/PostCard.vue'
import type { PostListItem } from '@/types'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const userId = computed(() => Number(route.params.id))
const user = ref<any>(null)
const loading = ref(true)
const tab = ref('posts')
const stats = ref({ postCount: 0, likeCount: 0, collectCount: 0 })
const posts = ref<PostListItem[]>([])
const postsLoading = ref(false)
const roleText = computed(() => ({ STUDENT: '同学', TEACHER: '老师', ADMIN: '管理员' } as any)[user.value?.role] || '同学')
const isMe = computed(() => userStore.user?.userId === userId.value)

function goUser(id: number) { router.push(`/user/${id}`) }

async function load() {
  loading.value = true; user.value = await userApi.profile(userId.value)
  stats.value = { postCount: user.value.postCount || 0, likeCount: user.value.likeCount || 0, collectCount: user.value.collectCount || 0 }
  loading.value = false
  loadPosts()
}
async function loadPosts() {
  postsLoading.value = true
  posts.value = await userApi.posts(userId.value) || []
  postsLoading.value = false
}
watch(() => route.params.id, load, { immediate: true })
</script>

<style scoped lang="scss">
.cover { position: relative; overflow: hidden; padding: 0 0 26px; border: none; }
.bg { height: 120px; background: var(--sf-gradient); }
.info { display: flex; align-items: flex-end; gap: 18px; padding: 0 32px; margin-top: -40px; position: relative; }
.avatar { border: 4px solid #fff; box-shadow: var(--sf-shadow-1); }
.main { flex: 1; padding-bottom: 8px; }
.name { font-size: 22px; font-weight: 700; display: flex; align-items: center; gap: 8px; }
.sign { color: var(--sf-text-3, #94a3b8); font-size: 13px; margin-top: 6px; }
.stats { display: flex; gap: 36px; padding: 16px 32px 0; }
.stat { display: flex; flex-direction: column; align-items: center; span { font-size: 20px; font-weight: 700; } em { font-style: normal; font-size: 12px; color: var(--sf-text-3, #94a3b8); margin-top: 2px; } }
.tabs { background: var(--sf-card, #fff); border-radius: var(--sf-radius, 12px); padding: 0 16px; margin-top: 16px; }
</style>