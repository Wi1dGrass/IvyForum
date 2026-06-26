const TOKEN_KEY = 'sf_token'
const USER_KEY = 'sf_user'

export function getToken(): string | null { return localStorage.getItem(TOKEN_KEY) }
export function setToken(token: string): void { localStorage.setItem(TOKEN_KEY, token) }
export function clearToken(): void { localStorage.removeItem(TOKEN_KEY); localStorage.removeItem(USER_KEY) }

export function saveUser(user: any): void { localStorage.setItem(USER_KEY, JSON.stringify(user)) }
export function getStoredUser(): any { const raw = localStorage.getItem(USER_KEY); return raw ? JSON.parse(raw) : null }