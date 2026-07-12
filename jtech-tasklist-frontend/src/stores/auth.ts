import { ref, computed } from 'vue'
import { defineStore } from 'pinia'
import { authService } from '@/services/authService'

export const useAuthStore = defineStore('auth', () => {
  const token = ref<string | null>(null)
  const user = ref<{ id: string; name: string; email: string } | null>(null)

  const isAuthenticated = computed(() => !!token.value)

  async function login(email: string, password: string) {
    const response = await authService.login({ email, password })
    token.value = response.token
    user.value = {
      id: response.id,
      name: response.name,
      email: response.email,
    }
    return response
  }

  function logout() {
    token.value = null
    user.value = null
  }

  return { token, user, isAuthenticated, login, logout }
})
