<template>
  <div class="my-fee">
    <h2 class="page-title">我的缴费</h2>
    
    <!-- 未缴费总额 -->
    <div class="unpaid-card card">
      <div class="unpaid-info">
        <span class="label">待缴费用总额</span>
        <span class="amount">¥{{ unpaidTotal }}</span>
      </div>
      <el-button type="primary" :disabled="!selectedIds.length" @click="handleBatchPay">
        缴纳选中费用 ({{ selectedIds.length }})
      </el-button>
    </div>
    
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
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>
    
    <!-- 表格区域 -->
    <div class="table-area">
      <el-table :data="tableData" v-loading="loading" stripe @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="50" :selectable="row => row.status === 0" />
        <el-table-column prop="feeType" label="费用类型" width="100" />
        <el-table-column prop="amount" label="金额" width="120">
          <template #default="{ row }">
            <span class="amount-text">¥{{ row.amount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="feeMonth" label="费用月份" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'warning'">
              {{ row.status === 1 ? '已缴' : '未缴' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="payTime" label="缴费时间" width="180">
          <template #default="{ row }">{{ row.payTime || '-' }}</template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === 0" type="success" link size="small" @click="handlePay(row)">缴费</el-button>
            <span v-else class="paid-text">已缴纳</span>
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMyFees, payFee, batchPayFee, getMyUnpaid } from '@/api/fee'

const loading = ref(false)
const tableData = ref([])
const unpaidTotal = ref('0.00')
const selectedIds = ref([])

const searchForm = reactive({
  feeType: '',
  status: null
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const loadData = async () => {
  loading.value = true
  try {
    const res = await getMyFees({
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      ...searchForm
    })
    tableData.value = res.data?.records || []
    pagination.total = res.data?.total || 0
  } finally {
    loading.value = false
  }
}

const loadUnpaid = async () => {
  try {
    const res = await getMyUnpaid()
    unpaidTotal.value = (res.data || 0).toFixed(2)
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
  handleSearch()
}

const handleSelectionChange = (selection) => {
  selectedIds.value = selection.map(item => item.id)
}

const handlePay = async (row) => {
  try {
    await ElMessageBox.confirm(`确定缴纳 ${row.feeType} ¥${row.amount} 吗？`, '确认缴费', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await payFee(row.id)
    ElMessage.success('缴费成功')
    loadData()
    loadUnpaid()
  } catch {
    // 取消操作
  }
}

const handleBatchPay = async () => {
  if (!selectedIds.value.length) return
  
  try {
    await ElMessageBox.confirm(`确定缴纳选中的 ${selectedIds.value.length} 条费用吗？`, '确认批量缴费', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await batchPayFee(selectedIds.value)
    ElMessage.success('批量缴费成功')
    loadData()
    loadUnpaid()
  } catch {
    // 取消操作
  }
}

onMounted(() => {
  loadData()
  loadUnpaid()
})
</script>

<style lang="scss" scoped>
.my-fee {
  .unpaid-card {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    
    .unpaid-info {
      display: flex;
      flex-direction: column;
      
      .label {
        font-size: 14px;
        color: var(--text-secondary);
        margin-bottom: 8px;
      }
      
      .amount {
        font-size: 32px;
        font-weight: 600;
        color: var(--danger-color);
      }
    }
  }
  
  .amount-text {
    font-weight: 600;
    color: var(--primary-color);
  }
  
  .paid-text {
    color: var(--text-secondary);
    font-size: 12px;
  }
  
  .pagination {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }
}
</style>

