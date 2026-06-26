<template>
  <el-pagination
    v-if="total > size"
    background
    layout="prev, pager, next"
    :total="total"
    :page-size="size"
    :current-page="page"
    :pager-count="7"
    @current-change="onChange"
  />
</template>

<script setup lang="ts">
const props = defineProps<{ total: number; page: number; size: number }>()
const emit = defineEmits<{ change: [p: number] }>()
function onChange(p: number) { emit('change', p) }
</script>
<style scoped>
.el-pagination { justify-content: center; margin-top: 24px; }
</style>