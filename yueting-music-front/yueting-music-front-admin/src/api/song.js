import request from './request'

export function getSongList(query) {
  return request.post('/content/song/list', query)
}

export function getSongById(id) {
  return request.get('/content/song/' + id)
}

export function saveSong(data) {
  return request.post('/content/song/save', data)
}

export function updateSong(data) {
  return request.put('/content/song/update', data)
}

export function deleteSong(id) {
  return request.delete('/content/song/' + id)
}