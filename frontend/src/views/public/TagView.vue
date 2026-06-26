<template>
  <div>
    <div class="head sf-card">
      <h2 class="c-title">标签：#{{ tagName }}</h2>
      <p class="desc">所有标注了此标签的帖子</p>
    </div>
    <PostListPanel :tag-id="tagId" :key="tagId" />
  </div>
</template>

<script setup lang="ts">
import { computed, ref, watchEffect } from 'vue'
import { tagApi } from '@/api/tag'
import PostListPanel from '@/components/PostListPanel.vue'

const props = defineProps<{ id: string }>()
const tagId = computed(() => Number(props.id))
const tagName = ref('')
watchEffect(async () => {
  const tags = await tagApi.list()
  tagName.value = tags.find(t => t.tagId === tagId.value)?.name || ''
})
</script>

<style scoped lang="scss">
.head { padding: 20px 22px; margin-bottom: 16px; }
.c-title { font-size: 22px; font-weight: 700; }
.desc { color: var(--sf-text-3, #94a3b8); margin-top: 4px; font-size: 13px; }
</style>