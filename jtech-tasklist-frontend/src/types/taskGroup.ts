import type { TaskResponse } from "./task"

export interface TaskGroupResponse {
  id: string
  name: string
  description: string
  user_id: string
  tasks: TaskResponse[]
  active: boolean
  created_at: string
}