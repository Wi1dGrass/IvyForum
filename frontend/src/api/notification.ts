import request from './request'
import type { Notification, PageResult } from '@/types'

export const notificationApi = {
  list: (page = 1, size = 20) => request.get<PageResult<Notification>>('/notifications', { params: { page, size } }),
  unreadCount: () => request.get<{ count: number }>('/notifications/unread-count'),
  read: (notifyId?: number) => request.put<void>('/notifications/read', { notifyId })
}