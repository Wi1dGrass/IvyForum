<template>
  <div v-loading="loading" class="page">
    <div class="toolbar">
      <el-input v-model="kw" placeholder="搜索用户名/昵称" :prefix-icon="Search" clearable style="width: 240px" />
    </div>
    <el-table :data="list" class="table sf-card">
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
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { Search } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { adminUserApi } from '@/api/user'
import type { AdminUserItem } from '@/types'

const allList = ref<AdminUserItem[]>([])
const list = ref<AdminUserItem[]>([])
const loading = ref(true)
const kw = ref('')

const roleText = (r: string) => ({ STUDENT: '学生', TEACHER: '教师', ADMIN: '管理员' } as any)[r]

async function load() {
  loading.value = true
  allList.value = await adminUserApi.list() || []
  applyFilter()
  loading.value = false
}
function applyFilter() {
  const a = allList.value
  if (!kw.value) list.value = a
  else list.value = a.filter(u => u.username.includes(kw.value) || u.nickname.includes(kw.value))
}
async function ban(row: AdminUserItem) { await adminUserApi.ban(row.userId); row.status = 'BANNED'; ElMessage.success('已封禁') }
async function unban(row: AdminUserItem) { await adminUserApi.unban(row.userId); row.status = 'NORMAL'; ElMessage.success('已解封') }
watch(kw, applyFilter)
onMounted(() => load())
</script>

<style scoped lang="scss">
.toolbar { margin-bottom: 14px; }
.table { padding: 4px; }
</style>