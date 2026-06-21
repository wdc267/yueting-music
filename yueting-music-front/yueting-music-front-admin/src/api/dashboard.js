import request from './request'

export function getDashboardStatistics() {
  return request.get('/dashboard/statistics')
}

export function getTopSongs() {
  return request.get('/dashboard/top/songs')
}

export function getTopSingers() {
  return request.get('/dashboard/top/singers')
}

export function getTopPlaylists() {
  return request.get('/dashboard/top/playlists')
}
