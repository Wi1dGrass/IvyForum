import type {
  Channel, Tag, Comment, Notification, Report, User, TagBrief
} from '@/types'

export interface Post {
  postId: number; authorId: number; authorNickname: string | null; authorAvatar: string | null
  channelId: number; channelName: string | null; title: string; content: string
  viewCount: number; likeCount: number; commentCount: number; collectCount: number
  isTop: number; isEssence: number; status: string
  tags: TagBrief[]; createTime: string; updateTime: string
}

export const dbUser: User[] = [
  { userId: 1, username: 'admin', nickname: '管理员', avatar: '', signature: '论坛守护者', role: 'ADMIN', status: 'NORMAL', email: 'admin@school.edu', createTime: '2026-06-20 09:00:00' },
  { userId: 2, username: 'teacher01', nickname: '王老师', avatar: '', signature: '答疑解惑', role: 'TEACHER', status: 'NORMAL', email: 'wang@school.edu', createTime: '2026-06-20 09:10:00' },
  { userId: 3, username: 'stu01', nickname: '小明', avatar: '', signature: '努力学习ing', role: 'STUDENT', status: 'NORMAL', email: 'ming@school.edu', createTime: '2026-06-22 10:00:00' },
  { userId: 4, username: 'stu02', nickname: '小红', avatar: '', signature: '爱技术', role: 'STUDENT', status: 'NORMAL', email: 'hong@school.edu', createTime: '2026-06-23 11:00:00' }
]

export const dbChannel: Channel[] = [
  { channelId: 1, parentId: 0, name: '学习交流', sort: 1, icon: 'Reading', description: '课程讨论', status: 'NORMAL' },
  { channelId: 2, parentId: 0, name: '校园生活', sort: 2, icon: 'House', description: '校园点滴', status: 'NORMAL' },
  { channelId: 3, parentId: 0, name: '技术分享', sort: 3, icon: 'Cpu', description: '技术干货', status: 'NORMAL' },
  { channelId: 4, parentId: 0, name: '求职招聘', sort: 4, icon: 'Briefcase', description: '面试/招聘', status: 'NORMAL' },
  { channelId: 5, parentId: 0, name: '答疑解惑', sort: 5, icon: 'ChatLineSquare', description: '提问专区', status: 'NORMAL' },
  { channelId: 6, parentId: 1, name: '课程作业', sort: 1, icon: '', description: '作业相关', status: 'NORMAL' },
  { channelId: 7, parentId: 1, name: '考试经验', sort: 2, icon: '', description: '考试技巧', status: 'NORMAL' },
  { channelId: 8, parentId: 3, name: '后端开发', sort: 1, icon: '', description: 'Java/Spring', status: 'NORMAL' },
  { channelId: 9, parentId: 3, name: '前端开发', sort: 2, icon: '', description: 'Vue/React', status: 'NORMAL' }
]

export const dbTag: Tag[] = [
  { tagId: 1, name: 'Java', color: '#f89820', refCount: 12 },
  { tagId: 2, name: 'Spring Boot', color: '#6db33f', refCount: 9 },
  { tagId: 3, name: 'Vue', color: '#42b883', refCount: 8 },
  { tagId: 4, name: 'MySQL', color: '#00758f', refCount: 6 },
  { tagId: 5, name: '求职', color: '#f77f00', refCount: 4 },
  { tagId: 6, name: '校园活动', color: '#e91e63', refCount: 3 },
  { tagId: 7, name: '期末复习', color: '#9c27b0', refCount: 5 },
  { tagId: 8, name: '面试', color: '#ff5722', refCount: 2 },
  { tagId: 9, name: '工具推荐', color: '#00bcd4', refCount: 3 },
  { tagId: 10, name: '生活分享', color: '#8bc34a', refCount: 7 }
]

const sampleMd = (title: string) => `## ${title}\n\n这是一篇示例帖子，正文使用 **Markdown** 编写。\n\n### 要点\n- 第一条\n- 第二条\n\n\`\`\`java\npublic class Hello {\n  public static void main(String[] args) {\n    System.out.println("Hi");\n  }\n}\n\`\`\`\n\n> 欢迎在评论区交流。`

export const dbPost: Post[] = [
  { postId: 1, authorId: 3, authorNickname: '小明', authorAvatar: '', channelId: 8, channelName: '后端开发', title: 'Spring Boot 3 启动报错排查指南', content: sampleMd('排查思路'), viewCount: 320, likeCount: 24, commentCount: 5, collectCount: 8, isTop: 1, isEssence: 1, status: 'NORMAL', tags: [{ tagId: 1, name: 'Java', color: '#f89820' }, { tagId: 2, name: 'Spring Boot', color: '#6db33f' }], createTime: '2026-06-25 14:20:00', updateTime: '2026-06-25 14:20:00' },
  { postId: 2, authorId: 4, authorNickname: '小红', authorAvatar: '', channelId: 9, channelName: '前端开发', title: 'Vue3 组合式 API 上手心得', content: sampleMd('Vue3 心得'), viewCount: 180, likeCount: 18, commentCount: 3, collectCount: 5, isTop: 0, isEssence: 0, status: 'NORMAL', tags: [{ tagId: 3, name: 'Vue', color: '#42b883' }], createTime: '2026-06-25 16:00:00', updateTime: '2026-06-25 16:00:00' },
  { postId: 3, authorId: 2, authorNickname: '王老师', authorAvatar: '', channelId: 5, channelName: '答疑解惑', title: '关于期末 Java 大作业的几个常见问题', content: sampleMd('常见问题'), viewCount: 540, likeCount: 35, commentCount: 8, collectCount: 20, isTop: 1, isEssence: 1, status: 'NORMAL', tags: [{ tagId: 1, name: 'Java', color: '#f89820' }, { tagId: 7, name: '期末复习', color: '#9c27b0' }], createTime: '2026-06-24 09:30:00', updateTime: '2026-06-24 09:30:00' },
  { postId: 4, authorId: 3, authorNickname: '小明', authorAvatar: '', channelId: 2, channelName: '校园生活', title: '校运会志愿者招募啦~', content: sampleMd('招募信息'), viewCount: 96, likeCount: 12, commentCount: 2, collectCount: 1, isTop: 0, isEssence: 0, status: 'NORMAL', tags: [{ tagId: 6, name: '校园活动', color: '#e91e63' }, { tagId: 10, name: '生活分享', color: '#8bc34a' }], createTime: '2026-06-26 08:10:00', updateTime: '2026-06-26 08:10:00' },
  { postId: 5, authorId: 4, authorNickname: '小红', authorAvatar: '', channelId: 4, channelName: '求职招聘', title: '秋招面试总结：Java 后端八股整理', content: sampleMd('八股整理'), viewCount: 720, likeCount: 60, commentCount: 12, collectCount: 45, isTop: 0, isEssence: 1, status: 'NORMAL', tags: [{ tagId: 5, name: '求职', color: '#f77f00' }, { tagId: 8, name: '面试', color: '#ff5722' }], createTime: '2026-06-23 20:00:00', updateTime: '2026-06-23 20:00:00' },
  { postId: 6, authorId: 3, authorNickname: '小明', authorAvatar: '', channelId: 1, channelName: '学习交流', title: '推荐几个超好用的开发工具', content: sampleMd('工具推荐'), viewCount: 210, likeCount: 20, commentCount: 4, collectCount: 11, isTop: 0, isEssence: 0, status: 'NORMAL', tags: [{ tagId: 9, name: '工具推荐', color: '#00bcd4' }], createTime: '2026-06-22 18:30:00', updateTime: '2026-06-22 18:30:00' },
  { postId: 7, authorId: 2, authorNickname: '王老师', authorAvatar: '', channelId: 1, channelName: '学习交流', title: '数据结构复习：红黑树到底考不考？', content: sampleMd('红黑树探讨'), viewCount: 330, likeCount: 28, commentCount: 6, collectCount: 14, isTop: 0, isEssence: 0, status: 'NORMAL', tags: [{ tagId: 7, name: '期末复习', color: '#9c27b0' }], createTime: '2026-06-21 10:00:00', updateTime: '2026-06-21 10:00:00' }
]

export const dbComment: Comment[] = [
  { commentId: 1, postId: 1, parentId: null, rootId: null, authorId: 4, authorNickname: '小红', authorAvatar: '', authorRole: 'STUDENT', content: '写得很清晰，谢谢分享！', likeCount: 5, floor: 1, createTime: '2026-06-25 15:00:00' },
  { commentId: 2, postId: 1, parentId: 1, rootId: 1, authorId: 3, authorNickname: '小明', authorAvatar: '', authorRole: 'STUDENT', content: '不客气～有问题继续交流', likeCount: 2, floor: 2, createTime: '2026-06-25 15:10:00' },
  { commentId: 3, postId: 1, parentId: 1, rootId: 1, authorId: 2, authorNickname: '王老师', authorAvatar: '', authorRole: 'TEACHER', content: '补充一点：记得检查依赖版本。', likeCount: 8, floor: 3, createTime: '2026-06-25 16:00:00' },
  { commentId: 4, postId: 1, parentId: null, rootId: null, authorId: 2, authorNickname: '王老师', authorAvatar: '', authorRole: 'TEACHER', content: '官方答复：启动类建议放在根包。', likeCount: 10, floor: 4, createTime: '2026-06-25 16:30:00' }
]

export const dbNotification: Notification[] = [
  { notifyId: 1, receiverId: 3, senderId: 4, type: 'COMMENT', targetType: 'POST', targetId: 1, content: '评论了你的帖子《Spring Boot 3 启动报错排查指南》', isRead: 0, createTime: '2026-06-25 15:00:00', senderNickname: '小红' },
  { notifyId: 2, receiverId: 3, senderId: 2, type: 'REPLY', targetType: 'COMMENT', targetId: 1, content: '回复了你的评论', isRead: 0, createTime: '2026-06-25 16:00:00', senderNickname: '王老师' }
]

export const dbReport: Report[] = [
  { reportId: 1, reporterId: 4, targetType: 'POST', targetId: 2, reasonType: '广告/灌水', reasonText: '疑似广告', status: 'PENDING', handlerId: null, handleRemark: null, createTime: '2026-06-25 17:00:00', handleTime: null, reporterNickname: '小红', targetTitle: 'Vue3 组合式 API 上手心得' }
]

export const dbcollect: { collectId: number; userId: number; postId: number }[] = [
  { collectId: 1, userId: 3, postId: 5 },
  { collectId: 2, userId: 3, postId: 7 }
]

export const dbLike: { likeId: number; userId: number; targetType: string; targetId: number }[] = [
  { likeId: 1, userId: 3, targetType: 'POST', targetId: 5 },
  { likeId: 2, userId: 4, targetType: 'POST', targetId: 1 },
  { likeId: 3, userId: 4, targetType: 'COMMENT', targetId: 3 }
]

export const dbHot: Record<number, number> = {
  5: 720 * 1 + 60 * 3 + 12 * 2,
  3: 540 + 35 * 3 + 8 * 2,
  1: 320 + 24 * 3 + 5 * 2,
  7: 330 + 28 * 3 + 6 * 2,
  6: 210 + 20 * 3 + 4 * 2
}

export const dbPassword: Record<number, string> = {
  1: 'admin123', 2: 'teacher123', 3: 'stu123', 4: 'stu123'
}

let seqId = 1000
export const nextId = () => ++seqId