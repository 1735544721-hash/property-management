import request from '@/utils/request'

// 分页查询房屋
export function getHouseList(params) {
  return request.get('/house/list', { params })
}

// 获取我的房产
export function getMyHouses() {
  return request.get('/house/my')
}

// 获取房屋详情
export function getHouseById(id) {
  return request.get(`/house/${id}`)
}

// 添加房屋
export function addHouse(data) {
  return request.post('/house/add', data)
}

// 更新房屋
export function updateHouse(data) {
  return request.put('/house/update', data)
}

// 删除房屋
export function deleteHouse(id) {
  return request.delete(`/house/${id}`)
}

// 绑定业主
export function bindOwner(houseId, ownerId) {
  return request.put(`/house/bind/${houseId}`, null, { params: { ownerId } })
}

// 解绑业主
export function unbindOwner(houseId) {
  return request.put(`/house/unbind/${houseId}`)
}

