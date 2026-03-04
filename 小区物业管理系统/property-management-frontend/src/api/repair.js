import request from '@/utils/request'

// 分页查询报修（管理员）
export function getRepairList(params) {
  return request.get('/repair/list', { params })
}

// 查询我的报修
export function getMyRepairs(params) {
  return request.get('/repair/my', { params })
}

// 获取报修详情
export function getRepairById(id) {
  return request.get(`/repair/${id}`)
}

// 提交报修
export function addRepair(data) {
  return request.post('/repair/add', data)
}

// 处理报修
export function handleRepair(id, handleResult, status) {
  return request.put(`/repair/handle/${id}`, null, { params: { handleResult, status } })
}

// 统计待处理数量
export function countPendingRepairs() {
  return request.get('/repair/pending/count')
}

