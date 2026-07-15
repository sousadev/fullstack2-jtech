import { beforeEach, describe, expect, it, vi } from 'vitest'
import { createPinia, setActivePinia } from 'pinia'
import { useAuthStore } from '../auth'
import { authService } from '@/services/authService'

vi.mock('@/services/authService', () => ({
  authService: {
    login: vi.fn(),
  },
}))

describe('auth store', () => {
  beforeEach(() => {
    const storage = new Map<string, string>()

    Object.defineProperty(window, 'localStorage', {
      value: {
        getItem: (key: string) => storage.get(key) ?? null,
        setItem: (key: string, value: string) => storage.set(key, value),
        removeItem: (key: string) => storage.delete(key),
        clear: () => storage.clear(),
      },
      writable: true,
    })

    setActivePinia(createPinia())
    vi.clearAllMocks()
  })

  it('persists token and user after login', async () => {
    vi.mocked(authService.login).mockResolvedValueOnce({
      id: '1',
      email: 'ana@email.com',
      name: 'Ana',
      token: 'persisted-token',
      createdAt: '2024-01-01T00:00:00.000Z',
    })

    const store = useAuthStore()
    await store.login('ana@email.com', '123456')

    expect(store.token).toBe('persisted-token')
    expect(window.localStorage.getItem('jtech-auth-token')).toBe('persisted-token')
    expect(window.localStorage.getItem('jtech-auth-user')).toContain('ana@email.com')
  })

  it('hydrates the auth state from storage', () => {
    window.localStorage.setItem('jtech-auth-token', 'stored-token')
    window.localStorage.setItem(
      'jtech-auth-user',
      JSON.stringify({ id: '1', name: 'Ana', email: 'ana@email.com' }),
    )

    const store = useAuthStore()

    expect(store.isAuthenticated).toBe(true)
    expect(store.token).toBe('stored-token')
    expect(store.user?.email).toBe('ana@email.com')
  })
})
