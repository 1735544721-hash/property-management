import request from '@/utils/request'

// 获取统计数据
export function getDashboardStats() {
  return request.get('/dashboard/stats')
}

