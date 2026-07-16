import { beforeEach, describe, expect, it, vi } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'
import { api } from '../api'
import { useAuthStore } from '@/stores/auth'

describe('api login requests', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.resetAllMocks()
    vi.unstubAllGlobals()

    const storage = new Map<string, string>()
    vi.stubGlobal('localStorage', {
      getItem: vi.fn((key: string) => storage.get(key) ?? null),
      setItem: vi.fn((key: string, value: string) => {
        storage.set(key, value)
      }),
      removeItem: vi.fn((key: string) => {
        storage.delete(key)
      }),
      clear: vi.fn(() => {
        storage.clear()
      }),
      key: vi.fn((index: number) => Array.from(storage.keys())[index] ?? null),
      get length() {
        return storage.size
      },
    })
  })

  it('does not block login requests when there is no stored session', async () => {
    const authStore = useAuthStore()
    expect(authStore.token).toBeNull()

    const fetchMock = vi.fn().mockResolvedValue({
      ok: true,
      status: 200,
      json: async () => ({ id: '1', name: 'Ana', email: 'ana@email.com', token: 'abc123' }),
    })

    vi.stubGlobal('fetch', fetchMock)

    await expect(api.post('/auth/login', { email: 'ana@email.com', password: '123456' })).resolves.toEqual({
      id: '1',
      name: 'Ana',
      email: 'ana@email.com',
      token: 'abc123',
    })

    expect(fetchMock).toHaveBeenCalledTimes(1)
  })
})
