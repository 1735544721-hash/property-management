<template>
  <div class="profile-page">
    <el-row :gutter="20">
      <!-- 左侧个人信息 -->
      <el-col :span="8">
        <div class="profile-card card">
          <div class="avatar-section">
            <el-avatar :size="100" :src="userInfo.avatar || defaultAvatar" />
            <h3>{{ userInfo.realName || userInfo.username }}</h3>
            <el-tag :type="roleTagType">{{ roleName }}</el-tag>
          </div>
          
          <div class="info-section">
            <div class="info-item">
              <el-icon><User /></el-icon>
              <span>用户名：{{ userInfo.username }}</span>
            </div>
            <div class="info-item">
              <el-icon><Phone /></el-icon>
              <span>手机号：{{ userInfo.phone || '未设置' }}</span>
            </div>
            <div class="info-item">
              <el-icon><Message /></el-icon>
              <span>邮箱：{{ userInfo.email || '未设置' }}</span>
            </div>
          </div>
        </div>
      </el-col>
      
      <!-- 右侧编辑区域 -->
      <el-col :span="16">
        <el-tabs v-model="activeTab" class="profile-tabs">
          <el-tab-pane label="基本信息" name="info">
            <div class="card">
              <el-form ref="infoFormRef" :model="infoForm" :rules="infoRules" label-width="100px">
                <el-form-item label="真实姓名" prop="realName">
                  <el-input v-model="infoForm.realName" placeholder="请输入真实姓名" />
                </el-form-item>
                <el-form-item label="手机号" prop="phone">
                  <el-input v-model="infoForm.phone" placeholder="请输入手机号" />
                </el-form-item>
                <el-form-item label="邮箱" prop="email">
                  <el-input v-model="infoForm.email" placeholder="请输入邮箱" />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" :loading="infoLoading" @click="handleUpdateInfo">保存修改</el-button>
                </el-form-item>
              </el-form>
            </div>
          </el-tab-pane>
          
          <el-tab-pane label="修改密码" name="password">
            <div class="card">
              <el-form ref="pwdFormRef" :model="pwdForm" :rules="pwdRules" label-width="100px">
                <el-form-item label="当前密码" prop="oldPassword">
                  <el-input v-model="pwdForm.oldPassword" type="password" placeholder="请输入当前密码" show-password />
                </el-form-item>
                <el-form-item label="新密码" prop="newPassword">
                  <el-input v-model="pwdForm.newPassword" type="password" placeholder="请输入新密码" show-password />
                </el-form-item>
                <el-form-item label="确认密码" prop="confirmPassword">
                  <el-input v-model="pwdForm.confirmPassword" type="password" placeholder="请再次输入新密码" show-password />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" :loading="pwdLoading" @click="handleUpdatePassword">修改密码</el-button>
                </el-form-item>
              </el-form>
            </div>
          </el-tab-pane>
        </el-tabs>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { getUserInfo, updateUserInfo, updatePassword } from '@/api/user'

const userStore = useUserStore()

const activeTab = ref('info')
const defaultAvatar = 'https://images.unsplash.com/photo-1633332755192-727a05c4013d?w=100&h=100&fit=crop'

const userInfo = ref({})
const infoLoading = ref(false)
const pwdLoading = ref(false)

const infoFormRef = ref(null)
const pwdFormRef = ref(null)

const infoForm = reactive({
  realName: '',
  phone: '',
  email: ''
})

const pwdForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const roleName = computed(() => {
  const roles = { admin: '管理员', staff: '物业员工', resident: '业主' }
  return roles[userInfo.value.role] || '未知'
})

const roleTagType = computed(() => {
  const types = { admin: 'danger', staff: 'success', resident: 'warning' }
  return types[userInfo.value.role] || ''
})

const infoRules = {
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ]
}

const validateConfirmPwd = (rule, value, callback) => {
  if (value !== pwdForm.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const pwdRules = {
  oldPassword: [{ required: true, message: '请输入当前密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    { validator: validateConfirmPwd, trigger: 'blur' }
  ]
}

const loadUserInfo = async () => {
  try {
    const res = await getUserInfo()
    userInfo.value = res.data
    infoForm.realName = res.data.realName
    infoForm.phone = res.data.phone
    infoForm.email = res.data.email
  } catch {
    // 忽略错误
  }
}

const handleUpdateInfo = async () => {
  const valid = await infoFormRef.value.validate().catch(() => false)
  if (!valid) return
  
  infoLoading.value = true
  try {
    await updateUserInfo(infoForm)
    ElMessage.success('修改成功')
    // 更新本地存储
    userStore.setUserInfo({ ...userStore.userInfo, ...infoForm })
    loadUserInfo()
  } finally {
    infoLoading.value = false
  }
}

const handleUpdatePassword = async () => {
  const valid = await pwdFormRef.value.validate().catch(() => false)
  if (!valid) return
  
  pwdLoading.value = true
  try {
    await updatePassword({
      oldPassword: pwdForm.oldPassword,
      newPassword: pwdForm.newPassword
    })
    ElMessage.success('密码修改成功')
    pwdFormRef.value.resetFields()
  } finally {
    pwdLoading.value = false
  }
}

onMounted(() => {
  loadUserInfo()
})
</script>

<style lang="scss" scoped>
.profile-page {
  .card {
    background: #fff;
    border-radius: var(--radius-base);
    padding: 24px;
    box-shadow: var(--shadow-light);
  }
  
  .profile-card {
    .avatar-section {
      text-align: center;
      padding-bottom: 24px;
      border-bottom: 1px solid var(--border-light);
      
      .el-avatar {
        margin-bottom: 16px;
      }
      
      h3 {
        font-size: 20px;
        color: var(--text-primary);
        margin-bottom: 8px;
      }
    }
    
    .info-section {
      padding-top: 24px;
      
      .info-item {
        display: flex;
        align-items: center;
        padding: 12px 0;
        color: var(--text-regular);
        
        .el-icon {
          margin-right: 12px;
          color: var(--primary-color);
        }
      }
    }
  }
  
  .profile-tabs {
    :deep(.el-tabs__content) {
      padding: 0;
    }
    
    :deep(.el-tabs__header) {
      margin-bottom: 20px;
    }
  }
}
</style>

