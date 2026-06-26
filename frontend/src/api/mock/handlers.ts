import type {
  Result, Channel, PostListItem, Comment, Notification, Report,
  User, UserProfile, HotPostItem, AdminUserItem, CollectItem, Tag
} from '@/types'
import { type Post,
  dbUser, dbChannel, dbTag, dbPost, dbComment, dbNotification, dbReport,
  dbcollect, dbLike, dbHot, dbPassword, nextId
} from './data'

function ok<T>(data: T): Result<T> { return { code: 0, message: 'ok', data } }
function err(code: number, message: string): Result<null> { return { code, message, data: null } }

function delay<T>(v: Result<T>): Promise<Result<T>> {
  return new Promise(res => setTimeout(() => res(v), 120))
}

function page<T>(records: T[], page: number, size: number) {
  page = page || 1; size = size || 20
  const total = records.length
  const start = (page - 1) * size
  return { records: records.slice(start, start + size), total, page, size }
}

function parseUserId(headers: any): number | null {
  const auth = headers?.Authorization || headers?.authorization
  if (!auth) return null
  const m = /mock-user-(\d+)/.exec(auth)
  return m ? Number(m[1]) : null
}

function requireUser(headers: any): User {
  const id = parseUserId(headers)
  if (!id) throw Object.assign(new Error('未登录'), { code: 1001 })
  const u = dbUser.find(x => x.userId === id)
  if (!u) throw Object.assign(new Error('用户不存在'), { code: 1001 })
  if (u.status === 'BANNED') throw Object.assign(new Error('账号已封禁'), { code: 1003 })
  return u
}

function requireRole(headers: any, roles: string[]): User {
  const u = requireUser(headers)
  if (!roles.includes(u.role)) throw Object.assign(new Error('权限不足'), { code: 1002 })
  return u
}

function postToList(p: Post): PostListItem {
  return {
    postId: p.postId, authorId: p.authorId, authorNickname: p.authorNickname!, authorAvatar: p.authorAvatar!,
    channelId: p.channelId, channelName: p.channelName!, title: p.title,
    viewCount: p.viewCount, likeCount: p.likeCount, commentCount: p.commentCount, collectCount: p.collectCount,
    isTop: p.isTop, isEssence: p.isEssence, tags: p.tags || [], createTime: p.createTime
  }
}

function buildChannelTree(list: Channel[]): Channel[] {
  const map = new Map<number, Channel>()
  list.forEach(c => map.set(c.channelId, { ...c, children: [] }))
  const res: Channel[] = []
  map.forEach(c => {
    if (c.parentId === 0) res.push(c)
    else { const parent = map.get(c.parentId); parent && parent.children!.push(c) }
  })
  return res
}

function buildCommentTree(list: Comment[]): Comment[] {
  const map = new Map<number, Comment>()
  list.forEach(c => map.set(c.commentId, { ...c, children: [] }))
  const roots: Comment[] = []
  map.forEach(c => {
    if (c.parentId == null) roots.push(c)
    else { const p = map.get(c.parentId); p && p.children!.push(c) }
  })
  return roots
}

export async function handle(method: string, url: string, body: any, headers: any): Promise<Result<any>> {
  const u = new URL(url, 'http://mock')
  const path = u.pathname
  const q = Object.fromEntries(u.searchParams)
  try {
    // ---- Auth ----
    if (path === '/api/auth/register' && method === 'POST') {
      const { username, password, nickname, email } = body
      if (!username || !password || !nickname) return delay(err(2001, '参数不完整'))
      if (dbUser.some(x => x.username === username)) return delay(err(3001, '用户名已存在'))
      const user: User = { userId: nextId(), username, nickname, avatar: null, signature: null, role: 'STUDENT', status: 'NORMAL', email: email || null, createTime: now() }
      dbUser.push(user); dbPassword[user.userId] = password
      return delay(ok({ userId: user.userId, username, role: 'STUDENT' }))
    }
    if (path === '/api/auth/login' && method === 'POST') {
      const { username, password } = body
      const user = dbUser.find(x => x.username === username)
      if (!user || dbPassword[user.userId] !== password) return delay(err(1003, '账号或密码错误'))
      if (user.status === 'BANNED') return delay(err(3003, '账号已封禁'))
      const token = `mock-user-${user.userId}`
      return delay(ok({ token, userId: user.userId, username, nickname: user.nickname, role: user.role }))
    }
    if (path === '/api/auth/logout' && method === 'POST') { requireUser(headers); return delay(ok(null)) }
    if (path === '/api/auth/me' && method === 'GET') { const u = requireUser(headers); return delay(ok(stripUser(u))) }
    if (path === '/api/auth/password' && method === 'PUT') {
      const u = requireUser(headers); const { oldPassword, newPassword } = body
      if (dbPassword[u.userId] !== oldPassword) return delay(err(3002, '原密码错误'))
      dbPassword[u.userId] = newPassword; return delay(ok(null))
    }

    // ---- Channels ----
    let channelsAll = false
    if (path === '/api/channels/tree' && method === 'GET') {
      const only = q.all === '1'
      return delay(ok(buildChannelTree(only ? dbChannel.slice() : dbChannel.filter(c => c.status === 'NORMAL'))))
    }
    if (path === '/api/admin/channels' && method === 'POST') {
      requireRole(headers, ['ADMIN'])
      const ch: Channel = { channelId: nextId(), parentId: body.parentId || 0, name: body.name, sort: body.sort ?? 0, icon: body.icon || null, description: body.description || null, status: body.status || 'NORMAL' }
      dbChannel.push(ch); return delay(ok(ch))
    }
    if (/^\/api\/admin\/channels\/\d+$/.test(path) && method === 'PUT') {
      requireRole(headers, ['ADMIN']); const id = Number(path.split('/').pop())!
      const ch = dbChannel.find(c => c.channelId === id); if (!ch) return delay(err(3004, '频道不存在'))
      Object.assign(ch, body); return delay(ok(ch))
    }
    if (/^\/api\/admin\/channels\/\d+$/.test(path) && method === 'DELETE') {
      requireRole(headers, ['ADMIN']); const id = Number(path.split('/').pop())!
      const i = dbChannel.findIndex(c => c.channelId === id); if (i >= 0) dbChannel.splice(i, 1)
      return delay(ok(null))
    }

    // ---- Tags ----
    if (path === '/api/tags' && method === 'GET') return delay(ok(dbTag))
    if (path === '/api/tags/hot' && method === 'GET') return delay(ok([...dbTag].sort((a, b) => b.refCount - a.refCount).slice(0, 10)))
    if (path === '/api/admin/tags' && method === 'POST') {
      requireRole(headers, ['ADMIN'])
      const t: Tag = { tagId: nextId(), name: body.name, color: body.color || '#4361ee', refCount: 0 }
      dbTag.push(t); return delay(ok(t))
    }
    if (/^\/api\/admin\/tags\/\d+$/.test(path) && method === 'PUT') {
      requireRole(headers, ['ADMIN']); const id = Number(path.split('/').pop())!
      const t = dbTag.find(x => x.tagId === id); if (!t) return delay(err(3004, '标签不存在'))
      Object.assign(t, body); return delay(ok(t))
    }
    if (/^\/api\/admin\/tags\/\d+$/.test(path) && method === 'DELETE') {
      requireRole(headers, ['ADMIN']); const id = Number(path.split('/').pop())!
      const i = dbTag.findIndex(x => x.tagId === id); if (i >= 0) dbTag.splice(i, 1)
      return delay(ok(null))
    }

    // ---- Posts ----
    if (path === '/api/posts/hot' && method === 'GET') {
      const ranks = Object.entries(dbHot).sort((a, b) => b[1] - a[1])
      const list: HotPostItem[] = ranks.slice(0, 10).map(([pid, score], idx) => {
        const p = dbPost.find(x => x.postId === Number(pid))!
        return { postId: p.postId, rankNo: idx + 1, hotScore: Math.round(score), title: p.title, authorNickname: p.authorNickname!, channelId: p.channelId, channelName: p.channelName!, viewCount: p.viewCount, likeCount: p.likeCount, commentCount: p.commentCount }
      })
      return delay(ok(list))
    }
    if (path === '/api/posts' && method === 'GET') {
      let list = dbPost.filter(p => p.status === 'NORMAL')
      if (q.channelId) list = list.filter(p => p.channelId === Number(q.channelId) || isUnderChannel(p.channelId, Number(q.channelId)))
      if (q.tagIds) { const ids = String(q.tagIds).split(',').map(Number); list = list.filter(p => p.tags?.some(t => ids.includes(t.tagId))) }
      if (q.keyword) { const k = q.keyword.toLowerCase(); list = list.filter(p => p.title.toLowerCase().includes(k) || p.content.toLowerCase().includes(k)) }
      const sort = q.sort || 'latest'
      list = [...list].sort((a, b) => sort === 'hot'
        ? (hotScore(b) - hotScore(a))
        : (new Date(b.createTime).getTime() - new Date(a.createTime).getTime()))
      // 置顶优先
      list.sort((a, b) => b.isTop - a.isTop)
      const data = page(list.map(postToList), Number(q.page) || 1, Number(q.size) || 20)
      return delay(ok(data))
    }
    if (path === '/api/posts' && method === 'POST') {
      const u = requireRole(headers, ['STUDENT', 'TEACHER'])
      const { channelId, title, content, tagIds } = body
      if (!title || !content || !channelId) return delay(err(2001, '参数不完整'))
      if (tagIds?.length > 5) return delay(err(2002, '最多 5 个标签'))
      const ch = dbChannel.find(c => c.channelId === channelId)
      if (!ch || ch.status !== 'NORMAL') return delay(err(3005, '频道不存在或已禁用'))
      const tags = (tagIds || []).map((id: number) => { const t = dbTag.find(x => x.tagId === id); return t ? { tagId: t.tagId, name: t.name, color: t.color } : null }).filter(Boolean)
      const p: Post = {
        postId: nextId(), authorId: u.userId, authorNickname: u.nickname, authorAvatar: u.avatar,
        channelId, channelName: ch.name, title, content, viewCount: 0, likeCount: 0, commentCount: 0, collectCount: 0,
        isTop: 0, isEssence: 0, status: 'NORMAL', tags, createTime: now(), updateTime: now()
      }
      dbPost.unshift(p)
      return delay(ok({ postId: p.postId }))
    }
    if (/^\/api\/posts\/\d+$/.test(path) && method === 'GET') {
      const id = Number(path.split('/').pop())!
      const p = dbPost.find(x => x.postId === id)
      if (!p || p.status === 'DELETED') return delay(err(3006, '帖子不存在'))
      p.viewCount++
      const u = parseUserId(headers)
      const liked = u ? !!dbLike.find(x => x.userId === u && x.targetType === 'POST' && x.targetId === id) : false
      const collected = u ? !!dbcollect.find(x => x.userId === u && x.postId === id) : false
      return delay(ok({ ...postToList(p), content: p.content, status: p.status, updateTime: p.updateTime, liked, collected }))
    }
    if (/^\/api\/posts\/\d+$/.test(path) && method === 'PUT') {
      const u = requireUser(headers); const id = Number(path.split('/').pop())!
      const p = dbPost.find(x => x.postId === id); if (!p) return delay(err(3006, '帖子不存在'))
      if (p.authorId !== u.userId && u.role !== 'ADMIN') return delay(err(1002, '无权编辑'))
      if (body.title) p.title = body.title
      if (body.content) p.content = body.content
      if (body.channelId) { const ch = dbChannel.find(c => c.channelId === body.channelId); if (ch) { p.channelId = ch.channelId; p.channelName = ch.name } }
      if (body.tagIds) { p.tags = body.tagIds.map((id: number) => dbTag.find(t => t.tagId === id)).filter(Boolean) }
      p.updateTime = now()
      return delay(ok({ postId: p.postId }))
    }
    if (/^\/api\/posts\/\d+$/.test(path) && method === 'DELETE') {
      const u = requireUser(headers); const id = Number(path.split('/').pop())!
      const i = dbPost.findIndex(x => x.postId === id); if (i < 0) return delay(err(3006, '帖子不存在'))
      if (dbPost[i].authorId !== u.userId && u.role !== 'ADMIN') return delay(err(1002, '无权删除'))
      dbPost[i].status = 'DELETED'; return delay(ok(null))
    }
    if (/^\/api\/admin\/posts\/\d+\/top$/.test(path) && method === 'POST') {
      requireRole(headers, ['ADMIN']); const id = Number(path.split('/')[4])!
      const p = dbPost.find(x => x.postId === id); if (!p) return delay(err(3006, '帖子不存在'))
      p.isTop = p.isTop ? 0 : 1; return delay(ok({ isTop: p.isTop }))
    }
    if (/^\/api\/admin\/posts\/\d+\/essence$/.test(path) && method === 'POST') {
      requireRole(headers, ['ADMIN']); const id = Number(path.split('/')[4])!
      const p = dbPost.find(x => x.postId === id); if (!p) return delay(err(3006, '帖子不存在'))
      p.isEssence = p.isEssence ? 0 : 1; return delay(ok({ isEssence: p.isEssence }))
    }

    // ---- Comments ----
    if (/^\/api\/posts\/\d+\/comments$/.test(path) && method === 'GET') {
      const pid = Number(path.split('/')[3])!
      const u = parseUserId(headers)
      const list = dbComment.filter(c => c.postId === pid && !c.isDeleted)
      const tree = buildCommentTree(list)
      tree.forEach(c => markLiked(c, u))
      return delay(ok(tree))
    }
    if (path === '/api/comments' && method === 'POST') {
      const u = requireRole(headers, ['STUDENT', 'TEACHER'])
      const { postId, parentId, content } = body
      if (!content) return delay(err(2001, '内容不能为空'))
      const post = dbPost.find(x => x.postId === postId); if (!post) return delay(err(3006, '帖子不存在'))
      let rootId = null
      if (parentId) { const parent = dbComment.find(c => c.commentId === parentId); if (parent) rootId = parent.rootId ?? parent.commentId }
      const c: Comment = { commentId: nextId(), postId, parentId: parentId ?? null, rootId, authorId: u.userId, authorNickname: u.nickname, authorAvatar: u.avatar ?? undefined, authorRole: u.role, content, likeCount: 0, floor: dbComment.filter(x => x.postId === postId).length + 1, createTime: now() }
      dbComment.push(c); post.commentCount++
      if (post.authorId !== u.userId) {
        dbNotification.unshift({ notifyId: nextId(), receiverId: post.authorId, senderId: u.userId, type: parentId ? 'REPLY' : 'COMMENT', targetType: 'POST', targetId: postId, content: parentId ? '回复了你的评论' : `评论了你的帖子《${post.title}》`, isRead: 0, createTime: now(), senderNickname: u.nickname })
      }
      return delay(ok(c))
    }
    if (/^\/api\/comments\/\d+$/.test(path) && method === 'DELETE') {
      const u = requireUser(headers); const id = Number(path.split('/').pop())!
      const c = dbComment.find(x => x.commentId === id); if (!c) return delay(err(3007, '评论不存在'))
      if (c.authorId !== u.userId && u.role !== 'ADMIN') return delay(err(1002, '无权删除'))
      c.isDeleted = 1; return delay(ok(null))
    }

    // ---- Like / Collect ----
    if (path === '/api/likes' && method === 'POST') {
      const u = requireRole(headers, ['STUDENT', 'TEACHER']); const { targetType, targetId } = body
      const i = dbLike.findIndex(x => x.userId === u.userId && x.targetType === targetType && x.targetId === targetId)
      let liked: boolean
      if (i >= 0) { dbLike.splice(i, 1); liked = false } else { dbLike.push({ likeId: nextId(), userId: u.userId, targetType, targetId }); liked = true }
      if (targetType === 'POST') { const p = dbPost.find(x => x.postId === targetId); if (p) p.likeCount = Math.max(0, p.likeCount + (liked ? 1 : -1)) }
      if (targetType === 'COMMENT') { const c = dbComment.find(x => x.commentId === targetId); if (c) c.likeCount = Math.max(0, c.likeCount + (liked ? 1 : -1)) }
      return delay(ok({ liked }))
    }
    if (path === '/api/likes/status' && method === 'GET') {
      const u = requireUser(headers); const { targetType, targetId } = q
      const liked = !!dbLike.find(x => x.userId === u.userId && x.targetType === targetType && x.targetId === Number(targetId))
      return delay(ok({ liked }))
    }
    if (path === '/api/collects' && method === 'POST') {
      const u = requireRole(headers, ['STUDENT', 'TEACHER']); const { postId } = body
      const i = dbcollect.findIndex(x => x.userId === u.userId && x.postId === postId)
      let collected: boolean
      if (i >= 0) { dbcollect.splice(i, 1); collected = false } else { dbcollect.push({ collectId: nextId(), userId: u.userId, postId }); collected = true }
      const p = dbPost.find(x => x.postId === postId); if (p) p.collectCount = Math.max(0, p.collectCount + (collected ? 1 : -1))
      return delay(ok({ collected }))
    }
    if (path === '/api/collects' && method === 'GET') {
      const u = requireUser(headers)
      const list: CollectItem[] = dbcollect.filter(c => c.userId === u.userId).map(c => {
        const p = dbPost.find(x => x.postId === c.postId)
        return { collectId: c.collectId, postId: c.postId, title: p?.title || '', createTime: now() } as CollectItem
      }).reverse()
      return delay(ok(list))
    }

    // ---- Notifications ----
    if (path === '/api/notifications' && method === 'GET') {
      const u = requireUser(headers)
      const list = dbNotification.filter(n => n.receiverId === u.userId).sort((a, b) => new Date(b.createTime).getTime() - new Date(a.createTime).getTime())
      const sender = (id: number | null) => dbUser.find(x => x.userId === id)
      list.forEach(n => { const s = sender(n.senderId); n.senderNickname = s?.nickname || '系统'; n.senderAvatar = s?.avatar || '' })
      return delay(ok(page(list, Number(q.page) || 1, Number(q.size) || 20)))
    }
    if (path === '/api/notifications/unread-count' && method === 'GET') {
      const u = requireUser(headers)
      return delay(ok({ count: dbNotification.filter(n => n.receiverId === u.userId && !n.isRead).length }))
    }
    if (path === '/api/notifications/read' && method === 'PUT') {
      const u = requireUser(headers); const { notifyId } = body
      if (notifyId) dbNotification.forEach(n => { if (n.notifyId === notifyId && n.receiverId === u.userId) n.isRead = 1 })
      else dbNotification.forEach(n => { if (n.receiverId === u.userId) n.isRead = 1 })
      return delay(ok(null))
    }

    // ---- Reports ----
    if (path === '/api/reports' && method === 'POST') {
      const u = requireRole(headers, ['STUDENT', 'TEACHER'])
      const { targetType, targetId, reasonType, reasonText } = body
      const r: Report = { reportId: nextId(), reporterId: u.userId, targetType, targetId, reasonType, reasonText: reasonText || null, status: 'PENDING', handlerId: null, handleRemark: null, createTime: now(), handleTime: null, reporterNickname: u.nickname }
      dbReport.unshift(r)
      return delay(ok({ reportId: r.reportId }))
    }
    if (path === '/api/admin/reports' && method === 'GET') {
      requireRole(headers, ['ADMIN'])
      const list = dbReport.map(r => ({ ...r, targetTitle: r.targetType === 'POST' ? dbPost.find(p => p.postId === r.targetId)?.title : '' }))
      return delay(ok(page(list, Number(q.page) || 1, Number(q.size) || 20)))
    }
    if (/^\/api\/admin\/reports\/\d+\/handle$/.test(path) && method === 'POST') {
      const u = requireRole(headers, ['ADMIN']); const id = Number(path.split('/')[4])!
      const r = dbReport.find(x => x.reportId === id); if (!r) return delay(err(3008, '举报不存在'))
      r.status = body.status; r.handlerId = u.userId; r.handleRemark = body.handleRemark || null; r.handleTime = now()
      return delay(ok(null))
    }

    // ---- User profile ----
    if (/^\/api\/users\/\d+\/profile$/.test(path) && method === 'GET') {
      const id = Number(path.split('/')[3])!
      const usr = dbUser.find(x => x.userId === id); if (!usr) return delay(err(3009, '用户不存在'))
      const profile: UserProfile = { ...stripUser(usr), postCount: dbPost.filter(p => p.authorId === id && p.status === 'NORMAL').length, likeCount: dbLike.filter(x => x.userId === id && x.targetType === 'POST').length, collectCount: dbcollect.filter(x => x.userId === id).length }
      return delay(ok(profile))
    }
    if (/^\/api\/users\/\d+\/posts$/.test(path) && method === 'GET') {
      const id = Number(path.split('/')[3])!
      const list = dbPost.filter(p => p.authorId === id && p.status === 'NORMAL').map(postToList)
      return delay(ok(page(list, Number(q.page) || 1, Number(q.size) || 20)))
    }
    if (path === '/api/user/profile' && method === 'PUT') {
      const u = requireUser(headers)
      if (body.nickname != null) u.nickname = body.nickname
      if (body.avatar != null) u.avatar = body.avatar
      if (body.signature != null) u.signature = body.signature
      if (body.email != null) u.email = body.email
      return delay(ok(stripUser(u)))
    }

    // ---- Admin users ----
    if (path === '/api/admin/users' && method === 'GET') {
      requireRole(headers, ['ADMIN'])
      const list: AdminUserItem[] = dbUser.map(u => ({ ...stripUser(u), postCount: dbPost.filter(p => p.authorId === u.userId).length }))
      return delay(ok(page(list, Number(q.page) || 1, Number(q.size) || 20)))
    }
    if (/^\/api\/admin\/users\/\d+\/ban$/.test(path) && method === 'POST') {
      requireRole(headers, ['ADMIN']); const id = Number(path.split('/')[4])!
      const usr = dbUser.find(x => x.userId === id); if (!usr) return delay(err(3009, '用户不存在'))
      usr.status = 'BANNED'; return delay(ok(null))
    }
    if (/^\/api\/admin\/users\/\d+\/unban$/.test(path) && method === 'POST') {
      requireRole(headers, ['ADMIN']); const id = Number(path.split('/')[4])!
      const usr = dbUser.find(x => x.userId === id); if (!usr) return delay(err(3009, '用户不存在'))
      usr.status = 'NORMAL'; return delay(ok(null))
    }

    return delay(err(4004, `Mock 未实现: ${method} ${path}`))
  } catch (e: any) {
    return Promise.resolve(err(e.code || 5000, e.message || '服务异常'))
  }
}

// helpers
function stripUser(u: User): User {
  return { userId: u.userId, username: u.username, nickname: u.nickname, avatar: u.avatar, signature: u.signature, role: u.role, status: u.status, email: u.email, createTime: u.createTime }
}
function now(): string { return new Date().toISOString().slice(0, 19).replace('T', ' ') }
function hotScore(p: Post): number { return p.viewCount * 1 + p.likeCount * 3 + p.commentCount * 2 + (p.isTop ? 1000 : 0) + (p.isEssence ? 500 : 0) }
function isUnderChannel(cid: number, rootId: number): boolean {
  if (cid === rootId) return true
  let cur: Channel | undefined = dbChannel.find(c => c.channelId === cid)
  while (cur && cur.parentId !== 0) { if (cur.parentId === rootId) return true; cur = dbChannel.find(c => c.channelId === cur!.parentId) }
  return false
}
function markLiked(c: Comment, uid: number | null) {
  if (uid) c.liked = !!dbLike.find(x => x.userId === uid && x.targetType === 'COMMENT' && x.targetId === c.commentId)
  c.children?.forEach(ch => markLiked(ch, uid))
}