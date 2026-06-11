import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/dashboard'
    },
    {
      path: '/',
      component: () => import('@/layout/MainLayout.vue'),
      children: [
        {
          path: 'dashboard',
          component: () => import('@/views/dashboard/index.vue'),
          meta: { title: '数据看板' }
        },
        {
          path: 'content/song',
          component: () => import('@/views/song/index.vue'),
          meta: { title: '歌曲管理' }
        },
        {
          path: 'content/playlist',
          component: () => import('@/views/playlist/index.vue'),
          meta: { title: '歌单管理' }
        },
        {
          path: 'content/singer',
          component: () => import('@/views/singer/index.vue'),
          meta: { title: '歌手管理' }
        },
        {
          path: 'operation/recommend',
          component: () => import('@/views/recommend/index.vue'),
          meta: { title: '首页推荐' }
        },
        {
          path: 'system/dictionary',
          component: () => import('@/views/dictionary/index.vue'),
          meta: { title: '字典管理' }
        },
        {
          path: 'system/user',
          component: () => import('@/views/user/index.vue'),
          meta: { title: '用户管理' }
        }
      ]
    }
  ]
})

export default router