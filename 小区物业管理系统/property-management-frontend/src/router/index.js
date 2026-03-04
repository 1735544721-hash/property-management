import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/auth/Login.vue'),
    meta: { title: '登录', requiresAuth: false }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/auth/Register.vue'),
    meta: { title: '注册', requiresAuth: false }
  },
  {
    path: '/',
    component: () => import('@/layouts/MainLayout.vue'),
    redirect: '/dashboard',
    meta: { requiresAuth: true },
    children: [
      // 仪表盘
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/common/Dashboard.vue'),
        meta: { title: '工作台', icon: 'Odometer' }
      },
      // 用户管理（管理员）
      {
        path: 'user',
        name: 'UserManage',
        component: () => import('@/views/admin/UserManage.vue'),
        meta: { title: '用户管理', icon: 'User', roles: ['admin'] }
      },
      // 楼栋管理
      {
        path: 'building',
        name: 'BuildingManage',
        component: () => import('@/views/admin/BuildingManage.vue'),
        meta: { title: '楼栋管理', icon: 'OfficeBuilding', roles: ['admin', 'staff'] }
      },
      // 房屋管理
      {
        path: 'house',
        name: 'HouseManage',
        component: () => import('@/views/admin/HouseManage.vue'),
        meta: { title: '房屋管理', icon: 'House', roles: ['admin', 'staff'] }
      },
      // 报修管理
      {
        path: 'repair',
        name: 'RepairManage',
        component: () => import('@/views/admin/RepairManage.vue'),
        meta: { title: '报修管理', icon: 'SetUp', roles: ['admin', 'staff'] }
      },
      // 费用管理
      {
        path: 'fee',
        name: 'FeeManage',
        component: () => import('@/views/admin/FeeManage.vue'),
        meta: { title: '费用管理', icon: 'Wallet', roles: ['admin', 'staff'] }
      },
      // 公告管理
      {
        path: 'notice',
        name: 'NoticeManage',
        component: () => import('@/views/admin/NoticeManage.vue'),
        meta: { title: '公告管理', icon: 'Bell', roles: ['admin', 'staff'] }
      },
      // 投诉管理
      {
        path: 'complaint',
        name: 'ComplaintManage',
        component: () => import('@/views/admin/ComplaintManage.vue'),
        meta: { title: '投诉管理', icon: 'ChatLineSquare', roles: ['admin', 'staff'] }
      },
      // 业主端 - 我的房产
      {
        path: 'my-house',
        name: 'MyHouse',
        component: () => import('@/views/resident/MyHouse.vue'),
        meta: { title: '我的房产', icon: 'House', roles: ['resident'] }
      },
      // 业主端 - 我的报修
      {
        path: 'my-repair',
        name: 'MyRepair',
        component: () => import('@/views/resident/MyRepair.vue'),
        meta: { title: '我的报修', icon: 'SetUp', roles: ['resident'] }
      },
      // 业主端 - 我的缴费
      {
        path: 'my-fee',
        name: 'MyFee',
        component: () => import('@/views/resident/MyFee.vue'),
        meta: { title: '我的缴费', icon: 'Wallet', roles: ['resident'] }
      },
      // 业主端 - 公告中心
      {
        path: 'notice-center',
        name: 'NoticeCenter',
        component: () => import('@/views/resident/NoticeCenter.vue'),
        meta: { title: '公告中心', icon: 'Bell', roles: ['resident'] }
      },
      // 业主端 - 投诉建议
      {
        path: 'my-complaint',
        name: 'MyComplaint',
        component: () => import('@/views/resident/MyComplaint.vue'),
        meta: { title: '投诉建议', icon: 'ChatLineSquare', roles: ['resident'] }
      },
      // 个人中心
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/common/Profile.vue'),
        meta: { title: '个人中心', icon: 'User' }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/common/NotFound.vue'),
    meta: { title: '404', requiresAuth: false }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 设置页面标题
  document.title = to.meta.title ? `${to.meta.title} - 小区物业管理系统` : '小区物业管理系统'
  
  const userStore = useUserStore()
  
  // 不需要认证的页面直接放行
  if (to.meta.requiresAuth === false) {
    // 已登录用户访问登录页，跳转到首页
    if (to.path === '/login' && userStore.isLoggedIn) {
      next('/dashboard')
      return
    }
    next()
    return
  }
  
  // 需要认证但未登录，跳转到登录页
  if (!userStore.isLoggedIn) {
    next('/login')
    return
  }
  
  // 检查角色权限
  const roles = to.meta.roles
  if (roles && roles.length > 0) {
    const userRole = userStore.userInfo.role
    if (!roles.includes(userRole)) {
      next('/dashboard')
      return
    }
  }
  
  next()
})

export default router

