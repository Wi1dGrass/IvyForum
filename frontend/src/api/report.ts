import request from './request'
import type { PageResult, Report, ReportCreateRequest, ReportHandleRequest } from '@/types'

export const reportApi = {
  create: (data: ReportCreateRequest) => request.post<{ reportId: number }>('/reports', data),
  adminList: () =>
    request.get<Report[]>('/admin/reports'),
  handle: (id: number, data: ReportHandleRequest) =>
    request.post<void>(`/admin/reports/${id}/handle`, data)
}