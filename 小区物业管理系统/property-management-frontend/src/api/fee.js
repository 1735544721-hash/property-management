import request from '@/utils/request'

// 分页查询费用（管理员）
export function getFeeList(params) {
  return request.get('/fee/list', { params })
}

// 查询我的费用
export function getMyFees(params) {
  return request.get('/fee/my', { params })
}

// 添加费用
export function addFee(data) {
  return request.post('/fee/add', data)
}

// 缴费
export function payFee(id) {
  return request.put(`/fee/pay/${id}`)
}

// 批量缴费
export function batchPayFee(ids) {
  return request.put('/fee/pay/batch', ids)
}

// 删除费用
export function deleteFee(id) {
  return request.delete(`/fee/${id}`)
}

// 费用统计
export function getFeeStatistics() {
  return request.get('/fee/statistics')
}

// 我的未缴费总额
export function getMyUnpaid() {
  return request.get('/fee/my/unpaid')
}

