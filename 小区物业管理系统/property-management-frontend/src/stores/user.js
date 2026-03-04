import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useUserStore = defineStore('user', () => {
  // 状态
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || '{}'))
  
  // 计算属性
  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => userInfo.value.role === 'admin')
  const isStaff = computed(() => userInfo.value.role === 'staff')
  const isResident = computed(() => userInfo.value.role === 'resident')
  
  // 方法
  function setToken(newToken) {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }
  
  function setUserInfo(info) {
    userInfo.value = info
    localStorage.setItem('userInfo', JSON.stringify(info))
  }
  
  function login(data) {
    setToken(data.token)
    setUserInfo({
      userId: data.userId,
      username: data.username,
      realName: data.realName,
      avatar: data.avatar,
      role: data.role
    })
  }
  
  function logout() {
    token.value = ''
    userInfo.value = {}
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }
  
  return {
    token,
    userInfo,
    isLoggedIn,
    isAdmin,
    isStaff,
    isResident,
    setToken,
    setUserInfo,
    login,
    logout
  }
})

