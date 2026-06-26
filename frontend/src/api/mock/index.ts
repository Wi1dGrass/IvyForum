import type { AxiosAdapter, AxiosRequestConfig, AxiosResponse } from 'axios'
import { handle } from './handlers'

function buildUrl(config: AxiosRequestConfig): string {
  const base = config.baseURL || ''
  let url = config.url || ''
  url = url.startsWith('http') ? url : base + url
  const params = config.params
  if (params && typeof params === 'object') {
    const usp = new URLSearchParams()
    Object.entries(params).forEach(([k, v]) => {
      if (v === undefined || v === null || v === '') return
      if (Array.isArray(v)) usp.append(k, v.join(','))
      else usp.append(k, String(v))
    })
    const qs = usp.toString()
    if (qs) url += (url.includes('?') ? '&' : '?') + qs
  }
  return url
}

export const mockAdapter: AxiosAdapter = async (config: any) => {
  const method = (config.method || 'get').toUpperCase()
  const url = buildUrl(config)
  let body: any = config.data
  if (typeof body === 'string') {
    try { body = JSON.parse(body) } catch { body = {} }
  }
  const headers: any = {}
  const h = config.headers as any
  if (h) {
    Object.keys(h).forEach(k => { const v = (h as any)[k]; if (v != null) headers[k] = v })
    const common = (config.headers as any).common as any
    if (common) Object.keys(common).forEach(k => { const v = common[k]; if (v != null) headers[k] = v })
  }
  const result = await handle(method, url, body || {}, headers)
  const resp: AxiosResponse = {
    data: result,
    status: 200,
    statusText: 'OK',
    headers: {},
    config,
    request: {}
  }
  return resp as any
}