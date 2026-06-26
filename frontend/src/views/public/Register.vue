<template>
  <div class="auth">
    <div class="bg"></div>
    <div class="box sf-card">
      <div class="brand"><span class="logo">校</span><span>校园论坛</span></div>
      <h2 class="title">创建账号</h2>
      <p class="sub">加入校园师生社区</p>

      <el-form ref="formRef" :model="form" :rules="rules">
        <el-form-item prop="username"><el-input v-model="form.username" :prefix-icon="User" placeholder="用户名" size="large" /></el-form-item>
        <el-form-item prop="nickname"><el-input v-model="form.nickname" :prefix-icon="UserFilled" placeholder="昵称" size="large" /></el-form-item>
        <el-form-item prop="email"><el-input v-model="form.email" :prefix-icon="Message" placeholder="邮箱（可选）" size="large" /></el-form-item>
        <el-form-item prop="password"><el-input v-model="form.password" type="password" show-password :prefix-icon="Lock" placeholder="密码" size="large" /></el-form-item>
        <el-form-item prop="confirm"><el-input v-model="form.confirm" type="password" show-password :prefix-icon="Lock" placeholder="确认密码" size="large" /></el-form-item>
        <el-button type="primary" size="large" class="submit" :loading="loading" @click="submit">注册</el-button>
        <div class="switch">已有账号？<router-link to="/login">去登录</router-link></div>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { User, UserFilled, Lock, Message } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const form = reactive({ username: '', nickname: '', email: '', password: '', confirm: '' })
const rules: FormRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }, { min: 3, message: '至少 3 个字符', trigger: 'blur' }],
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }, { min: 6, message: '至少 6 位', trigger: 'blur' }],
  confirm: [{ required: true, message: '请再次输入密码', trigger: 'blur' }, { validator: (_r, v, cb) => v === form.password ? cb() : cb(new Error('两次密码不一致')), trigger: 'blur' }]
}
const formRef = ref<FormInstance>()
const loading = ref(false)

async function submit() {
  await formRef.value?.validate()
  loading.value = true
  try {
    await userStore.register({ username: form.username, nickname: form.nickname, email: form.email || undefined, password: form.password })
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } finally { loading.value = false }
}
</script>

<style scoped lang="scss">
.auth { min-height: 100vh; display: flex; align-items: center; justify-content: center; position: relative; }
.bg { position: absolute; inset: 0; background: var(--sf-gradient); }
.bg::after { content: ''; position: absolute; inset: 0; background: radial-gradient(circle at 80% 20%, rgba(255,255,255,.3), transparent 50%), radial-gradient(circle at 20% 80%, rgba(247,127,0,.3), transparent 50%); }
.box { position: relative; width: 400px; max-width: 92vw; padding: 32px; border-radius: 18px; }
.brand { display: flex; align-items: center; gap: 10px; margin-bottom: 16px; }
.logo { width: 36px; height: 36px; border-radius: 10px; background: #fff; color: var(--sf-primary); display: flex; align-items: center; justify-content: center; font-weight: 700; }
.brand span:last-child { color: #fff; font-size: 18px; font-weight: 700; }
.title { color: #fff; font-size: 22px; font-weight: 700; }
.sub { color: rgba(255,255,255,.8); margin: 4px 0 18px; font-size: 13px; }
.submit { width: 100%; margin-top: 4px; }
.switch { text-align: center; margin-top: 16px; font-size: 13px; color: var(--sf-text-3, #94a3b8); a { color: var(--sf-primary, #4361ee); } }
</style>