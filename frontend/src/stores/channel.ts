import { defineStore } from 'pinia'
import { channelApi } from '@/api/channel'
import type { Channel } from '@/types'

export const useChannelStore = defineStore('channel', {
  state: () => ({
    tree: [] as Channel[],
    loaded: false
  }),
  actions: {
    async load(force = false, all = false) {
      if (!force && !all && this.loaded) return
      this.tree = await channelApi.tree(all)
      this.loaded = true
    },
    findName(id: number): string {
      const walk = (list: Channel[]): string => {
        for (const c of list) { if (c.channelId === id) return c.name; if (c.children?.length) { const r = walk(c.children); if (r) return r } }
        return ''
      }
      return walk(this.tree)
    }
  }
})