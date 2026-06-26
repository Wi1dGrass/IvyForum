<template>
  <div v-loading="loading" class="page">
    <div class="toolbar">
      <el-input v-model="kw" placeholder="搜索标题" :prefix-icon="Search" clearable style="width: 240px" />
      <el-select v-model="chFilter" placeholder="全部频道" clearable style="width: 180px">
        <el-option v-for="c in channels" :key="c.channelId" :label="c.name" :value="c.channelId" />
      </el-select>
    </div>
    <el-table :data="filtered" class="table sf-card">
      <el-table-column label="ID" prop="postId" width="70" />
      <el-table-column label="标题" min-width="240">
        <template #default="{ row }"><span v-if="row.isTop" class="sf-badge top mr-8">置顶</span><span v-if="row.isEssence" class="sf-badge essence mr-8">精</span>{{ row.title }}</template>
      </el-table-column>
      <el-table-column label="作者" prop="authorNickname" width="120" />
      <el-table-column label="频道" prop="channelName" width="120" />
      <el-table-column label="浏览/赞/评" width="140">
        <template #default="{ row }">{{ row.viewCount }} / {{ row.likeCount }} / {{ row.commentCount }}</template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{ row }"><el-tag :type="row.status === 'NORMAL' ? 'success' : 'info'">{{ statusText(row.status) }}</el-tag></template>
      </el-table-column>
      <el-table-column label="操作" width="280">
        <template #default="{ row }">
          <el-button size="small" @click="toggleTop(row)">{{ row.isTop ? '取消置顶' : '置顶' }}</el-button>
          <el-button size="small" @click="toggleEssence(row)">{{ row.isEssence ? '取消加精' : '加精' }}</el-button>
          <el-button size="small" type="danger" plain @click="del(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <Pagination :total="total" :page="page" :size="size" @change="load" />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { Search } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { postApi } from '@/api/post'
import { useChannelStore } from '@/stores/channel'
import Pagination from '@/components/Pagination.vue'
import type { PostListItem } from '@/types'

const channelStore = useChannelStore()
const channels = computed(() => channelStore.tree)
const list = ref<PostListItem[]>([])
const total = ref(0); const page = ref(1); const size = 20
const loading = ref(true); const kw = ref(''); const chFilter = ref<number | ''>('')
const statusText = (s: string) => ({ NORMAL: '正常', PENDING: '待审', REJECTED: '驳回', DELETED: '已删' } as any)[s]

const filtered = computed(() => list.value.filter(p =>
  (!kw.value || p.title.includes(kw.value)) && (!chFilter.value || p.channelId === chFilter.value)))
const filteredTotal = computed(() => total.value)

async function load(p = page.value) {
  page.value = p; loading.value = true
  const r = await postApi.list({ page: p, size: 1000, sort: 'latest' })
  list.value = r.records as any; total.value = r.total; loading.value = false
}
async function toggleTop(row: any) { const r = await postApi.toggleTop(row.postId); row.isTop = r.isTop }
async function toggleEssence(row: any) { const r = await postApi.toggleEssence(row.postId); row.isEssence = r.isEssence }
async function del(row: any) {
  await ElMessageBox.confirm(`删除帖子「${row.title}」？`, '提示', { type: 'warning' })
  await postApi.remove(row.postId); await load(); ElMessage.success('已删除')
}
onMounted(async () => { await channelStore.load(); load(1) })
</script>

<style scoped lang="scss">
.toolbar { display: flex; gap: 12px; margin-bottom: 14px; }
.table { padding: 4px; }
.mr-8 { margin-right: 8px; }
</style>