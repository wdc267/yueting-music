import request from './request'

export function getRecommendList(query) {
  return request.post('/operation/recommend/list', query)
}

export function getRecommendById(id) {
  return request.get('/operation/recommend/' + id)
}

export function saveRecommend(data) {
  return request.post('/operation/recommend/save', data)
}

export function updateRecommend(data) {
  return request.put('/operation/recommend/update', data)
}

export function deleteRecommend(id) {
  return request.delete('/operation/recommend/' + id)
}