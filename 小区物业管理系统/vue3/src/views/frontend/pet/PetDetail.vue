<template>
  <div class="pet-detail-page" v-loading="loading">
    <div class="page-decoration">
      <div class="decoration-bubble bubble-1"></div>
      <div class="decoration-bubble bubble-2"></div>
      <div class="decoration-bubble bubble-3"></div>
      <div class="decoration-paw paw-1">🐾</div>
      <div class="decoration-paw paw-2">🐾</div>
    </div>
    
    <div class="breadcrumb-container">
      <div class="back-button" @click="goBack">
        <i class="el-icon-arrow-left"></i>
        返回列表
      </div>
    </div>
    
    <div class="pet-detail-content" v-if="pet">
      <div class="pet-main-info">
        <div class="pet-gallery">
          <el-carousel :interval="5000" height="450px" arrow="always" 
            indicator-position="outside" class="pet-carousel">
            <el-carousel-item v-for="(image, index) in getImageList(pet.images)" :key="index">
              <img :src="image" alt="宠物图片" class="carousel-image">
            </el-carousel-item>
          </el-carousel>
        </div>
        
        <div class="pet-info-card">
          <div class="pet-header">
            <h1 class="pet-name">{{ pet.name }}</h1>
            <div class="pet-status-badge" :class="getStatusClass(pet.adoptionStatus)">
              {{ pet.adoptionStatus }}
            </div>
          </div>
          
          <div class="pet-tags">
            <span class="pet-tag tag-category" v-if="pet.categoryName">{{ pet.categoryName }}</span>
          </div>
          
          <div class="pet-attributes">
            <div class="attribute-item">
              <div class="attribute-icon">🐾</div>
              <div class="attribute-content">
                <div class="attribute-label">年龄</div>
                <div class="attribute-value">{{ pet.age }} 岁</div>
              </div>
            </div>
            
            <div class="attribute-item">
              <div class="attribute-icon">{{ pet.gender === '公' ? '♂️' : '♀️' }}</div>
              <div class="attribute-content">
                <div class="attribute-label">性别</div>
                <div class="attribute-value">{{ pet.gender }}</div>
              </div>
            </div>
            
            <div class="attribute-item">
              <div class="attribute-icon">❤️</div>
              <div class="attribute-content">
                <div class="attribute-label">健康状况</div>
                <div class="attribute-value">{{ pet.healthStatus }}</div>
              </div>
            </div>
          </div>
          
          <div class="pet-description-section">
            <h2 class="section-title">宠物介绍</h2>
            <p class="description-text">{{ pet.description }}</p>
          </div>
          
          <div class="pet-actions">
            <el-button v-if="pet.adoptionStatus === '可领养'" 
                      class="adopt-button" 
                      @click="handleAdopt">
              <span class="button-icon">🏠</span>
              申请领养
            </el-button>
            
            <el-button v-else-if="pet.userApplicationStatus === '已申请' || pet.userApplicationStatus === '审核中'" 
                      class="cancel-button" 
                      @click="handleCancel">
              取消申请
            </el-button>
            
            <el-button v-else-if="pet.userApplicationStatus === '已通过'" 
                      class="success-button" 
                      disabled>
              已通过申请
            </el-button>
            
            <el-button v-else-if="pet.userApplicationStatus === '已拒绝'" 
                      class="rejected-button" 
                      disabled>
              申请被拒绝
            </el-button>
            
            <el-button v-else-if="pet.adoptionStatus === '已领养'" 
                      class="adopted-button" 
                      disabled>
              已被领养
            </el-button>
          </div>
        </div>
      </div>
      
      <div class="pet-details-tabs">
        <el-tabs type="border-card" v-model="activeTab" class="custom-tabs">
          <!-- 健康记录标签页 -->
          <el-tab-pane label="健康记录" name="healthRecord">
            <div v-loading="healthRecordLoading" class="tab-content">
              <div v-if="healthRecordList.length === 0" class="empty-records">
                <el-empty description="暂无健康记录" />
              </div>
              <div v-else class="health-timeline">
                <el-timeline>
                  <el-timeline-item
                    v-for="record in healthRecordList"
                    :key="record.id"
                    :timestamp="formatDate(record.recordDate)"
                    :type="getHealthRecordType(record.recordType)"
                    class="timeline-item"
                  >
                    <el-card class="record-card">
                      <template #header>
                        <div class="record-header">
                          <span class="record-title">{{ record.recordType }}</span>
                          <span class="record-hospital">{{ record.hospital }}</span>
                        </div>
                      </template>
                      <div class="record-content">
                        <div class="record-item" v-if="record.doctor">
                          <strong>医生：</strong> {{ record.doctor }}
                        </div>
                        <div class="record-item-group">
                          <div class="record-item" v-if="record.weight">
                            <strong>体重：</strong> {{ record.weight }}kg
                          </div>
                          <div class="record-item" v-if="record.temperature">
                            <strong>体温：</strong> {{ record.temperature }}℃
                          </div>
                          <div class="record-item" v-if="record.heartRate">
                            <strong>心率：</strong> {{ record.heartRate }}次/分
                          </div>
                        </div>
                        <div class="record-item" v-if="record.diagnosisResults">
                          <strong>诊断结果：</strong> {{ record.diagnosisResults }}
                        </div>
                        <div class="record-item" v-if="record.healthSuggestions">
                          <strong>健康建议：</strong> {{ record.healthSuggestions }}
                        </div>
                        <div class="record-item" v-if="record.notes">
                          <strong>备注：</strong> {{ record.notes }}
                        </div>
                      </div>
                    </el-card>
                  </el-timeline-item>
                </el-timeline>
                
                <div class="pagination-container" v-if="healthRecordTotal > healthRecordPageSize">
                  <el-pagination
                    background
                    layout="prev, pager, next"
                    :total="healthRecordTotal"
                    :page-size="healthRecordPageSize"
                    :current-page="healthRecordCurrentPage"
                    @current-change="handleHealthRecordPageChange"
                  />
                </div>
              </div>
            </div>
          </el-tab-pane>
          
          <!-- 疫苗接种标签页 -->
          <el-tab-pane label="疫苗记录" name="vaccination">
            <div v-loading="vaccinationLoading" class="tab-content">
              <div v-if="vaccinationList.length === 0" class="empty-records">
                <el-empty description="暂无疫苗接种记录" />
              </div>
              <div v-else class="vaccination-table">
                <el-table :data="vaccinationList" border style="width: 100%" class="custom-table">
                  <el-table-column prop="vaccinationDate" label="接种日期" width="180" :formatter="formatDateColumn" />
                  <el-table-column prop="vaccineName" label="疫苗名称" width="180" />
                  <el-table-column prop="hospital" label="接种医院" width="180" />
                  <el-table-column prop="nextDate" label="下次接种日期" width="180" :formatter="formatDateColumn" />
                  <el-table-column prop="notes" label="备注" min-width="180" />
                </el-table>
                
                <div class="pagination-container" v-if="vaccinationTotal > vaccinationPageSize">
                  <el-pagination
                    background
                    layout="prev, pager, next"
                    :total="vaccinationTotal"
                    :page-size="vaccinationPageSize"
                    :current-page="vaccinationCurrentPage"
                    @current-change="handleVaccinationPageChange"
                  />
                </div>
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>
    
    <el-empty v-else-if="!loading" description="宠物不存在或已下架" class="pet-empty"></el-empty>
    
    <!-- 申请领养对话框 -->
    <el-dialog v-model="dialogVisible" title="申请领养" width="500px" class="custom-dialog">
      <div class="dialog-header">
        <div class="dialog-icon">🏠</div>
        <h3 class="dialog-title">领养申请</h3>
      </div>
      
      <el-form :model="adoptForm" label-width="80px" :rules="adoptRules" ref="adoptFormRef">
        <el-form-item label="申请理由" prop="applyReason">
          <el-input v-model="adoptForm.applyReason" type="textarea" rows="4" placeholder="请详细说明您申请领养的原因、饲养环境等信息"></el-input>
        </el-form-item>
        <el-form-item label="联系电话" prop="contactPhone">
          <el-input v-model="adoptForm.contactPhone" placeholder="请输入您的联系电话"></el-input>
        </el-form-item>
        <el-form-item label="联系地址" prop="address">
          <el-input v-model="adoptForm.address" type="textarea" rows="3" placeholder="请输入详细地址"></el-input>
        </el-form-item>
      </el-form>
      
      <div class="dialog-tip">
        <div class="tip-icon">💡</div>
        <div class="tip-text">提交申请后，我们将在1-3个工作日内进行审核，请保持电话畅通。</div>
      </div>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button class="cancel-btn" @click="dialogVisible = false">取消</el-button>
          <el-button class="submit-btn" @click="submitAdoption" :loading="submitLoading">提交申请</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'
import { formatDate, formatDateTime } from '@/utils/dateUtils'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const baseAPI = process.env.VUE_APP_BASE_API || '/api'

const petId = computed(() => route.params.id)
const pet = ref(null)
const loading = ref(false)
const dialogVisible = ref(false)
const submitLoading = ref(false)
const adoptFormRef = ref(null)
const activeTab = ref('healthRecord')

// 健康记录相关
const healthRecordLoading = ref(false)
const healthRecordList = ref([])
const healthRecordTotal = ref(0)
const healthRecordCurrentPage = ref(1)
const healthRecordPageSize = ref(5)

// 疫苗记录相关
const vaccinationLoading = ref(false)
const vaccinationList = ref([])
const vaccinationTotal = ref(0)
const vaccinationCurrentPage = ref(1)
const vaccinationPageSize = ref(5)

// 申请表单
const adoptForm = ref({
  petId: null,
  applyReason: '',
  contactPhone: '',
  address: ''
})

// 表单验证规则
const adoptRules = {
  applyReason: [
    { required: true, message: '请填写申请理由', trigger: 'blur' },
    { min: 10, message: '申请理由不能少于10个字符', trigger: 'blur' }
  ],
  contactPhone: [
    { required: true, message: '请填写联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  address: [
    { required: true, message: '请填写联系地址', trigger: 'blur' },
    { min: 5, message: '地址不能少于5个字符', trigger: 'blur' }
  ]
}

// 获取宠物详情
const fetchPetDetail = async () => {
  loading.value = true
  try {
    // 如果用户已登录，获取带用户申请状态的详情
    if (userStore.isLoggedIn) {
      await request.get(`/pet/${petId.value}/with-user-status`, {}, {
        onSuccess: (data) => {
          pet.value = data
          adoptForm.value.petId = data.id
          
          // 获取健康记录和疫苗记录
          fetchHealthRecords()
          fetchVaccinations()
        }
      })
    } else {
      // 未登录用户获取普通详情
      await request.get(`/pet/${petId.value}`, {}, {
        onSuccess: (data) => {
          pet.value = data
          adoptForm.value.petId = data.id
          
          // 获取健康记录和疫苗记录
          fetchHealthRecords()
          fetchVaccinations()
        }
      })
    }
  } catch (error) {
    console.error('获取宠物详情失败:', error)
    ElMessage.error('获取宠物详情失败')
  } finally {
    loading.value = false
  }
}

// 获取健康记录列表
const fetchHealthRecords = async () => {
  if (!pet.value) return
  
  healthRecordLoading.value = true
  try {
    await request.get('/pet-health-record/page', {
      petId: pet.value.id,
      currentPage: healthRecordCurrentPage.value,
      size: healthRecordPageSize.value
    }, {
      onSuccess: (res) => {
        healthRecordList.value = res.records
        healthRecordTotal.value = res.total
      }
    })
  } catch (error) {
    console.error('获取健康记录失败:', error)
  } finally {
    healthRecordLoading.value = false
  }
}

// 获取疫苗记录列表
const fetchVaccinations = async () => {
  if (!pet.value) return
  
  vaccinationLoading.value = true
  try {
    await request.get('/pet-vaccination/page', {
      petId: pet.value.id,
      currentPage: vaccinationCurrentPage.value,
      size: vaccinationPageSize.value
    }, {
      onSuccess: (res) => {
        vaccinationList.value = res.records
        vaccinationTotal.value = res.total
      }
    })
  } catch (error) {
    console.error('获取疫苗记录失败:', error)
  } finally {
    vaccinationLoading.value = false
  }
}

// 处理健康记录分页
const handleHealthRecordPageChange = (page) => {
  healthRecordCurrentPage.value = page
  fetchHealthRecords()
}

// 处理疫苗记录分页
const handleVaccinationPageChange = (page) => {
  vaccinationCurrentPage.value = page
  fetchVaccinations()
}

// 获取健康记录类型对应的样式
const getHealthRecordType = (recordType) => {
  switch (recordType) {
    case '体检':
      return 'success'
    case '就诊':
      return 'warning'
    case '手术':
      return 'danger'
    default:
      return 'info'
  }
}

// 格式化日期列
const formatDateColumn = (row, column, cellValue) => {
  return cellValue ? formatDateTime(cellValue) : '-'
}

// 监听标签页变化
watch(activeTab, (newVal) => {
  if (newVal === 'vaccination' && vaccinationList.value.length === 0) {
    fetchVaccinations()
  } else if (newVal === 'healthRecord' && healthRecordList.value.length === 0) {
    fetchHealthRecords()
  }
})

// 处理申请领养
const handleAdopt = () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录后再申请领养')
    router.push({ path: '/login', query: { redirect: route.fullPath } })
    return
  }
  
  dialogVisible.value = true
}

// 提交领养申请
const submitAdoption = () => {
  adoptFormRef.value.validate(async (valid) => {
    if (!valid) return
    
    submitLoading.value = true
    try {
      await request.post('/adoption', adoptForm.value, {
        successMsg: '申请提交成功',
        onSuccess: () => {
          dialogVisible.value = false
          fetchPetDetail() // 刷新详情
        }
      })
    } catch (error) {
      console.error('提交申请失败:', error)
    } finally {
      submitLoading.value = false
    }
  })
}

// 取消申请
const handleCancel = () => {
  ElMessageBox.confirm('确定要取消该领养申请吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await request.delete(`/adoption/${pet.value.adoptionId}`, {
        successMsg: '申请已取消',
        onSuccess: () => {
          fetchPetDetail() // 刷新详情
        }
      })
    } catch (error) {
      console.error('取消申请失败:', error)
    }
  }).catch(() => {})
}

// 获取图片列表
const getImageList = (images) => {
  // 使用数据URI作为默认图片，而不是引用外部图片文件
  const defaultImage = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMzAwIiBoZWlnaHQ9IjMwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB4PSIyIiB5PSIyIiB3aWR0aD0iMjk2IiBoZWlnaHQ9IjI5NiIgc3R5bGU9ImZpbGw6I2VlZTtzdHJva2U6I2FhYTtzdHJva2Utd2lkdGg6MnB4OyIvPjx0ZXh0IHg9IjE1MCIgeT0iMTUwIiBkb21pbmFudC1iYXNlbGluZT0ibWlkZGxlIiB0ZXh0LWFuY2hvcj0ibWlkZGxlIiBzdHlsZT0iZmlsbDojOTk5O2ZvbnQtZmFtaWx5OkFyaWFsLHNhbnMtc2VyaWY7Zm9udC1zaXplOjI0cHg7Ij7mmoLml6Dlm77niYc8L3RleHQ+PC9zdmc+'
  
  if (!images) return [defaultImage]
  
  return images.split(',').map(img => {
    const trimmedImg = img.trim()
    if (trimmedImg.startsWith('http')) {
      return trimmedImg
    }
    return `${baseAPI}${trimmedImg}`
  })
}

// 获取状态样式类
const getStatusClass = (status) => {
  if (status === '可领养') return 'status-available'
  if (status === '已领养') return 'status-adopted'
  return ''
}

// 返回上一页
const goBack = () => {
  router.back()
}

onMounted(() => {
  fetchPetDetail()
})
</script>

<style lang="scss" scoped>
.pet-detail-page {
  min-height: 100vh;
  background-color: #FFF9E6;
  padding: 30px 20px 60px;
  position: relative;
  overflow: hidden;
}

.page-decoration {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 0;
  
  .decoration-bubble {
    position: absolute;
    border-radius: 50%;
    background: linear-gradient(135deg, #FFB6C1 0%, #FFEE93 100%);
    opacity: 0.05;
    
    &.bubble-1 {
      width: 300px;
      height: 300px;
      top: -150px;
      left: -100px;
    }
    
    &.bubble-2 {
      width: 200px;
      height: 200px;
      bottom: 10%;
      right: -50px;
    }
    
    &.bubble-3 {
      width: 150px;
      height: 150px;
      top: 40%;
      right: 10%;
    }
  }
  
  .decoration-paw {
    position: absolute;
    font-size: 30px;
    opacity: 0.1;
    
    &.paw-1 {
      top: 20%;
      left: 5%;
      animation: float 15s infinite ease-in-out;
    }
    
    &.paw-2 {
      bottom: 10%;
      right: 10%;
      animation: float 18s infinite ease-in-out reverse;
    }
  }
}

.breadcrumb-container {
  max-width: 1200px;
  margin: 0 auto 20px;
  position: relative;
  z-index: 2;
  
  .back-button {
    display: inline-flex;
    align-items: center;
    padding: 8px 16px;
    background-color: white;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
    cursor: pointer;
    font-weight: 500;
    color: #683e35;
    transition: all 0.3s ease;
    
    i {
      margin-right: 8px;
    }
    
    &:hover {
      transform: translateX(-3px);
      background-color: #FFF0F0;
    }
  }
}

.pet-detail-content {
  max-width: 1200px;
  margin: 0 auto;
  position: relative;
  z-index: 1;
}

.pet-main-info {
  display: flex;
  gap: 30px;
  margin-bottom: 40px;
  
  @media (max-width: 992px) {
    flex-direction: column;
  }
}

.pet-gallery {
  flex: 1;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05);
  
  .pet-carousel {
    border-radius: 12px;
    overflow: hidden;
    
    :deep(.el-carousel__arrow) {
      background-color: rgba(255, 182, 193, 0.8);
      border-radius: 50%;
      
      &:hover {
        background-color: #FFB6C1;
      }
    }
    
    :deep(.el-carousel__indicators) {
      .el-carousel__button {
        background-color: #FFEE93;
      }
    }
  }
  
  .carousel-image {
    width: 100%;
    height: 100%;
    object-fit: cover;
    border-radius: 12px;
  }
}

.pet-info-card {
  flex: 1;
  background: white;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05);
  display: flex;
  flex-direction: column;
  
  .pet-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    
    .pet-name {
      font-family: 'Nunito Sans', sans-serif;
      font-size: 28px;
      color: #683e35;
      margin: 0;
    }
    
    .pet-status-badge {
      padding: 6px 12px;
      border-radius: 20px;
      font-size: 14px;
      font-weight: 600;
      color: white;
    }
  }
  
  .status-available {
    background: #67c23a;
    box-shadow: 0 3px 6px rgba(103, 194, 58, 0.2);
  }

  .status-adopted {
    background: #909399;
    box-shadow: 0 3px 6px rgba(144, 147, 153, 0.2);
  }
  
  .pet-tags {
    display: flex;
    gap: 8px;
    margin-bottom: 20px;
    flex-wrap: wrap;
    
    .pet-tag {
      padding: 4px 8px;
      border-radius: 4px;
      font-size: 12px;
      
      &.tag-category {
        background: rgba(255, 167, 38, 0.15);
        color: #ff8f00;
      }
      
      &.tag-type {
        background: rgba(255, 182, 193, 0.2);
        color: #d85a77;
      }
    }
  }
  
  .pet-attributes {
    display: flex;
    flex-wrap: wrap;
    gap: 15px;
    margin-bottom: 30px;
    
    .attribute-item {
      flex: 1;
      min-width: 120px;
      background: #f9f9f9;
      border-radius: 8px;
      padding: 15px;
      display: flex;
      align-items: center;
      
      .attribute-icon {
        width: 40px;
        height: 40px;
        border-radius: 50%;
        background: rgba(255, 238, 147, 0.3);
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 20px;
        margin-right: 12px;
      }
      
      .attribute-content {
        flex: 1;
        
        .attribute-label {
          font-size: 12px;
          color: #999;
          margin-bottom: 5px;
        }
        
        .attribute-value {
          font-size: 16px;
          font-weight: 600;
          color: #683e35;
        }
      }
    }
  }
  
  .pet-description-section {
    margin-bottom: 30px;
    flex-grow: 1;
    
    .section-title {
      font-family: 'Nunito Sans', sans-serif;
      font-size: 20px;
      color: #683e35;
      margin: 0 0 15px;
      position: relative;
      padding-left: 15px;
      
      &::before {
        content: '';
        position: absolute;
        left: 0;
        top: 0;
        bottom: 0;
        width: 4px;
        background: #FFB6C1;
        border-radius: 2px;
      }
    }
    
    .description-text {
      font-size: 15px;
      line-height: 1.8;
      color: #666;
      white-space: pre-line;
    }
  }
  
  .pet-actions {
    margin-top: auto;
    display: flex;
    justify-content: center;
    
    .adopt-button, .cancel-button, .success-button, .rejected-button, .adopted-button {
      min-width: 180px;
      height: 46px;
      font-size: 16px;
      border-radius: 8px;
      transition: all 0.3s cubic-bezier(0.25, 0.46, 0.45, 0.94);
      
      &:hover {
        transform: translateY(-2px);
      }
      
      .button-icon {
        margin-right: 8px;
      }
    }
    
    .adopt-button {
      background-color: #FFA726;
      color: white;
      border: none;
      
      &:hover {
        background-color: #ff9800;
        box-shadow: 0 4px 12px rgba(255, 167, 38, 0.3);
      }
    }
    
    .cancel-button {
      background-color: #f56c6c;
      color: white;
      border: none;
      
      &:hover {
        background-color: #f34e4e;
        box-shadow: 0 4px 12px rgba(245, 108, 108, 0.3);
      }
    }
    
    .success-button {
      background-color: #67c23a;
      color: white;
      border: none;
    }
    
    .rejected-button, .adopted-button {
      background-color: #909399;
      color: white;
      border: none;
    }
  }
}

.pet-details-tabs {
  margin-bottom: 40px;
  
  .custom-tabs {
    :deep(.el-tabs__nav) {
      background-color: #f9f9f9;
    }
    
    :deep(.el-tabs__item) {
      color: #666;
      
      &.is-active {
        color: #FFB6C1;
      }
      
      &:hover {
        color: #FFB6C1;
      }
    }
    
    :deep(.el-tabs__active-bar) {
      background-color: #FFB6C1;
    }
  }
  
  .tab-content {
    padding: 30px 20px;
    min-height: 200px;
  }
  
  .health-timeline {
    .timeline-item {
      margin-bottom: 25px;
      
      :deep(.el-timeline-item__node--success) {
        background-color: #67c23a;
      }
      
      :deep(.el-timeline-item__node--warning) {
        background-color: #e6a23c;
      }
      
      :deep(.el-timeline-item__node--danger) {
        background-color: #f56c6c;
      }
      
      :deep(.el-timeline-item__timestamp) {
        color: #999;
      }
    }
    
    .record-card {
      border-radius: 8px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
      transition: transform 0.3s ease;
      
      &:hover {
        transform: translateY(-3px);
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
      }
      
      :deep(.el-card__header) {
        padding: 15px 20px;
        border-bottom: 1px solid #f0f0f0;
      }
      
      .record-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        
        .record-title {
          font-weight: bold;
          color: #683e35;
        }
        
        .record-hospital {
          font-size: 13px;
          color: #999;
        }
      }
      
      .record-content {
        padding: 15px 0;
        
        .record-item {
          margin-bottom: 10px;
          font-size: 14px;
          color: #666;
          
          strong {
            color: #333;
          }
          
          &:last-child {
            margin-bottom: 0;
          }
        }
        
        .record-item-group {
          display: flex;
          flex-wrap: wrap;
          gap: 15px;
          margin-bottom: 10px;
        }
      }
    }
  }
  
  .custom-table {
    border-radius: 8px;
    overflow: hidden;
    
    :deep(.el-table__header-wrapper) {
      th {
        background-color: #f9f9f9;
        font-weight: 600;
        color: #683e35;
      }
    }
    
    :deep(.el-table__row) {
      &:hover {
        background-color: #fff9f9;
      }
    }
  }
}

.empty-records {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 200px;
  
  :deep(.el-empty__description) {
    color: #999;
  }
}

.pagination-container {
  margin-top: 30px;
  display: flex;
  justify-content: center;
  
  :deep(.el-pagination) {
    --el-pagination-button-bg-color: white;
    --el-pagination-hover-color: #FFB6C1;
    
    .el-pagination__jump {
      color: #683e35;
    }
    
    button:not(:disabled):hover {
      color: #FFB6C1;
    }
    
    .is-active {
      background-color: #FFB6C1 !important;
      color: white !important;
    }
  }
}

.custom-dialog {
  :deep(.el-dialog__header) {
    padding-bottom: 0;
  }
  
  .dialog-header {
    display: flex;
    align-items: center;
    margin-bottom: 25px;
    
    .dialog-icon {
      width: 50px;
      height: 50px;
      border-radius: 50%;
      background: rgba(255, 238, 147, 0.3);
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 24px;
      margin-right: 15px;
    }
    
    .dialog-title {
      font-family: 'Nunito Sans', sans-serif;
      font-size: 22px;
      color: #683e35;
      margin: 0;
    }
  }
  
  .dialog-tip {
    margin: 20px 0;
    background: rgba(255, 238, 147, 0.4);
    border-left: 3px solid #FFEE93;
    padding: 12px;
    border-radius: 4px;
    display: flex;
    align-items: center;
    
    .tip-icon {
      font-size: 20px;
      margin-right: 10px;
    }
    
    .tip-text {
      font-size: 14px;
      color: #683e35;
      line-height: 1.5;
    }
  }
  
  :deep(.el-form-item__label) {
    color: #683e35;
  }
  
  :deep(.el-input__wrapper) {
    &:hover {
      box-shadow: 0 0 0 1px #FFB6C1;
    }
    
    &.is-focus {
      box-shadow: 0 0 0 1px #FFB6C1;
    }
  }
  
  .dialog-footer {
    display: flex;
    justify-content: center;
    gap: 15px;
    width: 100%;
    
    .cancel-btn, .submit-btn {
      min-width: 120px;
      height: 38px;
      border-radius: 8px;
    }
    
    .cancel-btn {
      background: transparent;
      border: 1px solid #d9d9d9;
      color: #606266;
      
      &:hover {
        border-color: #FFB6C1;
        color: #FFB6C1;
      }
    }
    
    .submit-btn {
      background: #FFA726;
      color: white;
      border: none;
      
      &:hover {
        background-color: #ff9800;
        box-shadow: 0 4px 12px rgba(255, 167, 38, 0.3);
      }
    }
  }
}

.pet-empty {
  margin-top: 100px;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-20px);
  }
}

@media (max-width: 768px) {
  .pet-attributes {
    flex-direction: column;
    
    .attribute-item {
      width: 100%;
    }
  }
  
  .pet-description-section {
    .description-text {
      font-size: 14px;
    }
  }
  
  .tab-content {
    padding: 20px 10px;
  }
}
</style> 