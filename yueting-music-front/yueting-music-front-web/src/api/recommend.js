import request from './request'

export function getHomeData() {
  return request.get('/web/home/data')
}
export function getSingerDetail(id) {
  return request.get('/web/resource/singer/' + id)
}

export function getPlaylistDetail(id) {
  return request.get('/web/resource/playlist/' + id)
}