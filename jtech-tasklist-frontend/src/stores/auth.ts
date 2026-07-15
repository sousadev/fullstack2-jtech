import { ref, computed } from 'vue'
import { defineStore } from 'pinia'
import { authService } from '@/services/authService'

const AUTH_TOKEN_KEY = 'jtech-auth-token'
const AUTH_USER_KEY = 'jtech-auth-user'

type AuthUser = { id: string; name: string; email: string }

function getStorage() {
  if (typeof window === 'undefined') return null
  return window.localStorage
}

function readStoredUser(): AuthUser | null {
  const storage = getStorage()
  if (!storage) return null

  const storedUser = storage.getItem(AUTH_USER_KEY)
  if (!storedUser) return null

  try {
    return JSON.parse(storedUser) as AuthUser
  } catch {
    storage.removeItem(AUTH_USER_KEY)
    return null
  }
}

export const useAuthStore = defineStore('auth', () => {
  const token = ref<string | null>(getStorage()?.getItem(AUTH_TOKEN_KEY) ?? null)
  const user = ref<AuthUser | null>(readStoredUser())

  const isAuthenticated = computed(() => !!token.value)

  function persistSession() {
    const storage = getStorage()
    if (!storage) return

    if (token.value && user.value) {
      storage.setItem(AUTH_TOKEN_KEY, token.value)
      storage.setItem(AUTH_USER_KEY, JSON.stringify(user.value))
      return
    }

    storage.removeItem(AUTH_TOKEN_KEY)
    storage.removeItem(AUTH_USER_KEY)
  }

  async function login(email: string, password: string) {
    const response = await authService.login({ email, password })
    token.value = response.token
    user.value = {
      id: response.id,
      name: response.name,
      email: response.email,
    }
    persistSession()
    return response
  }

  function logout() {
    token.value = null
    user.value = null
    persistSession()
  }

  return { token, user, isAuthenticated, login, logout }
})
