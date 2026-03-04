import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', redirect: '/tasks' },
    { path: '/login', component: () => import('@/views/LoginView.vue') },
    { path: '/submit', component: () => import('@/views/SubmitView.vue'), meta: { requiresAuth: true } },
    { path: '/tasks', component: () => import('@/views/TasksView.vue'), meta: { requiresAuth: true } },
    { path: '/stats', component: () => import('@/views/StatsView.vue'), meta: { requiresAuth: true } },
  ],
})

router.beforeEach((to) => {
  const userStore = useUserStore()
  if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    return '/login'
  }
})

export default router
