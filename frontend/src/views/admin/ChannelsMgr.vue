<template>
  <div v-loading="loading" class="page">
    <div class="toolbar">
      <el-button type="primary" :icon="Plus" @click="open()">新增频道</el-button>
    </div>
    <el-table :data="flat" class="table sf-card" row-key="channelId" :tree-props="{ children: 'children' }">
      <el-table-column label="名称" prop="name" />
      <el-table-column label="父频道" width="140">
        <template #default="{ row }">{{ row.parentId ? byId(row.parentId)?.name || '-' : '顶级' }}</template>
      </el-table-column>
      <el-table-column label="排序" prop="sort" width="80" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }"><el-tag :type="row.status === 'NORMAL' ? 'success' : 'info'">{{ row.status === 'NORMAL' ? '正常' : '禁用' }}</el-tag></template>
      </el-table-column>
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button size="small" @click="open(row)">编辑</el-button>
          <el-button size="small" type="danger" plain @click="del(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dlg" :title="form.channelId ? '编辑频道' : '新增频道'" width="460px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="父频道">
          <el-select v-model="form.parentId" clearable placeholder="顶级频道" style="width: 100%">
            <el-option v-for="c in flat.filter(x => x.channelId !== form.channelId)" :key="c.channelId" :label="c.name" :value="c.channelId" />
          </el-select>
        </el-form-item>
        <el-form-item label="排序"><el-input-number v-model="form.sort" :min="0" /></el-form-item>
        <el-form-item label="图标"><el-input v-model="form.icon" placeholder="Element Plus 图标组件名" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" type="textarea" :rows="2" /></el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio value="NORMAL">正常</el-radio>
            <el-radio value="DISABLED">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dlg = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { channelApi } from '@/api/channel'
import type { Channel } from '@/types'

const tree = ref<Channel[]>([])
const flat = computed(() => flatten(tree.value))
const byId = (id: number) => flat.value.find(c => c.channelId === id)
const loading = ref(false)
const dlg = ref(false); const saving = ref(false)
const empty = (): any => ({ channelId: 0, parentId: 0, name: '', sort: 0, icon: '', description: '', status: 'NORMAL' })
const form = reactive(empty())

async function loadTree() { tree.value = await channelApi.treeAll() }
function flatten(list: Channel[]): any[] {
  const r: any[] = []
  const walk = (xs: Channel[]) => xs.forEach(c => { r.push(c); if (c.children?.length) walk(c.children) })
  walk(list); return r
}
function open(row?: Channel) {
  Object.assign(form, row ? { ...row, parentId: row.parentId || 0 } : empty())
  dlg.value = true
}
async function save() {
  saving.value = true
  try {
    if (form.channelId) await channelApi.update(form.channelId, form)
    else await channelApi.create(form)
    await loadTree()
    ElMessage.success('保存成功'); dlg.value = false
  } finally { saving.value = false }
}
async function del(row: Channel) {
  await ElMessageBox.confirm(`删除频道「${row.name}」？`, '提示', { type: 'warning' })
  await channelApi.remove(row.channelId); await loadTree(); ElMessage.success('已删除')
}
onMounted(async () => { loading.value = true; await loadTree(); loading.value = false })
</script>

<style scoped>
.toolbar { margin-bottom: 14px; }
.table { padding: 4px; }
</style>