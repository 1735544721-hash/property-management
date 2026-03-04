<template>
  <el-container class="main-layout">
    <!-- 侧边栏 -->
    <el-aside :width="isCollapse ? '64px' : '220px'" class="sidebar">
      <div class="logo">
        <img src="https://images.unsplash.com/photo-1486406146926-c627a92ad1ab?w=50&h=50&fit=crop" alt="Logo" />
        <span v-show="!isCollapse">物业管理</span>
      </div>
      
      <el-menu
        :default-active="$route.path"
        :collapse="isCollapse"
        :collapse-transition="false"
        background-color="#1E3A5F"
        text-color="#fff"
        active-text-color="#4CAF50"
        router
      >
        <template v-for="item in menuList" :key="item.path">
          <el-menu-item :index="item.path">
            <el-icon><component :is="item.icon" /></el-icon>
            <template #title>{{ item.title }}</template>
          </el-menu-item>
        </template>
      </el-menu>
    </el-aside>
    
    <!-- 主内容区 -->
    <el-container class="main-container">
      <!-- 顶部导航 -->
      <el-header class="header">
        <div class="header-left">
          <el-icon class="collapse-btn" @click="isCollapse = !isCollapse">
            <Fold v-if="!isCollapse" />
            <Expand v-else />
          </el-icon>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ $route.meta.title }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <div class="user-info">
              <el-avatar :size="36" :src="userStore.userInfo.avatar || defaultAvatar" />
              <span class="username">{{ userStore.userInfo.realName || userStore.userInfo.username }}</span>
              <el-icon><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon>个人中心
                </el-dropdown-item>
                <el-dropdown-item command="password">
                  <el-icon><Lock /></el-icon>修改密码
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <el-icon><SwitchButton /></el-icon>退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      
      <!-- 内容区 -->
      <el-main class="main-content">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { logout } from '@/api/auth'

const router = useRouter()
const userStore = useUserStore()

const isCollapse = ref(false)
const defaultAvatar = 'https://images.unsplash.com/photo-1633332755192-727a05c4013d?w=100&h=100&fit=crop'

// 根据角色生成菜单
const menuList = computed(() => {
  const role = userStore.userInfo.role
  const baseMenus = [
    { path: '/dashboard', title: '工作台', icon: 'Odometer' }
  ]
  
  if (role === 'admin') {
    return [
      ...baseMenus,
      { path: '/user', title: '用户管理', icon: 'User' },
      { path: '/building', title: '楼栋管理', icon: 'OfficeBuilding' },
      { path: '/house', title: '房屋管理', icon: 'House' },
      { path: '/repair', title: '报修管理', icon: 'SetUp' },
      { path: '/fee', title: '费用管理', icon: 'Wallet' },
      { path: '/notice', title: '公告管理', icon: 'Bell' },
      { path: '/complaint', title: '投诉管理', icon: 'ChatLineSquare' },
      { path: '/profile', title: '个人中心', icon: 'User' }
    ]
  } else if (role === 'staff') {
    return [
      ...baseMenus,
      { path: '/building', title: '楼栋管理', icon: 'OfficeBuilding' },
      { path: '/house', title: '房屋管理', icon: 'House' },
      { path: '/repair', title: '报修管理', icon: 'SetUp' },
      { path: '/fee', title: '费用管理', icon: 'Wallet' },
      { path: '/notice', title: '公告管理', icon: 'Bell' },
      { path: '/complaint', title: '投诉管理', icon: 'ChatLineSquare' },
      { path: '/profile', title: '个人中心', icon: 'User' }
    ]
  } else {
    return [
      ...baseMenus,
      { path: '/my-house', title: '我的房产', icon: 'House' },
      { path: '/my-repair', title: '我的报修', icon: 'SetUp' },
      { path: '/my-fee', title: '我的缴费', icon: 'Wallet' },
      { path: '/notice-center', title: '公告中心', icon: 'Bell' },
      { path: '/my-complaint', title: '投诉建议', icon: 'ChatLineSquare' },
      { path: '/profile', title: '个人中心', icon: 'User' }
    ]
  }
})

const handleCommand = async (command) => {
  if (command === 'profile') {
    router.push('/profile')
  } else if (command === 'password') {
    router.push('/profile')
  } else if (command === 'logout') {
    try {
      await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
      await logout()
      userStore.logout()
      router.push('/login')
    } catch {
      // 取消退出
    }
  }
}
</script>

<style lang="scss" scoped>
.main-layout {
  height: 100vh;
  
  .sidebar {
    background: var(--primary-color);
    transition: width 0.3s;
    overflow: hidden;
    
    .logo {
      height: 60px;
      display: flex;
      align-items: center;
      justify-content: center;
      padding: 10px;
      background: var(--primary-dark);
      
      img {
        width: 40px;
        height: 40px;
        border-radius: 8px;
      }
      
      span {
        margin-left: 10px;
        font-size: 18px;
        font-weight: 600;
        color: #fff;
        white-space: nowrap;
      }
    }
    
    .el-menu {
      border-right: none;
      
      .el-menu-item {
        &:hover {
          background: var(--primary-light);
        }
        
        &.is-active {
          background: var(--primary-light);
        }
      }
    }
  }
  
  .main-container {
    flex-direction: column;
    
    .header {
      background: #fff;
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 0 20px;
      box-shadow: var(--shadow-light);
      
      .header-left {
        display: flex;
        align-items: center;
        
        .collapse-btn {
          font-size: 20px;
          cursor: pointer;
          margin-right: 20px;
          color: var(--text-regular);
          
          &:hover {
            color: var(--primary-color);
          }
        }
      }
      
      .header-right {
        .user-info {
          display: flex;
          align-items: center;
          cursor: pointer;
          
          .username {
            margin: 0 8px;
            color: var(--text-primary);
          }
        }
      }
    }
    
    .main-content {
      background: var(--bg-color);
      padding: 20px;
      overflow-y: auto;
    }
  }
}
</style>

