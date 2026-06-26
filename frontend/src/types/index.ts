export interface Result<T> { code: number; message: string; data: T }
export interface PageResult<T> { records: T[]; total: number; page: number; size: number }

export type Role = 'STUDENT' | 'TEACHER' | 'ADMIN'
export type UserStatus = 'NORMAL' | 'BANNED'
export type PostStatus = 'NORMAL' | 'PENDING' | 'REJECTED' | 'DELETED'
export type ChannelStatus = 'NORMAL' | 'DISABLED'
export type ReportStatus = 'PENDING' | 'PASS' | 'REJECT'
export type NotifyType = 'COMMENT' | 'REPLY' | 'MENTION' | 'AUDIT'
export type TargetType = 'POST' | 'COMMENT' | 'USER'

export interface User {
  userId: number; username: string; nickname: string; avatar: string | null
  signature: string | null; role: Role; status: UserStatus; email: string | null
  createTime?: string
}

export interface LoginResponse {
  token: string; userId: number; username: string
  nickname: string; role: Role
}

export interface RegisterRequest {
  username: string; password: string; nickname: string; email?: string
}
export interface LoginRequest { username: string; password: string }
export interface PasswordUpdateRequest { oldPassword: string; newPassword: string }
export interface ProfileUpdateRequest { nickname?: string; avatar?: string; signature?: string; email?: string }

export interface Channel {
  channelId: number; parentId: number; name: string; sort: number
  icon: string | null; description: string | null; status: ChannelStatus
  children?: Channel[]
}
export interface ChannelSaveRequest {
  parentId: number; name: string; sort?: number; icon?: string; description?: string; status?: ChannelStatus
}

export interface Tag { tagId: number; name: string; color: string; refCount: number }
export interface TagSaveRequest { name: string; color: string }

export interface TagBrief { tagId: number; name: string; color: string }

export type PostSort = 'latest' | 'hot'

export interface PostListItem {
  postId: number; authorId: number; authorNickname: string; authorAvatar: string | null
  channelId: number; channelName: string; title: string
  contentAbstract?: string; firstImage?: string
  viewCount: number; likeCount: number; commentCount: number; collectCount: number
  isTop: number; isEssence: number; tags: TagBrief[]
  createTime: string
}

export interface PostDetail extends PostListItem {
  content: string; status: PostStatus; updateTime: string
  liked?: boolean; collected?: boolean
}

export interface PostListQuery {
  channelId?: number; tagIds?: number[]; keyword?: string
  sort?: PostSort; page?: number; size?: number
}
export interface PostSaveRequest {
  channelId: number; title: string; content: string; tagIds: number[]
}

export interface Comment {
  commentId: number; postId: number; parentId: number | null; rootId: number | null
  authorId: number; authorNickname?: string; authorAvatar?: string; authorRole?: Role
  content: string; likeCount: number; floor: number
  isDeleted?: number; createTime: string; liked?: boolean
  children?: Comment[]
}
export interface CommentCreateRequest { postId: number; parentId: number | null; content: string }

export interface LikeToggleRequest { targetType: TargetType; targetId: number }
export interface LikeStatusResult { liked: boolean }

export interface CollectToggleRequest { postId: number }
export interface CollectItem { collectId: number; postId: number; title: string; createTime: string }

export interface Notification {
  notifyId: number; receiverId: number; senderId: number | null
  type: NotifyType; targetType: TargetType | null; targetId: number | null
  content: string | null; isRead: number; createTime: string
  senderNickname?: string; senderAvatar?: string
}

export interface Report {
  reportId: number; reporterId: number; targetType: TargetType; targetId: number
  reasonType: string; reasonText: string | null; status: ReportStatus
  handlerId: number | null; handleRemark: string | null
  createTime: string; handleTime: string | null
  reporterNickname?: string; targetTitle?: string
}
export interface ReportCreateRequest {
  targetType: TargetType; targetId: number; reasonType: string; reasonText?: string
}
export interface ReportHandleRequest { status: ReportStatus; handleRemark?: string }

export interface UserProfile extends User {
  postCount?: number; likeCount?: number; collectCount?: number
}

export interface AdminUserItem extends User {
  postCount?: number
}
export interface HotPostItem {
  postId: number; rankNo: number; hotScore: number; title: string
  authorNickname: string; channelId: number; channelName: string
  viewCount: number; likeCount: number; commentCount: number
}