<template>
  <div v-loading="loading" class="page">
    <div class="toolbar">
      <el-input v-model="kw" placeholder="搜索用户名/昵称" :prefix-icon="Search" clearable style="width: 240px" />
    </div>
    <el-table :data="filtered" class="table sf-card">
      <el-table-column label="ID" prop="userId" width="80" />
      <el-table-column label="用户名" prop="username" width="140" />
      <el-table-column label="昵称" prop="nickname" width="140" />
      <el-table-column label="角色" width="100">
        <template #default="{ row }"><span class="sf-badge" :class="`role-${row.role.toLowerCase()}`">{{ roleText(row.role) }}</span></template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{ row }"><el-tag :type="row.status === 'NORMAL' ? 'success' : 'danger'">{{ row.status === 'NORMAL' ? '正常' : '封禁' }}</el-tag></template>
      </el-table-column>
      <el-table-column label="发帖数" prop="postCount" width="90" />
      <el-table-column label="操作" width="160">
        <template #default="{ row }">
          <el-button v-if="row.status === 'NORMAL'" size="small" type="danger" plain @click="ban(row)">封禁</el-button>
          <el-button v-else size="small" type="success" plain @click="unban(row)">解封</el-button>
        </template>
      </el-table-column>
    </el-table>
    <Pagination :total="total" :page="page" :size="size" @change="load" />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { Search } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { adminUserApi } from '@/api/user'
import Pagination from '@/components/Pagination.vue'
import type { AdminUserItem } from '@/types'

const list = ref<AdminUserItem[]>([])
const total = ref(0); const page = ref(1); const size = 20
const loading = ref(true)
const kw = ref('')
const filtered = computed(() => list.value.filter(u => !kw.value || u.username.includes(kw.value) || u.nickname.includes(kw.value)))

const roleText = (r: string) => ({ STUDENT: '学生', TEACHER: '教师', ADMIN: '管理员' } as any)[r]

async function load(p = page.value) {
  page.value = p; loading.value = true
  const r = await adminUserApi.list(p, size)
  list.value = r.records; total.value = r.total; loading.value = false
}
async function ban(row: AdminUserItem) { await adminUserApi.ban(row.userId); row.status = 'BANNED'; ElMessage.success('已封禁') }
async function unban(row: AdminUserItem) { await adminUserApi.unban(row.userId); row.status = 'NORMAL'; ElMessage.success('已解封') }
onMounted(() => load(1))
</script>

<style scoped lang="scss">
.toolbar { margin-bottom: 14px; }
.table { padding: 4px; }
</style>