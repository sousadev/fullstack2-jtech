import { api } from './api'
import type { AuthResponse, LoginRequest, RegisterRequest } from '@/types/auth'

export const authService = {
  login(data: LoginRequest) {
    return api.post<AuthResponse>('/auth/login', data)
  },
  register(data: RegisterRequest) {
    return api.post<AuthResponse>('/auth/register', data)
  },
}
