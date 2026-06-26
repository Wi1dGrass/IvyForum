<template>
  <div class="panel">
    <div class="bar sf-card">
      <div class="left">
        <span class="sort" :class="{ active: sort === 'latest' }" @click="setSort('latest')">最新</span>
        <span class="sort" :class="{ active: sort === 'hot' }" @click="setSort('hot')">最热</span>
      </div>
      <div class="right">
        <span class="filter-label">标签筛选：</span>
        <div class="tags">
          <TagChip v-for="t in tags" :key="t.tagId" :tag="t" @click.prevent="toggleTag(t.tagId)"
            :class="['tag-clip', selectedTag === t.tagId ? 'on' : '']" />
        </div>
        <el-button v-if="selectedTag" text size="small" @click="selectedTag = null">清除</el-button>
      </div>
    </div>

    <div v-loading="loading">
      <PostCard v-for="p in records" :key="p.postId" :post="p" />
      <div v-if="!loading && !records.length" class="sf-empty">暂无帖子，快来发布第一篇吧～</div>
    </div>

    <Pagination :total="total" :page="page" :size="size" @change="onPage" />
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onMounted } from 'vue'
import { postApi } from '@/api/post'
import { tagApi } from '@/api/tag'
import { useChannelStore } from '@/stores/channel'
import TagChip from '@/components/TagChip.vue'
import PostCard from '@/components/PostCard.vue'
import Pagination from '@/components/Pagination.vue'
import type { PostListItem, PostSort, Tag } from '@/types'

const props = defineProps<{
  channelId?: number
  tagId?: number
  keyword?: string
  title?: string
}>()

const channelStore = useChannelStore()
const records = ref<PostListItem[]>([])
const total = ref(0)
const page = ref(1)
const size = 20
const sort = ref<PostSort>('latest')
const tags = ref<Tag[]>([])
const selectedTag = ref<number | null>(null)
const loading = ref(false)

async function loadTags() { tags.value = await tagApi.list() }

async function loadList() {
  loading.value = true
  try {
    const q: any = { page: page.value, size, sort: sort.value }
    if (props.channelId) q.channelId = props.channelId
    const tagIds: number[] = []
    if (props.tagId) tagIds.push(props.tagId)
    if (selectedTag.value) tagIds.push(selectedTag.value)
    if (tagIds.length) q.tagIds = tagIds
    if (props.keyword) q.keyword = props.keyword
    const r = await postApi.list(q)
    records.value = r.records
    total.value = r.total
  } finally { loading.value = false }
}

function setSort(s: PostSort) { if (s === sort.value) return; sort.value = s; page.value = 1; loadList() }
function toggleTag(id: number) { selectedTag.value = selectedTag.value === id ? null : id; page.value = 1 }
function onPage(p: number) { page.value = p; loadList() }

watch(() => props.channelId, () => { page.value = 1; loadList() })
watch(() => props.tagId, () => { page.value = 1; loadList() })
watch(() => props.keyword, () => { page.value = 1; loadList() })

onMounted(async () => {
  await Promise.all([channelStore.load(), loadTags()])
  loadList()
})
</script>

<style scoped lang="scss" xmlns="http://www.w3.org/1999/html">
.panel { display: flex; flex-direction: column; }
.bar { display: flex; align-items: center; justify-content: space-between; padding: 12px 18px; margin-bottom: 16px; flex-wrap: wrap; gap: 10px; }
.left { display: flex; gap: 6px; }
.sort { padding: 4px 14px; border-radius: 999px; cursor: pointer; font-size: 13px; color: var(--sf-text-2, #475569); background: var(--sf-bg-soft, #eef2f9);
  &.active { background: var(--sf-primary, #4361ee); color: #fff; font-weight: 600; }
}
.right { display: flex; align-items: center; gap: 8px; flex-wrap: wrap; }
.filter-label { font-size: 12px; color: var(--sf-text-3, #94a3b8); }
.tags { display: flex; gap: 6px; flex-wrap: wrap; }
.tag-clip { cursor: pointer; }
.tag-clip.on { box-shadow: 0 0 0 2px var(--sf-primary, #4361ee); }
</style>