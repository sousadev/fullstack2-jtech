import { useAuthStore } from '@/stores/auth'

const BASE_URL = import.meta.env.VITE_API_URL

class ApiError extends Error {
  constructor(
    public status: number,
    message: string,
  ) {
    super(message)
  }
}

async function request<T>(
  path: string,
  options: RequestInit = {},
  token?: string,
): Promise<T> {
  const authStore = useAuthStore()
  const isAuthRequest = path === '/auth/login' || path === '/auth/register'

  if (!isAuthRequest && !authStore.ensureActiveSession()) {
    throw new Error('Sessão expirada')
  }

  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
  }

  if (token) {
    headers['Authorization'] = `Bearer ${token}`
  }

  const response = await fetch(`${BASE_URL}${path}`, {
    ...options,
    headers: {
      ...headers,
      ...(options.headers as Record<string, string> | undefined),
    },
  })

  if (!response.ok) {
    const message = await response.text()

    if (response.status === 401) {
      authStore.logout()
      throw new ApiError(response.status, 'Sessão expirada')
    }

    throw new ApiError(response.status, message || response.statusText)
  }

  if (response.status === 204) return undefined as T
  return response.json()
}

export const api = {
  get: <T>(path: string, token?: string) =>
    request<T>(path, { method: 'GET' }, token),

  post: <T>(path: string, body: unknown, token?: string) =>
    request<T>(path, { method: 'POST', body: JSON.stringify(body) }, token),

  put: <T>(path: string, body: unknown, token?: string) =>
    request<T>(path, { method: 'PUT', body: JSON.stringify(body) }, token),

  delete: <T>(path: string, token?: string) =>
    request<T>(path, { method: 'DELETE' }, token),
}