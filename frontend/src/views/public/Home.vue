<template>
  <div class="home">
    <div class="hero sf-card">
      <div class="hero-bg"></div>
      <div class="hero-text">
        <h1>欢迎来到校园论坛</h1>
        <p>分享心得、提问求助、答疑互动 —— 让校园更有温度</p>
        <div class="hero-btns">
          <el-button v-if="!userStore.isLoggedIn" type="primary" size="large" @click="$router.push('/register')">立即加入</el-button>
          <el-button v-else type="primary" size="large" :icon="EditPen" @click="$router.push('/post/new')">我要发帖</el-button>
          <el-button size="large" plain @click="$router.push('/hot')">查看热榜</el-button>
        </div>
      </div>
    </div>
    <PostListPanel />
  </div>
</template>

<script setup lang="ts">
import { EditPen } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import PostListPanel from '@/components/PostListPanel.vue'

const userStore = useUserStore()
</script>

<style scoped lang="scss">
.home { display: flex; flex-direction: column; gap: 20px; }
.hero { position: relative; overflow: hidden; border: none; }
.hero-bg { position: absolute; inset: 0; background: var(--sf-gradient, linear-gradient(135deg,#4361ee,#6c8aff)); opacity: .92; }
.hero-bg::after { content: ''; position: absolute; inset: 0; background: radial-gradient(circle at 80% 20%, rgba(255,255,255,.25), transparent 60%); }
.hero-text { position: relative; padding: 44px 36px; color: #fff; z-index: 1; }
.hero-text h1 { font-size: 28px; font-weight: 800; margin-bottom: 8px; }
.hero-text p { opacity: .92; margin-bottom: 20px; }
.hero-btns { display: flex; gap: 12px; }
:deep(.hero-btns .el-button--large) { border-radius: 10px; }
:deep(.hero .el-button--primary) { background: #fff; color: var(--sf-primary, #4361ee); border: none; }
:deep(.hero .el-button.is-plain) { background: rgba(255,255,255,.15); color: #fff; border-color: rgba(255,255,255,.6); }
</style>