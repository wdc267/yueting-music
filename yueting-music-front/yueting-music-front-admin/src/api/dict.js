import request from './request'

export function getDictTypes() {
  return request.get('/system/dict/types')
}

export function getDictByCode(dictCode) {
  return request.get('/system/dict/listByCode', { params: { dictCode } })
}

export function getDictById(id) {
  return request.get('/system/dict/' + id)
}

export function saveDict(data) {
  return request.post('/system/dict/save', data)
}

export function updateDict(data) {
  return request.put('/system/dict/update', data)
}

export function deleteDict(id) {
  return request.delete('/system/dict/' + id)
}