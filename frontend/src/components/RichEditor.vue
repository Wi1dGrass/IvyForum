<template>
  <MdEditor
    v-model="inner"
    :preview="preview"
    :language="'zh-CN'"
    :toolbars-exclude="['github', 'save', 'pageFullscreen', 'fullscreen', 'htmlPreview', 'catalog']"
    :style="{ height: editorHeight }"
    :preview-theme="'github'"
    :code-theme="'github'"
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
</script>