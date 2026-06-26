<template>
  <div v-loading="loading" class="page">
    <el-tabs v-model="tab">
      <el-tab-pane label="待处理" name="PENDING" />
      <el-tab-pane label="全部" name="ALL" />
    </el-tabs>
    <el-table :data="filtered" class="table sf-card">
      <el-table-column label="ID" prop="reportId" width="70" />
      <el-table-column label="举报人" prop="reporterNickname" width="120" />
      <el-table-column label="对象">
        <template #default="{ row }"><span class="type-tag">{{ targetText(row) }}</span> {{ row.targetTitle || `#${row.targetId}` }}</template>
      </el-table-column>
      <el-table-column label="原因" width="180">
        <template #default="{ row }"><div><b>{{ row.reasonType }}</b></div><div class="grey">{{ row.reasonText }}</div></template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{ row }"><el-tag :type="statusType(row.status)">{{ statusText(row.status) }}</el-tag></template>
      </el-table-column>
      <el-table-column label="操作" width="240">
        <template #default="{ row }">
          <template v-if="row.status === 'PENDING'">
            <el-button size="small" type="success" @click="handle(row, 'PASS')">通过</el-button>
            <el-button size="small" type="warning" @click="handle(row, 'REJECT')">驳回</el-button>
          </template>
          <span v-else class="grey">{{ row.handleRemark || '-' }}</span>
        </template>
      </el-table-column>
    </el-table>
    <Pagination :total="total" :page="page" :size="size" @change="load" />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { reportApi } from '@/api/report'
import Pagination from '@/components/Pagination.vue'
import type { Report, ReportStatus } from '@/types'

const list = ref<Report[]>([])
const total = ref(0); const page = ref(1); const size = 20
const loading = ref(true)
const tab = ref<'PENDING' | 'ALL'>('PENDING')
const filtered = computed(() => tab.value === 'PENDING' ? list.value.filter(r => r.status === 'PENDING') : list.value)

const targetText = (r: Report) => ({ POST: '帖子', COMMENT: '评论', USER: '用户' } as any)[r.targetType]
const statusText = (s: string) => ({ PENDING: '待处理', PASS: '通过', REJECT: '驳回' } as any)[s]
const statusType = (s: string) => ({ PENDING: 'warning', PASS: 'success', REJECT: 'info' } as any)[s]

async function load(p = page.value) {
  page.value = p; loading.value = true
  const r = await reportApi.adminList(p, size)
  list.value = r.records; total.value = r.total; loading.value = false
}
async function handle(row: Report, status: ReportStatus) {
  const { value } = await ElMessageBox.prompt(status === 'PASS' ? '处理备注（可选）' : '驳回说明（可选）', '审核', { inputType: 'textarea', inputValue: '' }).catch(() => ({ value: null as any }))
  if (value === null) return
  await reportApi.handle(row.reportId, { status, handleRemark: value })
  await load(); ElMessage.success('已处理')
}
watch(tab, () => load(1))
onMounted(() => load(1))
</script>

<style scoped lang="scss">
.table { padding: 4px; }
.type-tag { font-size: 12px; padding: 1px 8px; border-radius: 6px; background: var(--sf-bg-soft, #eef2f9); color: var(--sf-text-2, #475569); margin-right: 6px; }
.grey { color: var(--sf-text-3, #94a3b8); font-size: 12px; }
</style>