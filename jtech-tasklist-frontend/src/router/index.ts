import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const LoginView = () => import('../views/LoginView.vue')
const CadastroView = () => import('../views/CadastroView.vue')
const TarefasView = () => import('../views/TarefasView.vue')
const VisualizarTarefaView = () => import('../views/VisualizarTarefaView.vue')

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      redirect: () => {
        const authStore = useAuthStore()
        return authStore.isAuthenticated ? { name: 'tarefas' } : { name: 'login' }
      },
    },
    {
      path: '/login',
      name: 'login',
      component: LoginView,
    },
    {
      path: '/cadastrar',
      name: 'cadastro',
      component: CadastroView,
    },
    {
      path: '/tarefas',
      name: 'tarefas',
      component: TarefasView,
      meta: { requiresAuth: true },
    },
    {
      path: '/ver-tarefas/:id',
      name: 'visualizar-tarefa',
      component: VisualizarTarefaView,
      meta: { requiresAuth: true },
    },
    {
      path: '/about',
      name: 'about',
      component: () => import('../views/AboutView.vue'),
    },
  ],
})

router.beforeEach((to) => {
  const authStore = useAuthStore()

  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    return { name: 'login' }
  }

  if (to.name === 'login' && authStore.isAuthenticated) {
    return { name: 'tarefas' }
  }
})

export default router
