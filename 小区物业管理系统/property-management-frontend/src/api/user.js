import request from '@/utils/request'

// 获取当前用户信息
export function getUserInfo() {
  return request.get('/user/info')
}

// 修改密码
export function updatePassword(data) {
  return request.put('/user/password', data)
}

// 更新用户信息
export function updateUserInfo(data) {
  return request.put('/user/update', data)
}

// 分页查询用户列表
export function getUserList(params) {
  return request.get('/user/list', { params })
}

// 获取用户详情
export function getUserById(id) {
  return request.get(`/user/${id}`)
}

// 新增用户
export function addUser(data) {
  return request.post('/user/add', data)
}

// 编辑用户
export function editUser(data) {
  return request.put('/user/edit', data)
}

// 更新用户状态
export function updateUserStatus(id, status) {
  return request.put(`/user/status/${id}`, null, { params: { status } })
}

// 删除用户
export function deleteUser(id) {
  return request.delete(`/user/${id}`)
}

// 重置密码
export function resetPassword(id) {
  return request.put(`/user/reset-password/${id}`)
}

