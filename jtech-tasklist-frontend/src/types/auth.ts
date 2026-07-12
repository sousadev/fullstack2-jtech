import type { TaskGroupResponse } from "./taskGroup"

export interface LoginRequest {
  email: string
  password: string
}

export interface AuthResponse {
  id: string
  email: string
  name: string
  token: string
  createdAt: string
}

export interface RegisterRequest {
  email: string
  password: string
  name: string
  taskGroups: TaskGroupResponse[]
}