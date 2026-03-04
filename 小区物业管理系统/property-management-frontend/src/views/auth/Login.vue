<template>
  <div class="login-page">
    <div class="login-bg"></div>
    
    <div class="login-container">
      <div class="login-box">
        <div class="login-header">
          <img src="https://images.unsplash.com/photo-1486406146926-c627a92ad1ab?w=60&h=60&fit=crop" alt="Logo" class="logo" />
          <h1>小区物业管理系统</h1>
          <p>智慧物业 · 温馨家园</p>
        </div>
        
        <el-form ref="formRef" :model="form" :rules="rules" class="login-form">
          <el-form-item prop="username">
            <el-input
              v-model="form.username"
              placeholder="请输入用户名"
              prefix-icon="User"
              size="large"
            />
          </el-form-item>
          
          <el-form-item prop="password">
            <el-input
              v-model="form.password"
              type="password"
              placeholder="请输入密码"
              prefix-icon="Lock"
              size="large"
              show-password
              @keyup.enter="handleLogin"
            />
          </el-form-item>
          
          <el-form-item>
            <el-button
              type="primary"
              size="large"
              class="login-btn"
              :loading="loading"
              @click="handleLogin"
            >
              登 录
            </el-button>
          </el-form-item>
          
          <div class="login-footer">
            <span>还没有账号？</span>
            <router-link to="/register">立即注册</router-link>
          </div>
        </el-form>
        
        <div class="demo-accounts">
          <p>演示账号（密码均为 123456）：</p>
          <div class="accounts">
            <el-tag @click="fillAccount('admin', '123456')">管理员</el-tag>
            <el-tag type="success" @click="fillAccount('staff01', '123456')">物业员工</el-tag>
            <el-tag type="warning" @click="fillAccount('user01', '123456')">业主</el-tag>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { login } from '@/api/auth'

const router = useRouter()
const userStore = useUserStore()

const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const fillAccount = (username, password) => {
  form.username = username
  form.password = password
}

const handleLogin = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  
  loading.value = true
  try {
    const res = await login(form)
    userStore.login(res.data)
    ElMessage.success('登录成功')
    router.push('/dashboard')
  } catch (error) {
    console.error('登录失败:', error)
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.login-page {
  width: 100%;
  height: 100vh;
  display: flex;
  position: relative;
  overflow: hidden;
  
  .login-bg {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: linear-gradient(135deg, #1E3A5F 0%, #2E5A8F 50%, #4CAF50 100%);
    
    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      background-image: url('https://images.unsplash.com/photo-1545324418-cc1a3fa10c00?w=1920&h=1080&fit=crop');
      background-size: cover;
      background-position: center;
      opacity: 0.15;
    }
  }
  
  .login-container {
    position: relative;
    width: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 20px;
    
    .login-box {
      width: 420px;
      background: rgba(255, 255, 255, 0.95);
      border-radius: 16px;
      padding: 40px;
      box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
      backdrop-filter: blur(10px);
      
      .login-header {
        text-align: center;
        margin-bottom: 30px;
        
        .logo {
          width: 60px;
          height: 60px;
          border-radius: 12px;
          margin-bottom: 16px;
        }
        
        h1 {
          font-size: 24px;
          font-weight: 600;
          color: var(--primary-color);
          margin-bottom: 8px;
        }
        
        p {
          color: var(--text-secondary);
          font-size: 14px;
        }
      }
      
      .login-form {
        .login-btn {
          width: 100%;
          height: 46px;
          font-size: 16px;
          background: linear-gradient(135deg, var(--primary-color), var(--primary-light));
          border: none;
          
          &:hover {
            background: linear-gradient(135deg, var(--primary-light), var(--primary-color));
          }
        }
      }
      
      .login-footer {
        text-align: center;
        color: var(--text-secondary);
        
        a {
          color: var(--primary-color);
          font-weight: 500;
          
          &:hover {
            color: var(--success-color);
          }
        }
      }
      
      .demo-accounts {
        margin-top: 30px;
        padding-top: 20px;
        border-top: 1px dashed var(--border-color);
        
        p {
          font-size: 12px;
          color: var(--text-secondary);
          margin-bottom: 10px;
        }
        
        .accounts {
          display: flex;
          justify-content: center;
          gap: 10px;
          
          .el-tag {
            cursor: pointer;
            
            &:hover {
              opacity: 0.8;
            }
          }
        }
      }
    }
  }
}
</style>

