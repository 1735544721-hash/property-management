import request from '@/utils/request'

// 分页查询投诉建议（管理员）
export function getComplaintList(params) {
  return request.get('/complaint/list', { params })
}

// 查询我的投诉建议
export function getMyComplaints(params) {
  return request.get('/complaint/my', { params })
}

// 获取投诉建议详情
export function getComplaintById(id) {
  return request.get(`/complaint/${id}`)
}

// 提交投诉建议
export function addComplaint(data) {
  return request.post('/complaint/add', data)
}

// 处理投诉建议
export function handleComplaint(id, reply) {
  return request.put(`/complaint/handle/${id}`, null, { params: { reply } })
}

// 统计待处理数量
export function countPendingComplaints() {
  return request.get('/complaint/pending/count')
}

