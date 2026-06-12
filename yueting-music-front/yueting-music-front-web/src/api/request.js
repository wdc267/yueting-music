import axios from 'axios'

const request = axios.create({
  baseURL: '/api',
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json'
  }
})

request.interceptors.response.use(
  (response) => {
    const res = response.data
    if (res.code !== 200) {
      console.error('请求失败:', res.msg)
      return Promise.reject(new Error(res.msg || '请求失败'))
    }
    return res
  },
  (error) => {
    console.error('网络异常:', error.message)
    return Promise.reject(error)
  }
)

export default request