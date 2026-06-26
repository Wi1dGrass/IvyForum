import axios, { AxiosResponse, InternalAxiosRequestConfig } from 'axios'
import { ElMessage } from 'element-plus'
import { getToken, clearToken } from '@/utils/auth'
import { mockAdapter } from '@/api/mock'

interface TypedAxios {
  get<T = any>(url: string, config?: any): Promise<T>
  post<T = any>(url: string, data?: any, config?: any): Promise<T>
  put<T = any>(url: string, data?: any, config?: any): Promise<T>
  delete<T = any>(url: string, config?: any): Promise<T>
}

const useMock = (import.meta.env.VITE_USE_MOCK ?? 'true') === 'true'

const instance = axios.create({
  baseURL: '/api',
  timeout: 15000,
  adapter: useMock ? mockAdapter as any : undefined
})

instance.interceptors.request.use(config => {
  const token = getToken()
  if (token) config.headers!.Authorization = `Bearer ${token}`
  return config
})

instance.interceptors.response.use(
  (resp: AxiosResponse) => {
    const d = resp.data
    if (d.code !== 0) {
      ElMessage.error(d.message || '请求失败')
      if ([1001, 1002].includes(d.code)) { clearToken(); location.href = '/login' }
      return Promise.reject(new Error(d.message))
    }
    return d.data
  },
  err => {
    if (err.response?.status === 401) { clearToken(); ElMessage.error('登录过期'); location.href = '/login' }
    else if (err?.code !== 'MOCK_SKIP') ElMessage.error(err.message || '网络错误')
    return Promise.reject(err)
  }
)

const request = instance as unknown as TypedAxios
export default request