<template>
  <div class="user-manage">
    <h2 class="page-title">用户管理</h2>
    
    <!-- 搜索区域 -->
    <div class="search-area">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="角色">
          <el-select v-model="searchForm.role" placeholder="全部" clearable style="width: 120px">
            <el-option label="管理员" value="admin" />
            <el-option label="物业员工" value="staff" />
            <el-option label="业主" value="resident" />
          </el-select>
        </el-form-item>
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="用户名/姓名/手机号" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>重置
          </el-button>
          <el-button type="success" @click="handleAdd">
            <el-icon><Plus /></el-icon>新增用户
          </el-button>
        </el-form-item>
      </el-form>
    </div>
    
    <!-- 表格区域 -->
    <div class="table-area">
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="realName" label="真实姓名" width="120" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column prop="role" label="角色" width="100">
          <template #default="{ row }">
            <el-tag :type="getRoleType(row.role)">{{ getRoleName(row.role) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              :active-value="1"
              :inactive-value="0"
              :disabled="row.role === 'admin'"
              @change="handleStatusChange(row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="warning" link size="small" @click="handleResetPwd(row)" :disabled="row.role === 'admin'">重置密码</el-button>
            <el-popconfirm title="确定删除该用户吗？" @confirm="handleDelete(row.id)">
              <template #reference>
                <el-button type="danger" link size="small" :disabled="row.role === 'admin'">删除</el-button>
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
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" :disabled="!!form.id" />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="form.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="form.role" placeholder="请选择角色" style="width: 100%">
            <el-option label="管理员" value="admin" />
            <el-option label="物业员工" value="staff" />
            <el-option label="业主" value="resident" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="!form.id" label="默认密码">
          <el-input value="123456" disabled />
          <div class="form-tip">新用户默认密码为 123456</div>
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
import { getUserList, addUser, editUser, updateUserStatus, deleteUser, resetPassword } from '@/api/user'

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)

const searchForm = reactive({
  role: '',
  keyword: ''
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const form = reactive({
  id: null,
  username: '',
  realName: '',
  phone: '',
  email: '',
  role: 'resident'
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }]
}

const getRoleName = (role) => {
  const roles = { admin: '管理员', staff: '物业员工', resident: '业主' }
  return roles[role] || '未知'
}

const getRoleType = (role) => {
  const types = { admin: 'danger', staff: 'success', resident: 'warning' }
  return types[role] || ''
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getUserList({
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
  searchForm.role = ''
  searchForm.keyword = ''
  handleSearch()
}

const resetForm = () => {
  form.id = null
  form.username = ''
  form.realName = ''
  form.phone = ''
  form.email = ''
  form.role = 'resident'
}

const handleAdd = () => {
  resetForm()
  dialogTitle.value = '新增用户'
  dialogVisible.value = true
}

const handleEdit = (row) => {
  resetForm()
  Object.assign(form, {
    id: row.id,
    username: row.username,
    realName: row.realName,
    phone: row.phone,
    email: row.email,
    role: row.role
  })
  dialogTitle.value = '编辑用户'
  dialogVisible.value = true
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  
  submitLoading.value = true
  try {
    if (form.id) {
      await editUser(form)
      ElMessage.success('修改成功')
    } else {
      await addUser(form)
      ElMessage.success('新增成功，默认密码为 123456')
    }
    dialogVisible.value = false
    loadData()
  } finally {
    submitLoading.value = false
  }
}

const handleStatusChange = async (row) => {
  try {
    await updateUserStatus(row.id, row.status)
    ElMessage.success(row.status === 1 ? '已启用' : '已禁用')
  } catch {
    row.status = row.status === 1 ? 0 : 1
  }
}

const handleResetPwd = async (row) => {
  try {
    await ElMessageBox.confirm(`确定将用户 "${row.realName}" 的密码重置为 123456 吗？`, '重置密码', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await resetPassword(row.id)
    ElMessage.success('密码已重置为 123456')
  } catch {
    // 取消操作
  }
}

const handleDelete = async (id) => {
  try {
    await deleteUser(id)
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
.user-manage {
  .pagination {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }
  
  .form-tip {
    font-size: 12px;
    color: var(--text-secondary);
    margin-top: 4px;
  }
}
</style>
