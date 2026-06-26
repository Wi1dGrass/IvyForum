<template>
  <div v-loading="loading">
    <h2 class="sf-page-title mb-16">我的发帖</h2>
    <PostCard v-for="p in records" :key="p.postId" :post="p" />
    <div v-if="!loading && !records.length" class="sf-empty">你还没有发过帖</div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watchEffect } from 'vue'
import { userApi } from '@/api/user'
import { useUserStore } from '@/stores/user'
import PostCard from '@/components/PostCard.vue'
import type { PostListItem } from '@/types'

const userStore = useUserStore()
const records = ref<PostListItem[]>([])
const loading = ref(true)
const uid = ref(0)

async function load() {
  loading.value = true
  records.value = await userApi.posts(uid.value) || []
  loading.value = false
}
watchEffect(() => { if (userStore.user) { uid.value = userStore.user.userId; if (uid.value) load() } })
onMounted(() => { if (uid.value) load() })
</script>