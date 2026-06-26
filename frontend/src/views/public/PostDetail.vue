<template>
  <div v-loading="loading">
    <template v-if="post">
      <article class="post sf-card">
        <div class="header">
          <h1 class="title">{{ post.title }}</h1>
          <div class="badges">
            <span v-if="post.isTop" class="sf-badge top">置顶</span>
            <span v-if="post.isEssence" class="sf-badge essence">精华</span>
          </div>
        </div>

        <div class="author-bar">
          <div class="left" @click="router.push(`/user/${post.authorId}`)" style="cursor:pointer">
            <el-avatar :size="42" :src="post.authorAvatar || ''">{{ post.authorNickname?.[0] }}</el-avatar>
            <div>
              <div class="a-name">{{ post.authorNickname }}</div>
              <div class="a-sub">{{ post.channelName }} · {{ formatTime(post.createTime) }} · 浏览 {{ post.viewCount }}</div>
            </div>
          </div>
          <el-button v-if="canEdit" text @click="$router.push(`/post/edit/${post.postId}`)">编辑</el-button>
          <el-button v-if="canDel" text class="danger-btn" @click="del">删除</el-button>
        </div>

        <div class="tags">
          <TagChip v-for="t in post.tags" :key="t.tagId" :tag="t" />
        </div>

        <MarkdownPreview :modelValue="post.content" class="content" />

        <div class="actions">
          <div class="act" :class="{ on: post.liked }" @click="likePost">
            <el-icon><Pointer /></el-icon><span>{{ post.likeCount }}</span><em>赞</em>
          </div>
          <div class="act" :class="{ on: post.collected }" @click="collect">
            <el-icon><Star /></el-icon><span>{{ post.collectCount }}</span><em>收藏</em>
          </div>
          <el-button @click="scrollToComment">写评论</el-button>
        </div>
      </article>

      <section class="comment-box sf-card" id="comment">
        <div class="box-title">评论区 · {{ commentTotal }}</div>
        <div class="editor">
          <el-input v-model="commentText" type="textarea" :rows="3" placeholder="说点什么…（支持楼中楼回复）" />
          <div class="send-bar">
            <el-button type="primary" :disabled="!commentText.trim()" @click="submitComment">发表评论</el-button>
          </div>
        </div>

        <CommentTree :list="comments" :post-id="post.postId" @refresh="loadComments" />
        <div v-if="!comments.length" class="sf-empty">还没有评论，来抢沙发吧～</div>
      </section>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Pointer, Star } from '@element-plus/icons-vue'
import { postApi } from '@/api/post'
import { commentApi } from '@/api/comment'
import { likeApi } from '@/api/like'
import { collectApi } from '@/api/collect'
import { useUserStore } from '@/stores/user'
import { formatTime } from '@/utils/date'
import TagChip from '@/components/TagChip.vue'
import MarkdownPreview from '@/components/MarkdownPreview.vue'
import CommentTree from '@/components/CommentTree.vue'
import type { PostDetail, Comment } from '@/types'

const props = defineProps<{ id: string }>()
const router = useRouter()
const userStore = useUserStore()

const post = ref<PostDetail | null>(null)
const loading = ref(true)
const comments = ref<Comment[]>([])
const commentTotal = computed(() => comments.value.length)
const commentText = ref('')

const canEdit = computed(() => userStore.user && (userStore.user.userId === post.value?.authorId || userStore.isAdmin))
const canDel = computed(() => canEdit.value)

async function load() {
  loading.value = true
  try { post.value = await postApi.detail(Number(props.id)) } finally { loading.value = false }
  loadComments()
}
async function loadComments() {
  comments.value = await commentApi.treeByPost(Number(props.id))
}

async function likePost() {
  if (!userStore.isLoggedIn) return router.push('/login')
  const r = await likeApi.toggle({ targetType: 'POST', targetId: post.value!.postId })
  post.value!.liked = r.liked
  post.value!.likeCount += r.liked ? 1 : -1
}
async function collect() {
  if (!userStore.isLoggedIn) return router.push('/login')
  const r = await collectApi.toggle({ postId: post.value!.postId })
  post.value!.collected = r.collected
  post.value!.collectCount += r.collected ? 1 : -1
}
async function submitComment() {
  if (!userStore.isLoggedIn) return router.push('/login')
  await commentApi.create({ postId: post.value!.postId, parentId: null, content: commentText.value })
  commentText.value = ''; ElMessage.success('评论成功'); loadComments()
}
function scrollToComment() {
  if (!userStore.isLoggedIn) return router.push('/login')
  document.getElementById('comment')?.scrollIntoView({ behavior: 'smooth' })
}
async function del() {
  await ElMessageBox.confirm('确定删除这篇帖子吗？', '提示', { type: 'warning' })
  await postApi.remove(post.value!.postId)
  ElMessage.success('已删除')
  router.push('/')
}

onMounted(load)
</script>

<style scoped lang="scss">
.post { padding: 28px 30px; }
.header { display: flex; align-items: flex-start; justify-content: space-between; gap: 12px; }
.title { font-size: 26px; font-weight: 800; line-height: 1.3; }
.badges { display: flex; gap: 6px; }
.author-bar { display: flex; align-items: center; justify-content: space-between; margin: 18px 0; padding-top: 16px; border-top: 1px dashed var(--sf-border-soft, #f0f3f9); }
.left { display: flex; align-items: center; gap: 10px; }
.a-name { font-weight: 600; }
.a-sub { font-size: 12px; color: var(--sf-text-3, #94a3b8); margin-top: 2px; }
.tags { display: flex; gap: 6px; margin-bottom: 16px; flex-wrap: wrap; }
.content { margin-top: 8px; }
.actions { display: flex; gap: 12px; align-items: center; margin-top: 24px; padding-top: 20px; border-top: 1px solid var(--sf-border-soft, #f0f3f9); }
.act { display: flex; align-items: center; gap: 6px; padding: 8px 16px; border-radius: 999px; background: var(--sf-bg-soft, #eef2f9); color: var(--sf-text-2, #475569); cursor: pointer; transition: all .2s ease;
  &.on { background: var(--sf-primary, #4361ee); color: #fff; }
  em { font-style: normal; font-size: 13px; margin-left: 2px; }
}
.danger-btn { color: var(--sf-danger, #f5222d); }

.comment-box { padding: 22px 26px; margin-top: 20px; }
.box-title { font-size: 17px; font-weight: 700; margin-bottom: 14px; }
.editor { margin-bottom: 8px; }
.send-bar { text-align: right; margin-top: 10px; }
</style>