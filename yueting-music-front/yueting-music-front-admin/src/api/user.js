import request from './request'

export function getUserList(params) {
  return request.post('/system/user/list', params)
}

export function getUserById(id) {
  return request.get('/system/user/' + id)
}

export function updateUser(data) {
  return request.put('/system/user/update', data)
}

export function updateUserPassword(data) {
  return request.put('/system/user/password', data)
}

export function deleteUser(id) {
  return request.delete('/system/user/' + id)
}
