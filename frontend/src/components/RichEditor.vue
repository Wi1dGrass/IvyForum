<template>
  <MdEditor
    v-model="inner"
    :preview="preview"
    :language="'zh-CN'"
    :toolbars-exclude="['github', 'save', 'pageFullscreen', 'fullscreen', 'htmlPreview', 'catalog']"
    :style="{ height: editorHeight }"
    :preview-theme="'github'"
    :code-theme="'github'"
    :on-upload-img="onUploadImg"
  />
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { MdEditor } from 'md-editor-v3'
import 'md-editor-v3/lib/style.css'

const props = withDefaults(defineProps<{
  modelValue: string
  preview?: boolean
  height?: string
}>(), {
  preview: true,
  height: '420px'
})

const emit = defineEmits<{ 'update:modelValue': [v: string] }>()
const inner = computed({
  get: () => props.modelValue || '',
  set: (v) => emit('update:modelValue', v)
})
const editorHeight = props.height

async function onUploadImg(files: File[], callback: (urls: string[]) => void) {
  const urls: string[] = []
  for (const file of files) {
    const dataUrl = await fileToDataUrl(file)
    urls.push(dataUrl)
  }
  callback(urls)
}

function fileToDataUrl(file: File): Promise<string> {
  return new Promise((resolve, reject) => {
    const reader = new FileReader()
    reader.onload = () => resolve(reader.result as string)
    reader.onerror = reject
    reader.readAsDataURL(file)
  })
}
</script>