import request from './request'
import type { LoginRequest, LoginResponse, RegisterRequest, User, PasswordUpdateRequest } from '@/types'

export const authApi = {
  register: (data: RegisterRequest) => request.post<{ userId: number; username: string; role: string }>('/auth/register', data),
  login: (data: LoginRequest) => request.post<LoginResponse>('/auth/login', data),
  logout: () => request.post('/auth/logout'),
  me: () => request.get<User>('/auth/me'),
  updatePassword: (data: PasswordUpdateRequest) => request.put('/auth/password', data)
}