<template>
  <div class="dashboard">
    <!-- 管理员/物业员工工作台 -->
    <template v-if="!userStore.isResident">
      <!-- 统计卡片 -->
      <el-row :gutter="20" class="stat-cards">
        <el-col :span="6">
          <div class="stat-card users">
            <div class="stat-icon">
              <el-icon><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.residentCount || 0 }}</div>
              <div class="stat-label">业主数量</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card houses">
            <div class="stat-icon">
              <el-icon><House /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.occupiedHouseCount || 0 }}/{{ stats.houseCount || 0 }}</div>
              <div class="stat-label">入住/总房屋</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card repairs">
            <div class="stat-icon">
              <el-icon><SetUp /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.pendingRepairCount || 0 }}</div>
              <div class="stat-label">待处理报修</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card fees">
            <div class="stat-icon">
              <el-icon><Wallet /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">¥{{ formatMoney(stats.unpaidTotal) }}</div>
              <div class="stat-label">待缴费用</div>
            </div>
          </div>
        </el-col>
      </el-row>
      
      <!-- 快捷入口 -->
      <div class="quick-actions card">
        <h3 class="section-title">快捷入口</h3>
        <div class="action-grid">
          <div class="action-item" @click="$router.push('/repair')">
            <el-icon><SetUp /></el-icon>
            <span>报修处理</span>
            <el-badge :value="stats.pendingRepairCount" v-if="stats.pendingRepairCount" />
          </div>
          <div class="action-item" @click="$router.push('/complaint')">
            <el-icon><ChatLineSquare /></el-icon>
            <span>投诉处理</span>
            <el-badge :value="stats.pendingComplaintCount" v-if="stats.pendingComplaintCount" />
          </div>
          <div class="action-item" @click="$router.push('/fee')">
            <el-icon><Wallet /></el-icon>
            <span>费用管理</span>
          </div>
          <div class="action-item" @click="$router.push('/notice')">
            <el-icon><Bell /></el-icon>
            <span>发布公告</span>
          </div>
        </div>
      </div>
      
      <!-- 最新公告 -->
      <div class="latest-notices card">
        <h3 class="section-title">最新公告</h3>
        <el-table :data="notices" style="width: 100%">
          <el-table-column prop="title" label="标题" />
          <el-table-column prop="publisherName" label="发布人" width="120" />
          <el-table-column prop="publishTime" label="发布时间" width="180" />
        </el-table>
      </div>
    </template>
    
    <!-- 业主工作台 -->
    <template v-else>
      <!-- 欢迎区域 -->
      <div class="welcome-card card">
        <div class="welcome-content">
          <div class="welcome-text">
            <h2>您好，{{ userStore.userInfo.realName }}！</h2>
            <p>欢迎使用小区物业管理系统</p>
          </div>
          <img src="https://images.unsplash.com/photo-1560518883-ce09059eeffa?w=200&h=150&fit=crop" alt="Welcome" class="welcome-img" />
        </div>
      </div>
      
      <!-- 快捷入口 -->
      <div class="quick-actions card">
        <h3 class="section-title">常用服务</h3>
        <div class="action-grid resident">
          <div class="action-item" @click="$router.push('/my-repair')">
            <el-icon><SetUp /></el-icon>
            <span>在线报修</span>
          </div>
          <div class="action-item" @click="$router.push('/my-fee')">
            <el-icon><Wallet /></el-icon>
            <span>费用缴纳</span>
          </div>
          <div class="action-item" @click="$router.push('/notice-center')">
            <el-icon><Bell /></el-icon>
            <span>公告中心</span>
          </div>
          <div class="action-item" @click="$router.push('/my-complaint')">
            <el-icon><ChatLineSquare /></el-icon>
            <span>投诉建议</span>
          </div>
        </div>
      </div>
      
      <!-- 最新公告 -->
      <div class="latest-notices card">
        <div class="section-header">
          <h3 class="section-title">最新公告</h3>
          <el-button type="primary" link @click="$router.push('/notice-center')">查看更多</el-button>
        </div>
        <div class="notice-list">
          <div v-for="notice in notices" :key="notice.id" class="notice-item" @click="showNotice(notice)">
            <el-icon><Bell /></el-icon>
            <span class="notice-title">{{ notice.title }}</span>
            <span class="notice-time">{{ notice.publishTime }}</span>
          </div>
          <el-empty v-if="!notices.length" description="暂无公告" />
        </div>
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { getDashboardStats } from '@/api/dashboard'
import { getLatestNotices } from '@/api/notice'

const userStore = useUserStore()

const stats = ref({})
const notices = ref([])

const formatMoney = (value) => {
  if (!value) return '0.00'
  return Number(value).toFixed(2)
}

const showNotice = (notice) => {
  ElMessageBox.alert(notice.content, notice.title, {
    confirmButtonText: '关闭',
    dangerouslyUseHTMLString: true
  })
}

onMounted(async () => {
  // 获取最新公告
  try {
    const res = await getLatestNotices(5)
    notices.value = res.data || []
  } catch {
    // 忽略错误
  }
  
  // 管理员获取统计数据
  if (!userStore.isResident) {
    try {
      const res = await getDashboardStats()
      stats.value = res.data || {}
    } catch {
      // 忽略错误
    }
  }
})
</script>

<style lang="scss" scoped>
.dashboard {
  .stat-cards {
    margin-bottom: 20px;
    
    .stat-card {
      background: #fff;
      border-radius: var(--radius-base);
      padding: 24px;
      display: flex;
      align-items: center;
      box-shadow: var(--shadow-light);
      
      .stat-icon {
        width: 60px;
        height: 60px;
        border-radius: 12px;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-right: 16px;
        
        .el-icon {
          font-size: 28px;
          color: #fff;
        }
      }
      
      .stat-info {
        .stat-value {
          font-size: 24px;
          font-weight: 600;
          color: var(--text-primary);
        }
        
        .stat-label {
          font-size: 14px;
          color: var(--text-secondary);
          margin-top: 4px;
        }
      }
      
      &.users .stat-icon { background: linear-gradient(135deg, #667eea, #764ba2); }
      &.houses .stat-icon { background: linear-gradient(135deg, #11998e, #38ef7d); }
      &.repairs .stat-icon { background: linear-gradient(135deg, #f093fb, #f5576c); }
      &.fees .stat-icon { background: linear-gradient(135deg, #4facfe, #00f2fe); }
    }
  }
  
  .card {
    background: #fff;
    border-radius: var(--radius-base);
    padding: 24px;
    margin-bottom: 20px;
    box-shadow: var(--shadow-light);
  }
  
  .section-title {
    font-size: 16px;
    font-weight: 600;
    color: var(--text-primary);
    margin-bottom: 16px;
    padding-left: 10px;
    border-left: 3px solid var(--primary-color);
  }
  
  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
    
    .section-title {
      margin-bottom: 0;
    }
  }
  
  .quick-actions {
    .action-grid {
      display: grid;
      grid-template-columns: repeat(4, 1fr);
      gap: 16px;
      
      &.resident {
        grid-template-columns: repeat(4, 1fr);
      }
      
      .action-item {
        display: flex;
        flex-direction: column;
        align-items: center;
        padding: 24px;
        background: var(--bg-color);
        border-radius: var(--radius-base);
        cursor: pointer;
        transition: all 0.3s;
        position: relative;
        
        .el-icon {
          font-size: 32px;
          color: var(--primary-color);
          margin-bottom: 12px;
        }
        
        span {
          font-size: 14px;
          color: var(--text-primary);
        }
        
        .el-badge {
          position: absolute;
          top: 10px;
          right: 10px;
        }
        
        &:hover {
          background: var(--primary-color);
          transform: translateY(-4px);
          box-shadow: 0 8px 20px rgba(30, 58, 95, 0.3);
          
          .el-icon, span {
            color: #fff;
          }
        }
      }
    }
  }
  
  .welcome-card {
    .welcome-content {
      display: flex;
      justify-content: space-between;
      align-items: center;
      
      .welcome-text {
        h2 {
          font-size: 24px;
          color: var(--primary-color);
          margin-bottom: 8px;
        }
        
        p {
          color: var(--text-secondary);
        }
      }
      
      .welcome-img {
        width: 200px;
        height: 120px;
        border-radius: var(--radius-base);
        object-fit: cover;
      }
    }
  }
  
  .notice-list {
    .notice-item {
      display: flex;
      align-items: center;
      padding: 16px 0;
      border-bottom: 1px solid var(--border-light);
      cursor: pointer;
      
      &:last-child {
        border-bottom: none;
      }
      
      &:hover {
        .notice-title {
          color: var(--primary-color);
        }
      }
      
      .el-icon {
        color: var(--warning-color);
        margin-right: 12px;
      }
      
      .notice-title {
        flex: 1;
        color: var(--text-primary);
        transition: color 0.3s;
      }
      
      .notice-time {
        color: var(--text-secondary);
        font-size: 12px;
      }
    }
  }
}
</style>

