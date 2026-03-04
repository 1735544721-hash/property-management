<template>
  <div class="register-page">
    <div class="register-bg"></div>
    
    <div class="register-container">
      <div class="register-box">
        <div class="register-header">
          <img src="https://images.unsplash.com/photo-1486406146926-c627a92ad1ab?w=60&h=60&fit=crop" alt="Logo" class="logo" />
          <h1>业主注册</h1>
          <p>加入智慧社区，享受便捷服务</p>
        </div>
        
        <el-form ref="formRef" :model="form" :rules="rules" class="register-form">
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
            />
          </el-form-item>
          
          <el-form-item prop="realName">
            <el-input
              v-model="form.realName"
              placeholder="请输入真实姓名"
              prefix-icon="Postcard"
              size="large"
            />
          </el-form-item>
          
          <el-form-item prop="phone">
            <el-input
              v-model="form.phone"
              placeholder="请输入手机号"
              prefix-icon="Phone"
              size="large"
            />
          </el-form-item>
          
          <el-form-item prop="email">
            <el-input
              v-model="form.email"
              placeholder="请输入邮箱（选填）"
              prefix-icon="Message"
              size="large"
            />
          </el-form-item>
          
          <el-form-item>
            <el-button
              type="primary"
              size="large"
              class="register-btn"
              :loading="loading"
              @click="handleRegister"
            >
              注 册
            </el-button>
          </el-form-item>
          
          <div class="register-footer">
            <span>已有账号？</span>
            <router-link to="/login">立即登录</router-link>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { register } from '@/api/auth'

const router = useRouter()

const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  username: '',
  password: '',
  realName: '',
  phone: '',
  email: ''
})

const validatePhone = (rule, value, callback) => {
  const phoneReg = /^1[3-9]\d{9}$/
  if (!phoneReg.test(value)) {
    callback(new Error('手机号格式不正确'))
  } else {
    callback()
  }
}

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { validator: validatePhone, trigger: 'blur' }
  ]
}

const handleRegister = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  
  loading.value = true
  try {
    await register(form)
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } catch (error) {
    console.error('注册失败:', error)
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.register-page {
  width: 100%;
  height: 100vh;
  display: flex;
  position: relative;
  overflow: hidden;
  
  .register-bg {
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
  
  .register-container {
    position: relative;
    width: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 20px;
    
    .register-box {
      width: 420px;
      background: rgba(255, 255, 255, 0.95);
      border-radius: 16px;
      padding: 40px;
      box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
      backdrop-filter: blur(10px);
      
      .register-header {
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
      
      .register-form {
        .register-btn {
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
      
      .register-footer {
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
    }
  }
}
</style>

