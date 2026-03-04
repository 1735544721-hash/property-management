<template>
  <div class="my-house">
    <h2 class="page-title">我的房产</h2>
    
    <div class="house-list" v-loading="loading">
      <template v-if="houses.length">
        <el-card v-for="house in houses" :key="house.id" class="house-card">
          <div class="house-header">
            <el-icon class="house-icon"><House /></el-icon>
            <div class="house-info">
              <h3>{{ house.buildingName }} {{ house.unitNumber }}单元 {{ house.roomNumber }}</h3>
              <p>{{ house.floorNumber }}层</p>
            </div>
            <el-tag type="success">已入住</el-tag>
          </div>
          <el-divider />
          <div class="house-details">
            <div class="detail-item">
              <span class="label">房屋面积</span>
              <span class="value">{{ house.area }} ㎡</span>
            </div>
            <div class="detail-item">
              <span class="label">楼栋信息</span>
              <span class="value">{{ house.buildingName }}</span>
            </div>
            <div class="detail-item">
              <span class="label">单元/楼层</span>
              <span class="value">{{ house.unitNumber }}单元 / {{ house.floorNumber }}层</span>
            </div>
            <div class="detail-item">
              <span class="label">房间号</span>
              <span class="value">{{ house.roomNumber }}</span>
            </div>
          </div>
        </el-card>
      </template>
      
      <el-empty v-else description="暂无绑定的房产信息">
        <el-button type="primary" @click="$router.push('/my-complaint')">联系物业绑定</el-button>
      </el-empty>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getMyHouses } from '@/api/house'

const loading = ref(false)
const houses = ref([])

const loadData = async () => {
  loading.value = true
  try {
    const res = await getMyHouses()
    houses.value = res.data || []
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>

<style lang="scss" scoped>
.my-house {
  .house-list {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
    gap: 20px;
  }
  
  .house-card {
    .house-header {
      display: flex;
      align-items: center;
      
      .house-icon {
        font-size: 40px;
        color: var(--primary-color);
        margin-right: 16px;
      }
      
      .house-info {
        flex: 1;
        
        h3 {
          font-size: 18px;
          color: var(--text-primary);
          margin-bottom: 4px;
        }
        
        p {
          font-size: 14px;
          color: var(--text-secondary);
        }
      }
    }
    
    .house-details {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 16px;
      
      .detail-item {
        display: flex;
        flex-direction: column;
        
        .label {
          font-size: 12px;
          color: var(--text-secondary);
          margin-bottom: 4px;
        }
        
        .value {
          font-size: 14px;
          color: var(--text-primary);
          font-weight: 500;
        }
      }
    }
  }
}
</style>

