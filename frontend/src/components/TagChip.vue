<template>
  <router-link :to="`/tag/${tag.tagId}`" class="tag-chip" :style="cssVars">
    <span class="dot" :style="{ background: tag.color }"></span>
    <span class="name">{{ tag.name }}</span>
  </router-link>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { TagBrief } from '@/types'

const props = defineProps<{ tag: TagBrief }>()
const cssVars = computed(() => ({
  color: props.tag.color,
  '--chip-bg': props.tag.color + '14',
  '--chip-hover': props.tag.color + '22'
}))
</script>

<style scoped lang="scss">
.tag-chip {
  display: inline-flex; align-items: center; gap: 4px;
  padding: 2px 10px; border-radius: var(--sf-radius-pill, 999px);
  background: var(--chip-bg); color: inherit;
  font-size: 12px; line-height: 18px; font-weight: 500;
  cursor: pointer; transition: background .2s ease;
  &:hover { background: var(--chip-hover); }
  .dot { width: 6px; height: 6px; border-radius: 50%; flex: none; }
}
</style>