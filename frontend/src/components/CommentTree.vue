<template>
  <div class="comment-tree">
    <div v-for="c in list" :key="c.commentId" class="comment-item">
      <el-avatar :size="36" :src="c.authorAvatar || ''" class="clickable" @click="goUser(c.authorId)">{{ c.authorNickname?.[0] }}</el-avatar>
      <div class="body">
        <div class="line">
          <span class="name">{{ c.authorNickname }}</span>
          <span v-if="c.authorRole === 'TEACHER'" class="sf-badge role-teacher">老师</span>
          <span class="sf-badge role-student" v-else-if="c.authorRole === 'STUDENT'">同学</span>
          <span class="floor">#{{ c.floor }}</span>
          <span class="time">{{ formatTime(c.createTime) }}</span>
        </div>
        <div class="content">{{ c.content }}</div>
        <div class="actions">
          <span class="act" @click="like(c)"><el-icon><Pointer /></el-icon>{{ c.likeCount }}<em v-if="c.liked" class="liked">已赞</em></span>
          <span class="act" @click="reply = reply === c.commentId ? null : c.commentId">回复</span>
          <span v-if="canDel(c)" class="act danger" @click="del(c)">删除</span>
        </div>

        <div v-if="reply === c.commentId" class="reply-box">
          <el-input v-model="replyText" type="textarea" :rows="2" placeholder="友善回复…" />
          <div class="reply-btns">
            <el-button size="small" @click="reply = null; replyText = ''">取消</el-button>
            <el-button size="small" type="primary" @click="submitReply(c)">回复</el-button>
          </div>
        </div>

        <CommentTree
          v-if="c.children?.length"
          :list="c.children"
          :post-id="postId"
          @refresh="$emit('refresh')"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { Pointer } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { formatTime } from '@/utils/date'
import { commentApi } from '@/api/comment'
import { likeApi } from '@/api/like'
import { useUserStore } from '@/stores/user'

const props = defineProps<{ list: any[]; postId: number }>()
const emit = defineEmits<{ refresh: [] }>()
const router = useRouter()
function goUser(id: number) { router.push(`/user/${id}`) }

const userStore = useUserStore()
const reply = ref<number | null>(null)
const replyText = ref('')

const canDel = (c: any) =>
  userStore.user && (userStore.user.userId === c.authorId || userStore.isAdmin)

async function like(c: any) {
  if (!userStore.isLoggedIn) return ElMessage.warning('请先登录')
  const r = await likeApi.toggle({ targetType: 'COMMENT', targetId: c.commentId })
  c.liked = r.liked
  c.likeCount += r.liked ? 1 : -1
}

async function submitReply(c: any) {
  if (!replyText.value.trim()) return
  await commentApi.create({ postId: props.postId, parentId: c.commentId, content: replyText.value })
  reply.value = null; replyText.value = ''
  ElMessage.success('回复成功')
  emit('refresh')
}

async function del(c: any) {
  await commentApi.remove(c.commentId)
  ElMessage.success('已删除')
  emit('refresh')
}
</script>

<style scoped lang="scss">
.comment-item { display: flex; gap: 10px; padding: 14px 0; border-bottom: 1px solid var(--sf-border-soft, #f0f3f9); }
.clickable { cursor: pointer; transition: opacity .2s; &:hover { opacity: .8; } }
.body { flex: 1; min-width: 0; }
.line { display: flex; align-items: center; gap: 8px; font-size: 13px; }
.name { font-weight: 600; color: var(--sf-text-1, #1f2937); }
.floor { color: var(--sf-text-4, #cbd5e1); font-size: 12px; }
.time { color: var(--sf-text-3, #94a3b8); font-size: 12px; }
.content { margin: 6px 0; color: var(--sf-text-2, #475569); }
.actions { display: flex; gap: 16px; font-size: 13px; color: var(--sf-text-3); }
.act { cursor: pointer; display: inline-flex; align-items: center; gap: 4px; &:hover { color: var(--sf-primary, #4361ee); } }
.act.danger:hover { color: var(--sf-danger, #f5222d); }
.liked { font-style: normal; color: var(--sf-primary, #4361ee); margin-left: 4px; }
.reply-box { margin: 8px 0; }
.reply-btns { margin-top: 6px; text-align: right; }
</style>