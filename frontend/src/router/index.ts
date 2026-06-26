import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    component: () => import('@/layouts/BasicLayout.vue'),
    children: [
      { path: '', name: 'home', component: () => import('@/views/public/Home.vue') },
      { path: 'channel/:id', name: 'channel', component: () => import('@/views/public/ChannelView.vue'), props: true },
      { path: 'tag/:id', name: 'tag', component: () => import('@/views/public/TagView.vue'), props: true },
      { path: 'search', name: 'search', component: () => import('@/views/public/SearchView.vue') },
      { path: 'hot', name: 'hot', component: () => import('@/views/public/HotView.vue') },
      { path: 'post/:id', name: 'post', component: () => import('@/views/public/PostDetail.vue'), props: true },
      { path: 'post/new', name: 'post-new', component: () => import('@/views/public/PostEdit.vue'), meta: { requiresAuth: true, roles: ['STUDENT', 'TEACHER'] } },
      { path: 'post/edit/:id', name: 'post-edit', component: () => import('@/views/public/PostEdit.vue'), props: true, meta: { requiresAuth: true, roles: ['STUDENT', 'TEACHER'] } },
      { path: 'user/:id', name: 'user', component: () => import('@/views/user/ProfileView.vue'), props: true },
      { path: 'profile', name: 'profile-edit', component: () => import('@/views/user/EditProfile.vue'), meta: { requiresAuth: true } },
      { path: 'my/posts', name: 'my-posts', component: () => import('@/views/user/MyPosts.vue'), meta: { requiresAuth: true, roles: ['STUDENT', 'TEACHER'] } },
      { path: 'my/collects', name: 'my-collects', component: () => import('@/views/user/MyCollects.vue'), meta: { requiresAuth: true } },
      { path: 'notifications', name: 'notifications', component: () => import('@/views/user/Notifications.vue'), meta: { requiresAuth: true } }
    ]
  },
  { path: '/login', name: 'login', component: () => import('@/views/public/Login.vue') },
  { path: '/register', name: 'register', component: () => import('@/views/public/Register.vue') },
  {
    path: '/admin',
    component: () => import('@/layouts/AdminLayout.vue'),
    meta: { requiresAuth: true, roles: ['ADMIN'] },
    redirect: '/admin/users',
    children: [
      { path: 'users', name: 'admin-users', component: () => import('@/views/admin/UsersMgr.vue') },
      { path: 'channels', name: 'admin-channels', component: () => import('@/views/admin/ChannelsMgr.vue') },
      { path: 'tags', name: 'admin-tags', component: () => import('@/views/admin/TagsMgr.vue') },
      { path: 'reports', name: 'admin-reports', component: () => import('@/views/admin/ReportsMgr.vue') },
      { path: 'posts', name: 'admin-posts', component: () => import('@/views/admin/PostsMgr.vue') }
    ]
  },
  { path: '/:pathMatch(.*)*', component: () => import('@/views/public/NotFound.vue') }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior() { return { top: 0 } }
})

router.beforeEach((to, _from, next) => {
  const userStore = useUserStore()
  if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    next({ name: 'login', query: { redirect: to.fullPath } })
    return
  }
  const roles = to.meta.roles as string[] | undefined
  if (roles && roles.length && (!userStore.user || !roles.includes(userStore.user.role))) {
    next('/'); return
  }
  next()
})

export default router