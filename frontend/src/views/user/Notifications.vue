<template>
  <div v-loading="loading">
    <div class="bar sf-card">
      <h2 class="sf-page-title">通知中心</h2>
      <el-button type="primary" plain size="small" @click="readAll">全部已读</el-button>
    </div>

    <div class="list">
      <div class="item sf-card" v-for="n in records" :key="n.notifyId" :class="{ unread: !n.isRead }">
        <el-avatar :size="36" :src="n.senderAvatar || ''">{{ n.senderNickname?.[0] }}</el-avatar>
        <div class="body">
          <div class="line">
            <span class="who">{{ n.senderNickname }}</span>
            <span class="type-tag" :class="typeClass(n.type)">{{ typeText(n.type) }}</span>
            <span class="time">{{ formatTime(n.createTime) }}</span>
          </div>
          <div class="content">{{ n.content }}</div>
        </div>
        <el-button v-if="!n.isRead" text size="small" @click="readOne(n)">已读</el-button>
      </div>
      <div v-if="!loading && !records.length" class="sf-empty">暂无通知</div>
    </div>

    <Pagination :total="total" :page="page" :size="size" @change="load" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { notificationApi } from '@/api/notification'
import { useNotificationStore } from '@/stores/notification'
import { formatTime } from '@/utils/date'
import Pagination from '@/components/Pagination.vue'
import type { Notification, NotifyType } from '@/types'

const notifyStore = useNotificationStore()
const records = ref<Notification[]>([])
const total = ref(0); const page = ref(1); const size = 20
const loading = ref(true)

async function load(p = page.value) {
  page.value = p; loading.value = true
  const r = await notificationApi.list(p, size)
  records.value = r.records; total.value = r.total; loading.value = false
  notifyStore.fetchUnread()
}
const typeText = (t: NotifyType) => ({ COMMENT: '评论', REPLY: '回复', MENTION: '@提及', AUDIT: '审核' } as any)[t]
const typeClass = (t: NotifyType) => ({ COMMENT: 'c1', REPLY: 'c2', MENTION: 'c3', AUDIT: 'c4' } as any)[t]

async function readOne(n: Notification) { await notificationApi.read(n.notifyId); n.isRead = 1; notifyStore.fetchUnread() }
async function readAll() { await notificationApi.read(); records.value.forEach(n => n.isRead = 1); notifyStore.fetchUnread(); ElMessage.success('已全部标记已读') }

onMounted(() => load(1))
</script>

<style scoped lang="scss">
.bar { display: flex; align-items: center; justify-content: space-between; padding: 14px 20px; margin-bottom: 16px; }
.item { display: flex; align-items: center; gap: 12px; padding: 14px 20px; margin-bottom: 12px; &.unread { border-left: 3px solid var(--sf-primary, #4361ee); } }
.body { flex: 1; min-width: 0; }
.line { display: flex; align-items: center; gap: 8px; font-size: 13px; }
.who { font-weight: 600; }
.type-tag { font-size: 11px; padding: 1px 8px; border-radius: 999px; &.c1 { background: rgba(67,97,238,.12); color: var(--sf-primary); } &.c2 { background: rgba(34,160,107,.12); color: var(--sf-success); } &.c3 { background: rgba(247,127,0,.14); color: var(--sf-accent); } &.c4 { background: rgba(245,22,45,.12); color: var(--sf-danger); } }
.time { color: var(--sf-text-3, #94a3b8); font-size: 12px; margin-left: auto; }
.content { margin-top: 4px; color: var(--sf-text-2, #475569); font-size: 14px; }
</style>