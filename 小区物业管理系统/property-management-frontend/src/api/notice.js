import request from '@/utils/request'

// 分页查询公告
export function getNoticeList(params) {
  return request.get('/notice/list', { params })
}

// 获取最新公告
export function getLatestNotices(limit = 5) {
  return request.get('/notice/latest', { params: { limit } })
}

// 获取公告详情
export function getNoticeById(id) {
  return request.get(`/notice/${id}`)
}

// 添加公告
export function addNotice(data) {
  return request.post('/notice/add', data)
}

// 更新公告
export function updateNotice(data) {
  return request.put('/notice/update', data)
}

// 发布公告
export function publishNotice(id) {
  return request.put(`/notice/publish/${id}`)
}

// 删除公告
export function deleteNotice(id) {
  return request.delete(`/notice/${id}`)
}

