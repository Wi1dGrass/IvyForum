<template>
  <div v-loading="loading">
    <h2 class="sf-page-title mb-16">我的发帖</h2>
    <PostCard v-for="p in records" :key="p.postId" :post="p" />
    <div v-if="!loading && !records.length" class="sf-empty">你还没有发过帖</div>
    <Pagination :total="total" :page="page" :size="size" @change="load" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watchEffect } from 'vue'
import { userApi } from '@/api/user'
import { useUserStore } from '@/stores/user'
import PostCard from '@/components/PostCard.vue'
import Pagination from '@/components/Pagination.vue'
import type { PostListItem } from '@/types'

const userStore = useUserStore()
const records = ref<PostListItem[]>([])
const total = ref(0); const page = ref(1); const size = 20
const loading = ref(true)
const uid = ref(0)

async function load(p = page.value) {
  page.value = p
  const r = await userApi.posts(uid.value, p, size)
  records.value = r.records; total.value = r.total; loading.value = false
}
watchEffect(() => { if (userStore.user) { uid.value = userStore.user.userId } })
onMounted(() => { if (uid.value) load(1) })
</script>