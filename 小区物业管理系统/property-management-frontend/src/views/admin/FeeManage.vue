<template>
  <div class="fee-manage">
    <h2 class="page-title">费用管理</h2>
    
    <!-- 搜索区域 -->
    <div class="search-area">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="费用类型">
          <el-select v-model="searchForm.feeType" placeholder="全部" clearable style="width: 120px">
            <el-option label="物业费" value="物业费" />
            <el-option label="水费" value="水费" />
            <el-option label="电费" value="电费" />
            <el-option label="停车费" value="停车费" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable style="width: 100px">
            <el-option label="未缴" :value="0" />
            <el-option label="已缴" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item label="月份">
          <el-date-picker v-model="searchForm.feeMonth" type="month" format="YYYY-MM" value-format="YYYY-MM" placeholder="选择月份" style="width: 140px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>重置
          </el-button>
          <el-button type="success" @click="handleAdd">
            <el-icon><Plus /></el-icon>录入费用
          </el-button>
        </el-form-item>
      </el-form>
    </div>
    
    <!-- 表格区域 -->
    <div class="table-area">
      <el-table :data="tableData" v-loading="loading" stripe @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="50" :selectable="row => row.status === 0" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="houseInfo" label="房屋" min-width="180" />
        <el-table-column prop="ownerName" label="业主" width="100">
          <template #default="{ row }">{{ row.ownerName || '-' }}</template>
        </el-table-column>
        <el-table-column prop="feeType" label="费用类型" width="100" />
        <el-table-column prop="amount" label="金额" width="100">
          <template #default="{ row }">¥{{ row.amount }}</template>
        </el-table-column>
        <el-table-column prop="feeMonth" label="费用月份" width="100" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'warning'">
              {{ row.status === 1 ? '已缴' : '未缴' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="payTime" label="缴费时间" width="180">
          <template #default="{ row }">{{ row.payTime || '-' }}</template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === 0" type="success" link size="small" @click="handlePay(row)">缴费</el-button>
            <el-popconfirm v-if="row.status === 0" title="确定删除该费用吗？" @confirm="handleDelete(row.id)">
              <template #reference>
                <el-button type="danger" link size="small">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="table-footer">
        <div class="batch-actions" v-if="selectedIds.length">
          <el-button type="success" @click="handleBatchPay">批量缴费 ({{ selectedIds.length }})</el-button>
        </div>
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
    </div>
    
    <!-- 新增对话框 -->
    <el-dialog v-model="dialogVisible" title="录入费用" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="房屋" prop="houseId">
          <el-select v-model="form.houseId" placeholder="请选择房屋" filterable style="width: 100%">
            <el-option v-for="item in houses" :key="item.id" :label="`${item.buildingName}-${item.unitNumber}单元-${item.roomNumber}`" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="费用类型" prop="feeType">
          <el-select v-model="form.feeType" placeholder="请选择费用类型" style="width: 100%">
            <el-option label="物业费" value="物业费" />
            <el-option label="水费" value="水费" />
            <el-option label="电费" value="电费" />
            <el-option label="停车费" value="停车费" />
          </el-select>
        </el-form-item>
        <el-form-item label="金额" prop="amount">
          <el-input-number v-model="form.amount" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="费用月份" prop="feeMonth">
          <el-date-picker v-model="form.feeMonth" type="month" format="YYYY-MM" value-format="YYYY-MM" placeholder="选择月份" style="width: 100%" />
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
import { ElMessage, ElMessageBox } from 'element-plus'
import { getFeeList, addFee, payFee, batchPayFee, deleteFee } from '@/api/fee'
import { getHouseList } from '@/api/house'

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const houses = ref([])
const dialogVisible = ref(false)
const selectedIds = ref([])
const formRef = ref(null)

const searchForm = reactive({
  feeType: '',
  status: null,
  feeMonth: ''
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const form = reactive({
  houseId: null,
  feeType: '',
  amount: 0,
  feeMonth: ''
})

const rules = {
  houseId: [{ required: true, message: '请选择房屋', trigger: 'change' }],
  feeType: [{ required: true, message: '请选择费用类型', trigger: 'change' }],
  amount: [{ required: true, message: '请输入金额', trigger: 'blur' }],
  feeMonth: [{ required: true, message: '请选择费用月份', trigger: 'change' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getFeeList({
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

const loadHouses = async () => {
  try {
    const res = await getHouseList({ pageNum: 1, pageSize: 1000 })
    houses.value = res.data.records || []
  } catch {
    // 忽略错误
  }
}

const handleSearch = () => {
  pagination.pageNum = 1
  loadData()
}

const handleReset = () => {
  searchForm.feeType = ''
  searchForm.status = null
  searchForm.feeMonth = ''
  handleSearch()
}

const handleAdd = () => {
  form.houseId = null
  form.feeType = ''
  form.amount = 0
  form.feeMonth = ''
  dialogVisible.value = true
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  
  submitLoading.value = true
  try {
    await addFee(form)
    ElMessage.success('录入成功')
    dialogVisible.value = false
    loadData()
  } finally {
    submitLoading.value = false
  }
}

const handlePay = async (row) => {
  try {
    await ElMessageBox.confirm('确定缴纳该费用吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await payFee(row.id)
    ElMessage.success('缴费成功')
    loadData()
  } catch {
    // 取消操作
  }
}

const handleSelectionChange = (selection) => {
  selectedIds.value = selection.map(item => item.id)
}

const handleBatchPay = async () => {
  try {
    await ElMessageBox.confirm(`确定缴纳选中的 ${selectedIds.value.length} 条费用吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await batchPayFee(selectedIds.value)
    ElMessage.success('批量缴费成功')
    loadData()
  } catch {
    // 取消操作
  }
}

const handleDelete = async (id) => {
  try {
    await deleteFee(id)
    ElMessage.success('删除成功')
    loadData()
  } catch {
    // 忽略错误
  }
}

onMounted(() => {
  loadHouses()
  loadData()
})
</script>

<style lang="scss" scoped>
.fee-manage {
  .table-footer {
    margin-top: 20px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    
    .batch-actions {
      display: flex;
      gap: 10px;
    }
    
    .pagination {
      display: flex;
      justify-content: flex-end;
    }
  }
}
</style>

