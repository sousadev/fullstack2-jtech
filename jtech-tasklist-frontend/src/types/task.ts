export interface TaskResponse {
  id: string
  name: string
  description: string
  user_id: string
  task_group_id: string
  active: boolean
  created_at: string
  updated_at: string
}

export interface TaskGroupResponse {
  id: string
  name: string
  user_id: string
  created_at: string
  updated_at: string
}

export interface TaskRequest {
  name: string
  description: string
  status: string
  group_id: string
}