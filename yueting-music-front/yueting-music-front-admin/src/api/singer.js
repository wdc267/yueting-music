import request from './request'

export function getSingerList(query) {
  return request.post('/content/singer/list', query)
}

export function getSingerById(id) {
  return request.get('/content/singer/' + id)
}

export function saveSinger(data) {
  return request.post('/content/singer/save', data)
}

export function updateSinger(data) {
  return request.put('/content/singer/update', data)
}

export function deleteSinger(id) {
  return request.delete('/content/singer/' + id)
}

export function updateSingerSort(list) {
  return request.put('/content/singer/sort', list)
}