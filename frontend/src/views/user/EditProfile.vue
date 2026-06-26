<template>
  <div class="wrap">
    <h2 class="sf-page-title mb-16">编辑个人资料</h2>
    <el-form :model="form" :rules="rules" ref="formRef" label-position="top" class="form sf-card">
      <el-form-item label="头像">
        <div class="avatar-row">
          <el-avatar :size="64" :src="form.avatar || ''">{{ form.nickname?.[0] }}</el-avatar>
          <el-input v-model="form.avatar" placeholder="头像图片地址" style="flex:1" />
        </div>
      </el-form-item>
      <el-form-item label="昵称" prop="nickname"><el-input v-model="form.nickname" /></el-form-item>
      <el-form-item label="个性签名"><el-input v-model="form.signature" type="textarea" :rows="2" maxlength="255" show-word-limit /></el-form-item>
      <el-form-item label="邮箱"><el-input v-model="form.email" /></el-form-item>

      <el-divider>修改密码（可选）</el-divider>
      <el-form-item label="原密码"><el-input v-model="pwd.oldPassword" type="password" show-password /></el-form-item>
      <el-form-item label="新密码"><el-input v-model="pwd.newPassword" type="password" show-password /></el-form-item>

      <div class="footer">
        <el-button @click="$router.back()">取消</el-button>
        <el-button type="primary" :loading="saving" @click="save">保存</el-button>
      </div>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { authApi } from '@/api/auth'

const userStore = useUserStore()
const form = reactive({ nickname: userStore.user?.nickname || '', avatar: userStore.user?.avatar || '', signature: userStore.user?.signature || '', email: userStore.user?.email || '' })
const pwd = reactive({ oldPassword: '', newPassword: '' })
const rules: FormRules = { nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }] }
const formRef = ref<FormInstance>()
const saving = ref(false)

async function save() {
  await formRef.value?.validate()
  saving.value = true
  try {
    await userStore.updateProfile(form)
    if (pwd.oldPassword && pwd.newPassword) {
      await authApi.updatePassword({ oldPassword: pwd.oldPassword, newPassword: pwd.newPassword })
      pwd.oldPassword = ''; pwd.newPassword = ''
    }
    ElMessage.success('保存成功')
    userStore.fetchMe()
  } finally { saving.value = false }
}
</script>

<style scoped lang="scss">
.form { max-width: 640px; padding: 24px; }
.avatar-row { display: flex; align-items: center; gap: 14px; width: 100%; }
.footer { display: flex; justify-content: flex-end; gap: 10px; }
</style>