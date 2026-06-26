<template>
  <div class="auth">
    <div class="bg"></div>
    <div class="box sf-card">
      <div class="brand">
        <span class="logo">校</span><span>校园论坛</span>
      </div>
      <h2 class="title">欢迎回来</h2>
      <p class="sub">登录以参与讨论、点赞与发帖</p>

      <el-form ref="formRef" :model="form" :rules="rules" @submit.prevent="submit">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="用户名" :prefix-icon="User" size="large" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" show-password placeholder="密码" :prefix-icon="Lock" size="large" @keyup.enter="submit" />
        </el-form-item>
        <el-button type="primary" size="large" class="submit" :loading="loading" @click="submit">登录</el-button>
        <div class="switch">还没有账号？<router-link to="/register">立即注册</router-link></div>
      </el-form>

      <el-alert title="测试账号：admin / admin123，stu01 / stu123" type="info" :closable="false" class="tip" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { useNotificationStore } from '@/stores/notification'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const notifyStore = useNotificationStore()

const form = reactive({ username: '', password: '' })
const rules: FormRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}
const formRef = ref<FormInstance>()
const loading = ref(false)

async function submit() {
  await formRef.value?.validate()
  loading.value = true
  try {
    await userStore.login(form)
    notifyStore.startPoll()
    ElMessage.success('登录成功')
    router.push((route.query.redirect as string) || '/')
  } finally { loading.value = false }
}
</script>

<style scoped lang="scss">
.auth { min-height: 100vh; display: flex; align-items: center; justify-content: center; position: relative; overflow: hidden; }
.bg { position: absolute; inset: 0; background: var(--sf-gradient); }
.bg::after { content: ''; position: absolute; inset: 0; background: radial-gradient(circle at 20% 30%, rgba(255,255,255,.3), transparent 50%), radial-gradient(circle at 80% 70%, rgba(247,127,0,.3), transparent 50%); }
.box { position: relative; width: 400px; max-width: 92vw; padding: 36px 32px; border-radius: 18px; backdrop-filter: blur(10px); }
.brand { display: flex; align-items: center; gap: 10px; margin-bottom: 20px; }
.logo { width: 36px; height: 36px; border-radius: 10px; background: var(--sf-gradient); color: #fff; display: flex; align-items: center; justify-content: center; font-weight: 700; }
.brand span:last-child { font-size: 18px; font-weight: 700; }
.title { font-size: 22px; font-weight: 700; }
.sub { color: var(--sf-text-3, #94a3b8); margin: 4px 0 22px; font-size: 13px; }
.submit { width: 100%; margin-top: 6px; }
.switch { text-align: center; margin-top: 18px; font-size: 13px; color: var(--sf-text-3, #94a3b8); a { color: var(--sf-primary, #4361ee); } }
.tip { margin-top: 14px; }
</style>