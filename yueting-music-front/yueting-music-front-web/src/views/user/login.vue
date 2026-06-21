<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter, useRoute } from 'vue-router'
import { login, register, uploadAvatar } from '@/api/user'

const router = useRouter()
const route = useRoute()

const isLogin = ref(true)
const form = ref({ username: '', password: '', nickname: '' })
const avatarFile = ref(null)
const avatarPreview = ref('')
const confirmPassword = ref('')
const loading = ref(false)


function onAvatarChange(e) {
  const file = e.target.files[0]
  if (!file) return
  avatarFile.value = file
  const reader = new FileReader()
  reader.onload = (ev) => { avatarPreview.value = ev.target.result }
  reader.readAsDataURL(file)
}
function toggleMode() {
  isLogin.value = !isLogin.value
  confirmPassword.value = ''
  avatarFile.value = null
  avatarPreview.value = ''
}

async function handleSubmit() {
  if (!form.value.username || !form.value.password) {
    ElMessage.warning('请填写完整信息')
    return
  }
  if (!isLogin.value && form.value.password !== confirmPassword.value) {
    ElMessage.warning('两次密码不一致')
    return
  }

  loading.value = true
  try {
    const res = isLogin.value
      ? await login(form.value)
      : await register({ ...form.value })
    if (res.code === 200) {
      if (isLogin.value) {
        localStorage.setItem('user_token', res.data.token)
        localStorage.setItem('user_info', JSON.stringify(res.data.user))
        const redirect = route.query.redirect || '/'
        router.push(redirect)
      } else {
        // Upload avatar if selected
        if (avatarFile.value && res.data.id) {
          try {
            const avatarRes = await uploadAvatar(res.data.id, avatarFile.value)
            if (avatarRes.code === 200) {
              const u = JSON.parse(localStorage.getItem('user_info') || '{}')
              u.avatar = avatarRes.data
            }
          } catch {}
        }
        ElMessage.success('注册成功，请登录')
        isLogin.value = true
      }
    }
  } catch (err) {
    // 错误已在 request 拦截器中处理
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="login-page">
    <div class="login-card">
      <div class="login-header">
        <div class="logo-area">
          <span class="logo-icon">🎵</span>
          <h1 class="logo-text">悦听音乐</h1>
        </div>
        <p class="login-subtitle">{{ isLogin ? '欢迎回来' : '创建账号' }}</p>
      </div>

      <el-form :model="form" class="login-form" @submit.prevent="handleSubmit">
        <el-form-item>
          <el-input v-model="form.username" placeholder="用户名" size="large" prefix-icon="User" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="form.password" type="password" placeholder="密码" size="large" prefix-icon="Lock" show-password />
        </el-form-item>
        <el-form-item v-if="!isLogin">
          <el-input v-model="form.nickname" placeholder="昵称（选填）" size="large" prefix-icon="User" />
        </el-form-item>
        <el-form-item v-if="!isLogin">
          <el-input v-model="confirmPassword" type="password" placeholder="确认密码" size="large" prefix-icon="Lock" show-password />
        </el-form-item>
        <el-form-item v-if="!isLogin">
          <div class="avatar-upload">
            <div class="avatar-preview" @click="$refs.avatarInput.click()">
              <img v-if="avatarPreview" :src="avatarPreview" />
              <span v-else class="avatar-placeholder">{{ form.username?.charAt(0) || '?' }}</span>
            </div>
            <span class="avatar-tip">点击上传头像（可选）</span>
          </div>
          <input ref="avatarInput" type="file" accept="image/*" style="display:none" @change="onAvatarChange" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" class="submit-btn" :loading="loading" native-type="submit" round>
            {{ isLogin ? '登录' : '注册' }}
          </el-button>
        </el-form-item>
      </el-form>

      <div class="login-footer">
        <span v-if="isLogin">还没有账号？</span>
        <span v-else>已有账号？</span>
        <a href="javascript:;" @click="toggleMode" class="toggle-link">
          {{ isLogin ? '立即注册' : '去登录' }}
        </a>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
$primary: #3b82f6;

.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #eff6ff 0%, #dbeafe 50%, #ede9fe 100%);
  padding: 20px;
}

.login-card {
  width: 420px;
  max-width: 100%;
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(16px);
  border-radius: 20px;
  padding: 40px 36px 32px;
  box-shadow: 0 8px 40px rgba(59, 130, 246, 0.12);
  border: 1px solid rgba(59, 130, 246, 0.06);
}

.login-header {
  text-align: center;
  margin-bottom: 32px;
}

.logo-area {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-bottom: 8px;
}

.logo-icon { font-size: 32px; }

.logo-text {
  font-size: 24px;
  font-weight: 700;
  background: linear-gradient(135deg, $primary, #8b5cf6);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.login-subtitle {
  font-size: 14px;
  color: #94a3b8;
}

.login-form {
  :deep(.el-input__wrapper) {
    border-radius: 12px;
    padding: 4px 16px;
    background: #f8fafc;
    box-shadow: none;
    border: 1px solid #e2e8f0;
    transition: all 0.2s;

    &:hover, &.is-focus {
      border-color: $primary;
      box-shadow: 0 0 0 1px rgba($primary, 0.2);
    }
  }

  :deep(.el-input__prefix) {
    color: #94a3b8;
  }
}

.submit-btn {
  width: 100%;
  height: 44px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 22px !important;
  background: linear-gradient(135deg, $primary, #6366f1);
  border: none;
  transition: all 0.3s;

  &:hover {
    transform: translateY(-1px);
    box-shadow: 0 4px 16px rgba($primary, 0.3);
  }
}

.login-footer {
  text-align: center;
  margin-top: 16px;
  font-size: 13px;
  color: #94a3b8;
}

.toggle-link {
  color: $primary;
  font-weight: 600;
  margin-left: 4px;

  &:hover { text-decoration: underline; }
}

.avatar-upload { display: flex; align-items: center; gap: 14px; }
.avatar-preview {
  width: 64px; height: 64px; border-radius: 50%;
  overflow: hidden; cursor: pointer;
  border: 2px dashed #e2e8f0; display: flex;
  align-items: center; justify-content: center;
  transition: all 0.2s; flex-shrink: 0;
}
.avatar-preview:hover { border-color: $primary; }
.avatar-preview img { width: 100%; height: 100%; object-fit: cover; }
.avatar-placeholder {
  width: 100%; height: 100%; display: flex;
  align-items: center; justify-content: center;
  background: linear-gradient(135deg, #ff85a2, #c084fc);
  color: #fff; font-size: 22px; font-weight: 700;
}
.avatar-tip { font-size: 12px; color: #94a3b8; }
</style>