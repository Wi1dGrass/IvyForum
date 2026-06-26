import request from './request'
import type { Channel, ChannelSaveRequest } from '@/types'

export const channelApi = {
  tree: (all = false) => request.get<Channel[]>('/channels/tree', { params: all ? { all: 1 } : {} }),
  treeAll: () => request.get<Channel[]>('/channels/tree', { params: { all: 1 } }),
  create: (data: ChannelSaveRequest) => request.post<Channel>('/admin/channels', data),
  update: (id: number, data: ChannelSaveRequest) => request.put<Channel>(`/admin/channels/${id}`, data),
  remove: (id: number) => request.delete(`/admin/channels/${id}`)
}