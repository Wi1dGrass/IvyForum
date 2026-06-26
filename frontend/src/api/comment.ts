import request from './request'
import type { Comment, CommentCreateRequest } from '@/types'

export const commentApi = {
  treeByPost: (postId: number) => request.get<Comment[]>(`/posts/${postId}/comments`),
  create: (data: CommentCreateRequest) => request.post<Comment>('/comments', data),
  remove: (id: number) => request.delete(`/comments/${id}`)
}