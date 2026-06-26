import request from './request'
import type { CollectToggleRequest, CollectItem } from '@/types'

export const collectApi = {
  toggle: (data: CollectToggleRequest) => request.post<{ collected: boolean }>('/collects', data),
  list: () => request.get<CollectItem[]>('/collects')
}