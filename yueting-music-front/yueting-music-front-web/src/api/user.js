import request from './request'

export function login(data) {
  return request.post('/web/user/login', data)
}

export function register(data) {
  return request.post('/web/user/register', data)
}

export function uploadAvatar(userId, file) {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('userId', userId)
  return request.post('/web/user/avatar', formData, { headers: { 'Content-Type': 'multipart/form-data' } })
}

export function getUserInfo(id) {
  return request.get('/web/user/info/' + id)
}

// 点赞相关
export function toggleLike(data) {
  return request.post('/web/user/like/toggle', data)
}

export function getLikeStatus(userId, resourceType, resourceId) {
  return request.get('/web/user/like/status', { params: { userId, resourceType, resourceId } })
}

// 收藏相关
export function toggleFavorite(data) {
  return request.post('/web/user/favorite/toggle', data)
}

export function getFavoriteStatus(userId, resourceType, resourceId) {
  return request.get('/web/user/favorite/status', { params: { userId, resourceType, resourceId } })
}

// 个人中心
export function getUserProfile(userId) {
  return request.get('/web/user/profile', { params: { userId } })
}

export function getLikedSongs(userId) {
  return request.get('/web/user/liked/songs', { params: { userId } })
}

export function getFavoriteSongs(userId) {
  return request.get('/web/user/favorite/songs', { params: { userId } })
}

export function getFavoritePlaylists(userId) {
  return request.get('/web/user/favorite/playlists', { params: { userId } })
}

export function getFavoriteSingers(userId) {
  return request.get('/web/user/favorite/singers', { params: { userId } })
}
// 播放记录
export function recordPlay(songId, userId) {
  return request.post('/web/user/play/record', null, { params: { songId, userId } })
}
