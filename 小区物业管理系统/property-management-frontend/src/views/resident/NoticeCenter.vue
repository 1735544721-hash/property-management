<template>
  <div class="notice-center">
    <h2 class="page-title">公告中心</h2>
    
    <div class="notice-list" v-loading="loading">
      <template v-if="notices.length">
        <div v-for="notice in notices" :key="notice.id" class="notice-item card" @click="handleView(notice)">
          <div class="notice-header">
            <el-icon class="notice-icon"><Bell /></el-icon>
            <h3>{{ notice.title }}</h3>
          </div>
          <div class="notice-meta">
            <span>发布人：{{ notice.publisherName }}</span>
            <span>发布时间：{{ notice.publishTime }}</span>
          </div>
          <div class="notice-content">{{ notice.content }}</div>
        </div>
      </template>
      
      <el-empty v-else description="暂无公告" />
      
      <div class="pagination" v-if="pagination.total > 0">
        <el-pagination
          v-model:current-page="pagination.pageNum"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50]"
          :total="pagination.total"
          layout="total, prev, pager, next"
          @current-change="loadData"
        />
      </div>
    </div>
    
    <!-- 查看对话框 -->
    <el-dialog v-model="dialogVisible" :title="currentNotice.title" width="700px">
      <div class="notice-detail">
        <div class="notice-detail-meta">
          <span>发布人：{{ currentNotice.publisherName }}</span>
          <span>发布时间：{{ currentNotice.publishTime }}</span>
        </div>
        <el-divider />
        <div class="notice-detail-content">{{ currentNotice.content }}</div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getNoticeList } from '@/api/notice'

const loading = ref(false)
const notices = ref([])
const dialogVisible = ref(false)
const currentNotice = ref({})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const loadData = async () => {
  loading.value = true
  try {
    const res = await getNoticeList({
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      status: 1 // 只查询已发布的
    })
    notices.value = res.data.records || []
    pagination.total = res.data.total || 0
  } finally {
    loading.value = false
  }
}

const handleView = (notice) => {
  currentNotice.value = notice
  dialogVisible.value = true
}

onMounted(() => {
  loadData()
})
</script>

<style lang="scss" scoped>
.notice-center {
  .notice-list {
    display: flex;
    flex-direction: column;
    gap: 16px;
  }
  
  .notice-item {
    cursor: pointer;
    transition: all 0.3s;
    
    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
    }
    
    .notice-header {
      display: flex;
      align-items: center;
      margin-bottom: 12px;
      
      .notice-icon {
        font-size: 24px;
        color: var(--warning-color);
        margin-right: 12px;
      }
      
      h3 {
        font-size: 18px;
        color: var(--text-primary);
      }
    }
    
    .notice-meta {
      display: flex;
      gap: 24px;
      font-size: 12px;
      color: var(--text-secondary);
      margin-bottom: 12px;
    }
    
    .notice-content {
      color: var(--text-regular);
      line-height: 1.6;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }
  }
  
  .notice-detail {
    .notice-detail-meta {
      display: flex;
      gap: 24px;
      font-size: 14px;
      color: var(--text-secondary);
    }
    
    .notice-detail-content {
      white-space: pre-wrap;
      line-height: 1.8;
      color: var(--text-primary);
    }
  }
  
  .pagination {
    margin-top: 20px;
    display: flex;
    justify-content: center;
  }
}
</style>

