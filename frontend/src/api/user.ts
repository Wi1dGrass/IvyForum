import request from './request'
import type {
  UserProfile, PostListItem, PageResult, ProfileUpdateRequest,
  AdminUserItem, CollectItem, User
} from '@/types'

export const userApi = {
  profile: (id: number) => request.get<UserProfile>(`/users/${id}/profile`),
  posts: (id: number) =>
    request.get<PostListItem[]>(`/users/${id}/posts`),
  collects: (id: number) => request.get<CollectItem[]>(`/users/${id}/collects`),
  updateProfile: (data: ProfileUpdateRequest) => request.put<User>('/user/profile', data)
}

export const adminUserApi = {
  list: (page = 1, size = 20) =>
    request.get<AdminUserItem[]>('/admin/users'),
  ban: (id: number) => request.post<void>(`/admin/users/${id}/ban`),
  unban: (id: number) => request.post<void>(`/admin/users/${id}/unban`)
}