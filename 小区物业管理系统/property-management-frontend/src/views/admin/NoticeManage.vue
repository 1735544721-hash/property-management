<template>
  <div class="notice-manage">
    <h2 class="page-title">公告管理</h2>
    
    <!-- 搜索区域 -->
    <div class="search-area">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="标题">
          <el-input v-model="searchForm.title" placeholder="请输入标题" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable style="width: 100px">
            <el-option label="草稿" :value="0" />
            <el-option label="已发布" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>重置
          </el-button>
          <el-button type="success" @click="handleAdd">
            <el-icon><Plus /></el-icon>新增公告
          </el-button>
        </el-form-item>
      </el-form>
    </div>
    
    <!-- 表格区域 -->
    <div class="table-area">
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" min-width="200" />
        <el-table-column prop="publisherName" label="发布人" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '已发布' : '草稿' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="publishTime" label="发布时间" width="180">
          <template #default="{ row }">{{ row.publishTime || '-' }}</template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleView(row)">查看</el-button>
            <el-button type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button v-if="row.status === 0" type="success" link size="small" @click="handlePublish(row)">发布</el-button>
            <el-popconfirm title="确定删除该公告吗？" @confirm="handleDelete(row.id)">
              <template #reference>
                <el-button type="danger" link size="small">删除</el-button>
              </template>
            </el-popconfirm>
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
    
    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="700px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入公告标题" />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="10" placeholder="请输入公告内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">保存</el-button>
        <el-button v-if="!form.id" type="success" :loading="submitLoading" @click="handleSubmitAndPublish">保存并发布</el-button>
      </template>
    </el-dialog>
    
    <!-- 查看对话框 -->
    <el-dialog v-model="viewDialogVisible" title="查看公告" width="700px">
      <h2 style="text-align: center; margin-bottom: 20px;">{{ currentNotice.title }}</h2>
      <div style="color: #909399; text-align: center; margin-bottom: 20px;">
        发布人：{{ currentNotice.publisherName }} | 发布时间：{{ currentNotice.publishTime || '未发布' }}
      </div>
      <div style="white-space: pre-wrap; line-height: 1.8;">{{ currentNotice.content }}</div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getNoticeList, addNotice, updateNotice, publishNotice, deleteNotice } from '@/api/notice'

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const viewDialogVisible = ref(false)
const dialogTitle = ref('')
const currentNotice = ref({})
const formRef = ref(null)

const searchForm = reactive({
  title: '',
  status: null
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const form = reactive({
  id: null,
  title: '',
  content: ''
})

const rules = {
  title: [{ required: true, message: '请输入公告标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入公告内容', trigger: 'blur' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getNoticeList({
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
  searchForm.title = ''
  searchForm.status = null
  handleSearch()
}

const resetForm = () => {
  form.id = null
  form.title = ''
  form.content = ''
}

const handleAdd = () => {
  resetForm()
  dialogTitle.value = '新增公告'
  dialogVisible.value = true
}

const handleEdit = (row) => {
  resetForm()
  Object.assign(form, row)
  dialogTitle.value = '编辑公告'
  dialogVisible.value = true
}

const handleView = (row) => {
  currentNotice.value = row
  viewDialogVisible.value = true
}

const handleSubmit = async (andPublish = false) => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  
  submitLoading.value = true
  try {
    if (form.id) {
      await updateNotice(form)
      ElMessage.success('保存成功')
    } else {
      await addNotice(form)
      ElMessage.success('保存成功')
    }
    dialogVisible.value = false
    loadData()
  } finally {
    submitLoading.value = false
  }
}

const handleSubmitAndPublish = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  
  submitLoading.value = true
  try {
    const res = await addNotice(form)
    await publishNotice(res.data)
    ElMessage.success('发布成功')
    dialogVisible.value = false
    loadData()
  } finally {
    submitLoading.value = false
  }
}

const handlePublish = async (row) => {
  try {
    await ElMessageBox.confirm('确定发布该公告吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await publishNotice(row.id)
    ElMessage.success('发布成功')
    loadData()
  } catch {
    // 取消操作
  }
}

const handleDelete = async (id) => {
  try {
    await deleteNotice(id)
    ElMessage.success('删除成功')
    loadData()
  } catch {
    // 忽略错误
  }
}

onMounted(() => {
  loadData()
})
</script>

<style lang="scss" scoped>
.notice-manage {
  .pagination {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }
}
</style>

