export function hotLabel(score: number): string {
  if (score > 5000) return '🔥 爆款'
  if (score > 1500) return '🔥 热门'
  if (score > 500) return '🔥 升温'
  return '一般'
}

export function hotColor(score: number): string {
  if (score > 5000) return '#f5222d'
  if (score > 1500) return '#f77f00'
  if (score > 500) return '#4361ee'
  return '#9aa7bd'
}