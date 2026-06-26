import { defineStore } from 'pinia'
import { authApi } from '@/api/auth'
import { userApi } from '@/api/user'
import { getToken, setToken, clearToken, saveUser, getStoredUser } from '@/utils/auth'
import type { LoginRequest, RegisterRequest, User, PasswordUpdateRequest, ProfileUpdateRequest } from '@/types'

interface State {
  token: string | null
  user: User | null
}

export const useUserStore = defineStore('user', {
  state: (): State => ({
    token: getToken(),
    user: getStoredUser()
  }),
  getters: {
    isLoggedIn: (s) => !!s.token,
    isAdmin: (s) => s.user?.role === 'ADMIN',
    role: (s) => s.user?.role || 'GUEST'
  },
  actions: {
    async login(data: LoginRequest) {
      const res = await authApi.login(data)
      this.token = res.token
      setToken(res.token)
      const me = await authApi.me()
      this.user = me
      saveUser(me)
    },
    async register(data: RegisterRequest) {
      return authApi.register(data)
    },
    async fetchMe() {
      if (!this.token) return
      try {
        this.user = await authApi.me()
        saveUser(this.user)
      } catch {
        this.logout()
      }
    },
    async updatePassword(data: PasswordUpdateRequest) {
      await authApi.updatePassword(data)
    },
    async updateProfile(data: ProfileUpdateRequest) {
      const u = await userApi.updateProfile(data)
      this.user = u
      saveUser(u)
    },
    logout() {
      authApi.logout().catch(() => {})
      this.token = null
      this.user = null
      clearToken()
    }
  }
})