<template>
  <div class="building-manage">
    <h2 class="page-title">楼栋管理</h2>
    
    <!-- 搜索区域 -->
    <div class="search-area">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="楼栋名称">
          <el-input v-model="searchForm.keyword" placeholder="请输入楼栋名称" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>重置
          </el-button>
          <el-button type="success" @click="handleAdd">
            <el-icon><Plus /></el-icon>新增楼栋
          </el-button>
        </el-form-item>
      </el-form>
    </div>
    
    <!-- 表格区域 -->
    <div class="table-area">
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="buildingName" label="楼栋名称" />
        <el-table-column prop="floors" label="楼层数" width="100" />
        <el-table-column prop="units" label="单元数" width="100" />
        <el-table-column prop="description" label="描述" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
            <el-popconfirm title="确定删除该楼栋吗？" @confirm="handleDelete(row.id)">
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
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="楼栋名称" prop="buildingName">
          <el-input v-model="form.buildingName" placeholder="请输入楼栋名称" />
        </el-form-item>
        <el-form-item label="楼层数" prop="floors">
          <el-input-number v-model="form.floors" :min="1" :max="100" />
        </el-form-item>
        <el-form-item label="单元数" prop="units">
          <el-input-number v-model="form.units" :min="1" :max="10" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" placeholder="请输入描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getBuildingList, addBuilding, updateBuilding, deleteBuilding } from '@/api/building'

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)

const searchForm = reactive({
  keyword: ''
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const form = reactive({
  id: null,
  buildingName: '',
  floors: 1,
  units: 1,
  description: ''
})

const rules = {
  buildingName: [{ required: true, message: '请输入楼栋名称', trigger: 'blur' }],
  floors: [{ required: true, message: '请输入楼层数', trigger: 'blur' }],
  units: [{ required: true, message: '请输入单元数', trigger: 'blur' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getBuildingList({
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      keyword: searchForm.keyword
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
  searchForm.keyword = ''
  handleSearch()
}

const resetForm = () => {
  form.id = null
  form.buildingName = ''
  form.floors = 1
  form.units = 1
  form.description = ''
}

const handleAdd = () => {
  resetForm()
  dialogTitle.value = '新增楼栋'
  dialogVisible.value = true
}

const handleEdit = (row) => {
  resetForm()
  Object.assign(form, row)
  dialogTitle.value = '编辑楼栋'
  dialogVisible.value = true
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  
  submitLoading.value = true
  try {
    if (form.id) {
      await updateBuilding(form)
      ElMessage.success('修改成功')
    } else {
      await addBuilding(form)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadData()
  } finally {
    submitLoading.value = false
  }
}

const handleDelete = async (id) => {
  try {
    await deleteBuilding(id)
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
.building-manage {
  .pagination {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }
}
</style>

