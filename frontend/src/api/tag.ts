import request from './request'
import type { Tag, TagSaveRequest } from '@/types'

export const tagApi = {
  list: () => request.get<Tag[]>('/tags'),
  hot: () => request.get<Tag[]>('/tags/hot'),
  create: (data: TagSaveRequest) => request.post<Tag>('/admin/tags', data),
  update: (id: number, data: TagSaveRequest) => request.put<Tag>(`/admin/tags/${id}`, data),
  remove: (id: number) => request.delete(`/admin/tags/${id}`)
}