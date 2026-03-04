import request from '@/utils/request'

// 分页查询楼栋
export function getBuildingList(params) {
  return request.get('/building/list', { params })
}

// 获取所有楼栋
export function getAllBuildings() {
  return request.get('/building/all')
}

// 获取楼栋详情
export function getBuildingById(id) {
  return request.get(`/building/${id}`)
}

// 添加楼栋
export function addBuilding(data) {
  return request.post('/building/add', data)
}

// 更新楼栋
export function updateBuilding(data) {
  return request.put('/building/update', data)
}

// 删除楼栋
export function deleteBuilding(id) {
  return request.delete(`/building/${id}`)
}

