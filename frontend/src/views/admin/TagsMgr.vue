<template>
  <div v-loading="loading" class="page">
    <div class="toolbar">
      <el-button type="primary" :icon="Plus" @click="open()">新增标签</el-button>
    </div>
    <el-table :data="list" class="table sf-card">
      <el-table-column label="ID" prop="tagId" width="80" />
      <el-table-column label="名称" width="180">
        <template #default="{ row }"><span class="dot" :style="{ background: row.color }"></span>{{ row.name }}</template>
      </el-table-column>
      <el-table-column label="颜色" width="120">
        <template #default="{ row }"><el-color-picker v-model="row.color" size="small" disabled /></template>
      </el-table-column>
      <el-table-column label="引用数" prop="refCount" width="100" />
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button size="small" @click="open(row)">编辑</el-button>
          <el-button size="small" type="danger" plain @click="del(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dlg" :title="form.tagId ? '编辑标签' : '新增标签'" width="380px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="颜色"><el-color-picker v-model="form.color" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dlg = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { tagApi } from '@/api/tag'
import type { Tag } from '@/types'

const list = ref<Tag[]>([])
const loading = ref(true); const dlg = ref(false); const saving = ref(false)
const empty = () => ({ tagId: 0, name: '', color: '#4361ee' })
const form = reactive(empty())

async function load() { loading.value = true; list.value = await tagApi.list(); loading.value = false }
function open(row?: Tag) { Object.assign(form, row ? { ...row } : empty()); dlg.value = true }
async function save() {
  saving.value = true
  try {
    if (form.tagId) await tagApi.update(form.tagId, form); else await tagApi.create(form)
    await load(); ElMessage.success('保存成功'); dlg.value = false
  } finally { saving.value = false }
}
async function del(row: Tag) {
  await ElMessageBox.confirm(`删除标签「${row.name}」？`, '提示', { type: 'warning' })
  await tagApi.remove(row.tagId); await load(); ElMessage.success('已删除')
}
onMounted(load)
</script>

<style scoped lang="scss">
.toolbar { margin-bottom: 14px; }
.table { padding: 4px; }
.dot { display: inline-block; width: 8px; height: 8px; border-radius: 50%; margin-right: 8px; }
</style>