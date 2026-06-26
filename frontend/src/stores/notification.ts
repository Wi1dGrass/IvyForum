import { defineStore } from 'pinia'
import { notificationApi } from '@/api/notification'

export const useNotificationStore = defineStore('notification', {
  state: () => ({
    unreadCount: 0,
    timer: null as any
  }),
  actions: {
    async fetchUnread() {
      try { const r = await notificationApi.unreadCount(); this.unreadCount = r.count } catch {}
    },
    startPoll() {
      this.stopPoll()
      this.fetchUnread()
      this.timer = setInterval(() => this.fetchUnread(), 30000)
    },
    stopPoll() {
      if (this.timer) { clearInterval(this.timer); this.timer = null }
    },
    clear() { this.unreadCount = 0 }
  }
})