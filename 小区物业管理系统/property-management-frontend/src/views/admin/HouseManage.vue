<template>
  <div class="house-manage">
    <h2 class="page-title">房屋管理</h2>
    
    <!-- 搜索区域 -->
    <div class="search-area">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="楼栋">
          <el-select v-model="searchForm.buildingId" placeholder="全部" clearable style="width: 140px">
            <el-option v-for="item in buildings" :key="item.id" :label="item.buildingName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable style="width: 100px">
            <el-option label="空置" :value="0" />
            <el-option label="已入住" :value="1" />
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
            <el-icon><Plus /></el-icon>新增房屋
          </el-button>
        </el-form-item>
      </el-form>
    </div>
    
    <!-- 表格区域 -->
    <div class="table-area">
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="buildingName" label="楼栋" width="100" />
        <el-table-column prop="unitNumber" label="单元" width="80">
          <template #default="{ row }">{{ row.unitNumber }}单元</template>
        </el-table-column>
        <el-table-column prop="floorNumber" label="楼层" width="80">
          <template #default="{ row }">{{ row.floorNumber }}层</template>
        </el-table-column>
        <el-table-column prop="roomNumber" label="房间号" width="100" />
        <el-table-column prop="area" label="面积(㎡)" width="100" />
        <el-table-column prop="ownerName" label="业主" width="100">
          <template #default="{ row }">{{ row.ownerName || '-' }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '已入住' : '空置' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button v-if="!row.ownerId" type="success" link size="small" @click="handleBind(row)">绑定业主</el-button>
            <el-button v-else type="warning" link size="small" @click="handleUnbind(row)">解绑</el-button>
            <el-popconfirm title="确定删除该房屋吗？" @confirm="handleDelete(row.id)">
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
        <el-form-item label="楼栋" prop="buildingId">
          <el-select v-model="form.buildingId" placeholder="请选择楼栋" style="width: 100%">
            <el-option v-for="item in buildings" :key="item.id" :label="item.buildingName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="单元号" prop="unitNumber">
          <el-input-number v-model="form.unitNumber" :min="1" :max="10" />
        </el-form-item>
        <el-form-item label="楼层号" prop="floorNumber">
          <el-input-number v-model="form.floorNumber" :min="1" :max="100" />
        </el-form-item>
        <el-form-item label="房间号" prop="roomNumber">
          <el-input v-model="form.roomNumber" placeholder="请输入房间号" />
        </el-form-item>
        <el-form-item label="面积(㎡)" prop="area">
          <el-input-number v-model="form.area" :min="0" :precision="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
    
    <!-- 绑定业主对话框 -->
    <el-dialog v-model="bindDialogVisible" title="绑定业主" width="400px">
      <el-form ref="bindFormRef" :model="bindForm" :rules="bindRules" label-width="80px">
        <el-form-item label="业主" prop="ownerId">
          <el-select v-model="bindForm.ownerId" placeholder="请选择业主" filterable style="width: 100%">
            <el-option v-for="item in residents" :key="item.id" :label="`${item.realName}(${item.username})`" :value="item.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="bindDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="bindLoading" @click="handleBindSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getHouseList, addHouse, updateHouse, deleteHouse, bindOwner, unbindOwner } from '@/api/house'
import { getAllBuildings } from '@/api/building'
import { getUserList } from '@/api/user'

const loading = ref(false)
const submitLoading = ref(false)
const bindLoading = ref(false)
const tableData = ref([])
const buildings = ref([])
const residents = ref([])
const dialogVisible = ref(false)
const bindDialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)
const bindFormRef = ref(null)
const currentHouseId = ref(null)

const searchForm = reactive({
  buildingId: null,
  status: null
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const form = reactive({
  id: null,
  buildingId: null,
  unitNumber: 1,
  floorNumber: 1,
  roomNumber: '',
  area: 0
})

const bindForm = reactive({
  ownerId: null
})

const rules = {
  buildingId: [{ required: true, message: '请选择楼栋', trigger: 'change' }],
  unitNumber: [{ required: true, message: '请输入单元号', trigger: 'blur' }],
  floorNumber: [{ required: true, message: '请输入楼层号', trigger: 'blur' }],
  roomNumber: [{ required: true, message: '请输入房间号', trigger: 'blur' }]
}

const bindRules = {
  ownerId: [{ required: true, message: '请选择业主', trigger: 'change' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getHouseList({
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

const loadBuildings = async () => {
  try {
    const res = await getAllBuildings()
    buildings.value = res.data || []
  } catch {
    // 忽略错误
  }
}

const loadResidents = async () => {
  try {
    const res = await getUserList({ pageNum: 1, pageSize: 1000, role: 'resident' })
    residents.value = res.data.records || []
  } catch {
    // 忽略错误
  }
}

const handleSearch = () => {
  pagination.pageNum = 1
  loadData()
}

const handleReset = () => {
  searchForm.buildingId = null
  searchForm.status = null
  handleSearch()
}

const resetForm = () => {
  form.id = null
  form.buildingId = null
  form.unitNumber = 1
  form.floorNumber = 1
  form.roomNumber = ''
  form.area = 0
}

const handleAdd = () => {
  resetForm()
  dialogTitle.value = '新增房屋'
  dialogVisible.value = true
}

const handleEdit = (row) => {
  resetForm()
  Object.assign(form, row)
  dialogTitle.value = '编辑房屋'
  dialogVisible.value = true
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  
  submitLoading.value = true
  try {
    if (form.id) {
      await updateHouse(form)
      ElMessage.success('修改成功')
    } else {
      await addHouse(form)
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
    await deleteHouse(id)
    ElMessage.success('删除成功')
    loadData()
  } catch {
    // 忽略错误
  }
}

const handleBind = (row) => {
  currentHouseId.value = row.id
  bindForm.ownerId = null
  bindDialogVisible.value = true
}

const handleBindSubmit = async () => {
  const valid = await bindFormRef.value.validate().catch(() => false)
  if (!valid) return
  
  bindLoading.value = true
  try {
    await bindOwner(currentHouseId.value, bindForm.ownerId)
    ElMessage.success('绑定成功')
    bindDialogVisible.value = false
    loadData()
  } finally {
    bindLoading.value = false
  }
}

const handleUnbind = async (row) => {
  try {
    await ElMessageBox.confirm('确定解绑该房屋的业主吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await unbindOwner(row.id)
    ElMessage.success('解绑成功')
    loadData()
  } catch {
    // 取消操作
  }
}

onMounted(() => {
  loadBuildings()
  loadResidents()
  loadData()
})
</script>

<style lang="scss" scoped>
.house-manage {
  .pagination {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }
}
</style>

