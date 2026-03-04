import axios from 'axios'

const request = axios.create({
  baseURL: 'http://localhost:9199/api',
  timeout: 30000,
  withCredentials: true,
})

request.interceptors.response.use(
  (response) => response.data,
  (error) => Promise.reject(error)
)

export default request
