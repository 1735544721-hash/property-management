<template>
  <div class="complaint-manage">
    <h2 class="page-title">投诉管理</h2>
    
    <!-- 搜索区域 -->
    <div class="search-area">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="类型">
          <el-select v-model="searchForm.type" placeholder="全部" clearable style="width: 100px">
            <el-option label="投诉" value="投诉" />
            <el-option label="建议" value="建议" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable style="width: 100px">
            <el-option label="待处理" :value="0" />
            <el-option label="已处理" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>重置
          </el-button>
        </el-form-item>
      </el-form>
    </div>
    
    <!-- 表格区域 -->
    <div class="table-area">
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="type" label="类型" width="80">
          <template #default="{ row }">
            <el-tag :type="row.type === '投诉' ? 'danger' : 'success'">{{ row.type }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" min-width="150" />
        <el-table-column prop="userName" label="提交人" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'warning'">
              {{ row.status === 1 ? '已处理' : '待处理' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="handlerName" label="处理人" width="100">
          <template #default="{ row }">{{ row.handlerName || '-' }}</template>
        </el-table-column>
        <el-table-column prop="createTime" label="提交时间" width="180" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleView(row)">查看</el-button>
            <el-button v-if="row.status === 0" type="success" link size="small" @click="handleProcess(row)">处理</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination">
        <el-pagination
          v-model:current-page="pagination.pageNum"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </div>
    
    <!-- 查看/处理对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="类型">
          <el-tag :type="currentComplaint.type === '投诉' ? 'danger' : 'success'">{{ currentComplaint.type }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="currentComplaint.status === 1 ? 'success' : 'warning'">
            {{ currentComplaint.status === 1 ? '已处理' : '待处理' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="标题" :span="2">{{ currentComplaint.title }}</el-descriptions-item>
        <el-descriptions-item label="提交人">{{ currentComplaint.userName }}</el-descriptions-item>
        <el-descriptions-item label="提交时间">{{ currentComplaint.createTime }}</el-descriptions-item>
        <el-descriptions-item label="内容" :span="2">{{ currentComplaint.content }}</el-descriptions-item>
        <el-descriptions-item v-if="currentComplaint.reply" label="回复" :span="2">{{ currentComplaint.reply }}</el-descriptions-item>
      </el-descriptions>
      
      <template v-if="isProcessing">
        <el-divider />
        <el-form ref="processFormRef" :model="processForm" :rules="processRules" label-width="100px">
          <el-form-item label="回复内容" prop="reply">
            <el-input v-model="processForm.reply" type="textarea" :rows="4" placeholder="请输入回复内容" />
          </el-form-item>
        </el-form>
      </template>
      
      <template #footer>
        <el-button @click="dialogVisible = false">关闭</el-button>
        <el-button v-if="isProcessing" type="primary" :loading="submitLoading" @click="handleSubmit">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getComplaintList, handleComplaint } from '@/api/complaint'

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const isProcessing = ref(false)
const currentComplaint = ref({})
const processFormRef = ref(null)

const searchForm = reactive({
  type: '',
  status: null
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const processForm = reactive({
  reply: ''
})

const processRules = {
  reply: [{ required: true, message: '请输入回复内容', trigger: 'blur' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getComplaintList({
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      ...searchForm
    })
    tableData.value = res.data.records || []
    pagination.total = res.data.total || 0
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.pageNum = 1
  loadData()
}

const handleReset = () => {
  searchForm.type = ''
  searchForm.status = null
  handleSearch()
}

const handleView = (row) => {
  currentComplaint.value = row
  dialogTitle.value = '查看详情'
  isProcessing.value = false
  dialogVisible.value = true
}

const handleProcess = (row) => {
  currentComplaint.value = row
  dialogTitle.value = '处理投诉建议'
  isProcessing.value = true
  processForm.reply = ''
  dialogVisible.value = true
}

const handleSubmit = async () => {
  const valid = await processFormRef.value.validate().catch(() => false)
  if (!valid) return
  
  submitLoading.value = true
  try {
    await handleComplaint(currentComplaint.value.id, processForm.reply)
    ElMessage.success('处理成功')
    dialogVisible.value = false
    loadData()
  } finally {
    submitLoading.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>

<style lang="scss" scoped>
.complaint-manage {
  .pagination {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }
}
</style>

