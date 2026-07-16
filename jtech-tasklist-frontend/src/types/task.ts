export interface TaskResponse {
  id: string
  name: string
  description: string
  user_id: string
  task_group_id: string
  active: boolean
  status?: 'PENDING' | 'IN_PROGRESS' | 'COMPLETED' | 'CANCELLED' | null
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
  id?: string
  name: string
  description: string
  status: 'PENDING' | 'IN_PROGRESS' | 'COMPLETED' | 'CANCELLED'  
  group_id: string
}