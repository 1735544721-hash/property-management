<template>
  <div class="my-repair">
    <h2 class="page-title">我的报修</h2>
    
    <!-- 操作区域 -->
    <div class="action-area card">
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>提交报修
      </el-button>
    </div>
    
    <!-- 报修列表 -->
    <div class="table-area">
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="报修标题" min-width="150" />
        <el-table-column prop="houseInfo" label="房屋" width="180" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="报修时间" width="180" />
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
    <el-dialog v-model="dialogVisible" title="提交报修" width="600px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="房屋" prop="houseId">
          <el-select v-model="form.houseId" placeholder="请选择房屋" style="width: 100%">
            <el-option v-for="item in houses" :key="item.id" :label="`${item.buildingName}-${item.unitNumber}单元-${item.roomNumber}`" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="报修标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入报修标题" />
        </el-form-item>
        <el-form-item label="报修内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="4" placeholder="请详细描述报修问题" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">提交</el-button>
      </template>
    </el-dialog>
    
    <!-- 查看对话框 -->
    <el-dialog v-model="viewDialogVisible" title="报修详情" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="报修标题" :span="2">{{ currentRepair.title }}</el-descriptions-item>
        <el-descriptions-item label="房屋">{{ currentRepair.houseInfo }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentRepair.status)">{{ getStatusText(currentRepair.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="报修时间">{{ currentRepair.createTime }}</el-descriptions-item>
        <el-descriptions-item label="处理人">{{ currentRepair.handlerName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="报修内容" :span="2">{{ currentRepair.content }}</el-descriptions-item>
        <el-descriptions-item v-if="currentRepair.handleResult" label="处理结果" :span="2">{{ currentRepair.handleResult }}</el-descriptions-item>
        <el-descriptions-item v-if="currentRepair.handleTime" label="处理时间" :span="2">{{ currentRepair.handleTime }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getMyRepairs, addRepair } from '@/api/repair'
import { getMyHouses } from '@/api/house'

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const houses = ref([])
const dialogVisible = ref(false)
const viewDialogVisible = ref(false)
const currentRepair = ref({})
const formRef = ref(null)

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const form = reactive({
  houseId: null,
  title: '',
  content: ''
})

const rules = {
  houseId: [{ required: true, message: '请选择房屋', trigger: 'change' }],
  title: [{ required: true, message: '请输入报修标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入报修内容', trigger: 'blur' }]
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
    const res = await getMyRepairs({
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    })
    tableData.value = res.data.records || []
    pagination.total = res.data.total || 0
  } finally {
    loading.value = false
  }
}

const loadHouses = async () => {
  try {
    const res = await getMyHouses()
    houses.value = res.data || []
  } catch {
    // 忽略错误
  }
}

const handleAdd = () => {
  form.houseId = houses.value.length ? houses.value[0].id : null
  form.title = ''
  form.content = ''
  dialogVisible.value = true
}

const handleView = (row) => {
  currentRepair.value = row
  viewDialogVisible.value = true
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  
  submitLoading.value = true
  try {
    await addRepair(form)
    ElMessage.success('提交成功')
    dialogVisible.value = false
    loadData()
  } finally {
    submitLoading.value = false
  }
}

onMounted(() => {
  loadHouses()
  loadData()
})
</script>

<style lang="scss" scoped>
.my-repair {
  .action-area {
    margin-bottom: 20px;
  }
  
  .pagination {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }
}
</style>

