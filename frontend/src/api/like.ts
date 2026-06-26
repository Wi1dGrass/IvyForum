import request from './request'
import type { LikeToggleRequest, LikeStatusResult } from '@/types'

export const likeApi = {
  toggle: (data: LikeToggleRequest) => request.post<{ liked: boolean }>('/likes', data),
  status: (targetType: string, targetId: number) =>
    request.get<LikeStatusResult>('/likes/status', { params: { targetType, targetId } })
}