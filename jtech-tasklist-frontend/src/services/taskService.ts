import { api } from './api'
import type { TaskGroupResponse, TaskRequest, TaskResponse } from '@/types/task'

export const taskService = {
  getTaskGroups(token: string | undefined) {
    return api.get<TaskGroupResponse[]>('/task-groups', token)
  },
  getAllTasksByGroup(groupId: string, token: string | undefined) {
    return api.get<TaskResponse[]>(`/task-groups/${groupId}`, token)
  },
  createTaskGroup(name: string, description: string, token: string | undefined) {
    return api.post<TaskGroupResponse>('/task-groups', { name, description }, token)
  },
  updateTaskGroup(id: string, name: string, token: string | undefined) {
    return api.put<TaskGroupResponse>(`/task-groups/${id}`, { name }, token)
  },
  deleteTaskGroup(id: string, token: string | undefined) {
    return api.delete<void>(`/task-groups/${id}`, token)
  },
  getTasks(groupId: string, token: string | undefined) {
    return api.get<TaskResponse[]>(`/task-groups/${groupId}`, token)
  },
  createTask(data: TaskRequest, token: string | undefined) {
    return api.post<TaskResponse>(`/tasks`, data, token)
  },
  updateTask(groupId: string, taskId: string, data: TaskRequest, token: string | undefined) {
    return api.put<TaskResponse>(`/task-groups/${groupId}/tasks/${taskId}`, data, token)
  },
  deleteTask(groupId: string, taskId: string, token: string | undefined) {
    return api.delete<void>(`/task-groups/${groupId}/tasks/${taskId}`, token)
  },
}