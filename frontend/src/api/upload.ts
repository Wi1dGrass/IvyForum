import request from './request'

export const uploadApi = {
  image: (file: File) => {
    const fd = new FormData()
    fd.append('file', file)
    return request.post<{ url: string }>('/upload/image', fd, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  },
  avatar: (file: File) => {
    const fd = new FormData()
    fd.append('file', file)
    return request.post<{ url: string }>('/upload/avatar', fd, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  }
}