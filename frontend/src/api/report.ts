import request from './request'
import type { PageResult, Report, ReportCreateRequest, ReportHandleRequest } from '@/types'

export const reportApi = {
  create: (data: ReportCreateRequest) => request.post<{ reportId: number }>('/reports', data),
  adminList: (page = 1, size = 20) =>
    request.get<PageResult<Report>>('/admin/reports', { params: { page, size } }),
  handle: (id: number, data: ReportHandleRequest) =>
    request.post<void>(`/admin/reports/${id}/handle`, data)
}