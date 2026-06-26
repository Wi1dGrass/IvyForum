export function formatTime(input: string | Date | null | undefined): string {
  if (!input) return ''
  const d = typeof input === 'string' ? new Date(input.replace(/-/g, '/')) : input
  if (isNaN(d.getTime())) return String(input)
  const now = new Date()
  const diff = (now.getTime() - d.getTime()) / 1000
  if (diff < 60) return '刚刚'
  if (diff < 3600) return `${Math.floor(diff / 60)} 分钟前`
  if (diff < 86400) return `${Math.floor(diff / 3600)} 小时前`
  if (diff < 2592000) return `${Math.floor(diff / 86400)} 天前`
  const pad = (n: number) => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`
}

export function formatDate(input: string | Date | null | undefined): string {
  if (!input) return ''
  const d = typeof input === 'string' ? new Date(input.replace(/-/g, '/')) : input
  if (isNaN(d.getTime())) return String(input)
  const pad = (n: number) => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`
}

export function formatCount(n: number): string {
  if (n < 1000) return String(n)
  if (n < 10000) return (n / 1000).toFixed(1) + 'k'
  if (n < 100000000) return (n / 10000).toFixed(1) + 'w'
  return (n / 100000000).toFixed(1) + '亿'
}