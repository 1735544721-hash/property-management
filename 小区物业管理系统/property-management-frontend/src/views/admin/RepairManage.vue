<template>
  <div class="repair-manage">
    <h2 class="page-title">报修管理</h2>
    
    <!-- 搜索区域 -->
    <div class="search-area">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable style="width: 120px">
            <el-option label="待处理" :value="0" />
            <el-option label="处理中" :value="1" />
            <el-option label="已完成" :value="2" />
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
        <el-table-column prop="title" label="报修标题" min-width="150" />
        <el-table-column prop="userName" label="报修人" width="100" />
        <el-table-column prop="houseInfo" label="房屋" width="180" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="handlerName" label="处理人" width="100">
          <template #default="{ row }">{{ row.handlerName || '-' }}</template>
        </el-table-column>
        <el-table-column prop="createTime" label="报修时间" width="180" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleView(row)">查看</el-button>
            <el-button v-if="row.status !== 2" type="success" link size="small" @click="handleProcess(row)">处理</el-button>
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
        <el-descriptions-item label="报修标题" :span="2">{{ currentRepair.title }}</el-descriptions-item>
        <el-descriptions-item label="报修人">{{ currentRepair.userName }}</el-descriptions-item>
        <el-descriptions-item label="房屋">{{ currentRepair.houseInfo }}</el-descriptions-item>
        <el-descriptions-item label="报修时间">{{ currentRepair.createTime }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentRepair.status)">{{ getStatusText(currentRepair.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="报修内容" :span="2">{{ currentRepair.content }}</el-descriptions-item>
        <el-descriptions-item v-if="currentRepair.handleResult" label="处理结果" :span="2">{{ currentRepair.handleResult }}</el-descriptions-item>
      </el-descriptions>
      
      <template v-if="isProcessing">
        <el-divider />
        <el-form ref="processFormRef" :model="processForm" :rules="processRules" label-width="100px">
          <el-form-item label="处理状态" prop="status">
            <el-radio-group v-model="processForm.status">
              <el-radio :value="1">处理中</el-radio>
              <el-radio :value="2">已完成</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="处理结果" prop="handleResult">
            <el-input v-model="processForm.handleResult" type="textarea" :rows="3" placeholder="请输入处理结果" />
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
import { getRepairList, handleRepair } from '@/api/repair'

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const isProcessing = ref(false)
const currentRepair = ref({})
const processFormRef = ref(null)

const searchForm = reactive({
  status: null
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const processForm = reactive({
  status: 1,
  handleResult: ''
})

const processRules = {
  status: [{ required: true, message: '请选择处理状态', trigger: 'change' }],
  handleResult: [{ required: true, message: '请输入处理结果', trigger: 'blur' }]
}

const getStatusText = (status) => {
  const texts = { 0: '待处理', 1: '处理中', 2: '已完成' }
  return texts[status] || '未知'
}

const getStatusType = (status) => {
  const types = { 0: 'warning', 1: 'primary', 2: 'success' }
  return types[status] || ''
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getRepairList({
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
  searchForm.status = null
  handleSearch()
}

const handleView = (row) => {
  currentRepair.value = row
  dialogTitle.value = '查看报修'
  isProcessing.value = false
  dialogVisible.value = true
}

const handleProcess = (row) => {
  currentRepair.value = row
  dialogTitle.value = '处理报修'
  isProcessing.value = true
  processForm.status = row.status === 0 ? 1 : row.status
  processForm.handleResult = row.handleResult || ''
  dialogVisible.value = true
}

const handleSubmit = async () => {
  const valid = await processFormRef.value.validate().catch(() => false)
  if (!valid) return
  
  submitLoading.value = true
  try {
    await handleRepair(currentRepair.value.id, processForm.handleResult, processForm.status)
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
.repair-manage {
  .pagination {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }
}
</style>

