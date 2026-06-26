<template>
  <div>
    <div class="head sf-card">
      <el-icon class="ico"><component :is="icon" /></el-icon>
      <div>
        <h2 class="c-title">{{ channelName }}</h2>
        <p class="desc">{{ channel?.description || '该频道下的所有帖子' }}</p>
      </div>
    </div>
    <PostListPanel :channel-id="channelId" :key="channelId" />
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useChannelStore } from '@/stores/channel'
import PostListPanel from '@/components/PostListPanel.vue'

const props = defineProps<{ id: string }>()
const channelId = computed(() => Number(props.id))
const channelStore = useChannelStore()
const channel = computed(() => {
  const walk = (list: any[]): any => { for (const c of list) { if (c.channelId === channelId.value) return c; if (c.children?.length) { const r = walk(c.children); if (r) return r } } return null }
  return walk(channelStore.tree)
})
const channelName = computed(() => channel.value?.name || '频道')
const icon = computed(() => channel.value?.icon || 'Files')
</script>

<style scoped lang="scss">
.head { display: flex; align-items: center; gap: 16px; padding: 20px 22px; margin-bottom: 16px; }
.ico { font-size: 30px; color: var(--sf-primary, #4361ee); }
.c-title { font-size: 22px; font-weight: 700; }
.desc { color: var(--sf-text-3, #94a3b8); margin-top: 4px; font-size: 13px; }
</style>