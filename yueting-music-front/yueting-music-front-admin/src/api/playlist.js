import request from './request'

export function getPlaylistList(query) {
  return request.post('/content/playlist/list', query)
}

export function getPlaylistById(id) {
  return request.get('/content/playlist/' + id)
}

export function savePlaylist(data) {
  return request.post('/content/playlist/save', data)
}

export function updatePlaylist(data) {
  return request.put('/content/playlist/update', data)
}

export function deletePlaylist(id) {
  return request.delete('/content/playlist/' + id)
}