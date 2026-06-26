import request from './request'
import type { PageResult, PostListItem, PostDetail, PostSaveRequest, PostListQuery, HotPostItem } from '@/types'

export const postApi = {
  list: (q: PostListQuery) => request.get<PageResult<PostListItem>>('/posts', { params: q }),
  hot: () => request.get<HotPostItem[]>('/posts/hot'),
  detail: (id: number) => request.get<PostDetail>(`/posts/${id}`),
  create: (data: PostSaveRequest) => request.post<number>('/posts', data),
  update: (id: number, data: PostSaveRequest) => request.put<number>(`/posts/${id}`, data),
  remove: (id: number) => request.delete(`/posts/${id}`),
  toggleTop: (id: number) => request.post<void>(`/admin/posts/${id}/top`),
  toggleEssence: (id: number) => request.post<void>(`/admin/posts/${id}/essence`)
}