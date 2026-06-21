import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '@/views/home/index.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
    },
    {
      path: '/list/:category',
      name: 'list',
      component: () => import('@/views/list/index.vue'),
    },
    {
      path: '/detail/:type/:id',
      name: 'detail',
      component: () => import('@/views/detail/index.vue'),
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/user/login.vue'),
    },
    {
      path: '/profile',
      name: 'profile',
      component: () => import('@/views/user/profile.vue'),
    },
  ],
})

export default router