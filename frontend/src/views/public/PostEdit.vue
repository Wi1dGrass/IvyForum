<template>
  <div class="edit">
    <div class="head sf-card">
      <h2 class="c-title">{{ isEdit ? '编辑帖子' : '发布新帖' }}</h2>
      <p class="desc">选择合适频道，并可贴最多 5 个标签</p>
    </div>

    <el-form ref="formRef" :model="form" :rules="rules" label-position="top" class="form sf-card" v-loading="loading">
      <el-form-item label="频道" prop="channelId">
        <el-cascader
          v-model="channelPath" :options="cascaderOptions" :props="{ checkStrictly: true }" placeholder="选择频道"
          clearable style="width: 280px" @change="onChannelChange" />
      </el-form-item>

      <el-form-item label="标题" prop="title">
        <el-input v-model="form.title" maxlength="128" show-word-limit placeholder="一句话概括你的帖子" />
      </el-form-item>

      <el-form-item label="标签（最多 5 个）" prop="tagIds">
        <el-select v-model="form.tagIds" multiple :multiple-limit="5" placeholder="选择标签" style="width: 100%">
          <el-option v-for="t in tags" :key="t.tagId" :label="t.name" :value="t.tagId">
            <span class="tag-dot" :style="{ background: t.color }"></span>{{ t.name }}
          </el-option>
        </el-select>
      </el-form-item>

      <el-form-item label="正文（Markdown）" prop="content">
        <RichEditor v-model="form.content" :height="'460px'" />
      </el-form-item>

      <div class="footer">
        <el-button @click="$router.back()">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submit">发布</el-button>
      </div>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { useChannelStore } from '@/stores/channel'
import { tagApi } from '@/api/tag'
import { postApi } from '@/api/post'
import RichEditor from '@/components/RichEditor.vue'
import type { Tag, Channel, PostSaveRequest } from '@/types'

const props = defineProps<{ id?: string }>()
const router = useRouter()
const isEdit = computed(() => !!props.id)

const channelStore = useChannelStore()
const tags = ref<Tag[]>([])
const loading = ref(false)
const submitting = ref(false)
const channelPath = ref<number[]>([])

const form = reactive<PostSaveRequest>({ channelId: 0, title: '', content: '', tagIds: [] })
const rules: FormRules = {
  channelId: [{ required: true, message: '请选择频道', trigger: 'change' }],
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }, { min: 4, message: '标题至少 4 个字', trigger: 'blur' }],
  content: [{ required: true, message: '请输入正文', trigger: 'blur' }]
}
const formRef = ref<FormInstance>()

const cascaderOptions = computed(() =>
  channelStore.tree.map(c => ({ value: c.channelId, label: c.name, children: (c.children || []).map(s => ({ value: s.channelId, label: s.name })) }))
)
function onChannelChange(path: number[]) { if (path?.length) form.channelId = path[path.length - 1] }

async function submit() {
  await formRef.value?.validate()
  submitting.value = true
  try {
    if (isEdit.value) {
      await postApi.update(Number(props.id), form)
      ElMessage.success('修改成功'); router.push(`/post/${props.id}`)
    } else {
      const r = await postApi.create(form)
      ElMessage.success('发布成功'); router.push(`/post/${r.postId}`)
    }
  } finally { submitting.value = false }
}

onMounted(async () => {
  loading.value = true
  await Promise.all([channelStore.load(), tagApi.list().then(t => tags.value = t)])
  if (isEdit.value) {
    const detail = await postApi.detail(Number(props.id))
    form.channelId = detail.channelId; form.title = detail.title
    form.content = detail.content; form.tagIds = detail.tags.map(t => t.tagId)
    channelPath.value = [detail.channelId]
  }
  loading.value = false
})
</script>

<style scoped lang="scss">
.edit { display: flex; flex-direction: column; gap: 16px; }
.head { padding: 20px 22px; }
.c-title { font-size: 22px; font-weight: 700; }
.desc { color: var(--sf-text-3, #94a3b8); margin-top: 4px; font-size: 13px; }
.form { padding: 22px 24px; }
.footer { display: flex; justify-content: flex-end; gap: 10px; }
.tag-dot { display: inline-block; width: 8px; height: 8px; border-radius: 50%; margin-right: 6px; }
</style>