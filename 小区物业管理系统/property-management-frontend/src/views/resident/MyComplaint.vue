<template>
  <div class="my-complaint">
    <h2 class="page-title">投诉建议</h2>
    
    <!-- 操作区域 -->
    <div class="action-area card">
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>提交投诉建议
      </el-button>
    </div>
    
    <!-- 列表 -->
    <div class="table-area">
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="type" label="类型" width="80">
          <template #default="{ row }">
            <el-tag :type="row.type === '投诉' ? 'danger' : 'success'">{{ row.type }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" min-width="150" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'warning'">
              {{ row.status === 1 ? '已处理' : '待处理' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="提交时间" width="180" />
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleView(row)">查看</el-button>
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
    
    <!-- 新增对话框 -->
    <el-dialog v-model="dialogVisible" title="提交投诉建议" width="600px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="类型" prop="type">
          <el-radio-group v-model="form.type">
            <el-radio value="投诉">投诉</el-radio>
            <el-radio value="建议">建议</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="6" placeholder="请详细描述您的投诉或建议" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">提交</el-button>
      </template>
    </el-dialog>
    
    <!-- 查看对话框 -->
    <el-dialog v-model="viewDialogVisible" title="投诉建议详情" width="600px">
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
        <el-descriptions-item label="提交时间" :span="2">{{ currentComplaint.createTime }}</el-descriptions-item>
        <el-descriptions-item label="内容" :span="2">{{ currentComplaint.content }}</el-descriptions-item>
        <el-descriptions-item v-if="currentComplaint.reply" label="物业回复" :span="2">
          <div class="reply-content">{{ currentComplaint.reply }}</div>
        </el-descriptions-item>
        <el-descriptions-item v-if="currentComplaint.handleTime" label="处理时间" :span="2">{{ currentComplaint.handleTime }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getMyComplaints, addComplaint } from '@/api/complaint'

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const viewDialogVisible = ref(false)
const currentComplaint = ref({})
const formRef = ref(null)

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const form = reactive({
  type: '建议',
  title: '',
  content: ''
})

const rules = {
  type: [{ required: true, message: '请选择类型', trigger: 'change' }],
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getMyComplaints({
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    })
    tableData.value = res.data.records || []
    pagination.total = res.data.total || 0
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  form.type = '建议'
  form.title = ''
  form.content = ''
  dialogVisible.value = true
}

const handleView = (row) => {
  currentComplaint.value = row
  viewDialogVisible.value = true
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  
  submitLoading.value = true
  try {
    await addComplaint(form)
    ElMessage.success('提交成功')
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
.my-complaint {
  .action-area {
    margin-bottom: 20px;
  }
  
  .pagination {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }
  
  .reply-content {
    background: #f5f7fa;
    padding: 12px;
    border-radius: 4px;
    color: var(--success-color);
  }
}
</style>

