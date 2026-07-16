<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { taskService } from '@/services/taskService'
import type { TaskResponse } from '@/types/task'
import '@/styles/Tarefas.css'

interface TaskFormState {
  group_id: string
  name: string
  description: string
  status: 'PENDING' | 'IN_PROGRESS' | 'COMPLETED' | 'CANCELLED'
}

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const taskGroupId = computed(() => route.params.id as string)
const groupName = ref('')
const tasks = ref<TaskResponse[]>([])
const isLoading = ref(false)
const error = ref('')
const draggedTaskId = ref<string | null>(null)
const isCreateTaskModalOpen = ref(false)
const isCreatingTask = ref(false)
const createTaskError = ref('')
const newTaskForm = ref<TaskFormState>({
  group_id: '',
  name: '',
  description: '',
  status: 'PENDING',
})
const statusOptions = [
  { value: 'PENDING', label: 'A fazer' },
  { value: 'IN_PROGRESS', label: 'Em andamento' },
  { value: 'COMPLETED', label: 'Concluído' },
  { value: 'CANCELLED', label: 'Cancelado' },
]

const columns = [
  { key: 'PENDING', title: 'A fazer', subtitle: 'Pendentes' },
  { key: 'IN_PROGRESS', title: 'Em andamento', subtitle: 'Em progresso' },
  { key: 'COMPLETED', title: 'Concluído', subtitle: 'Finalizadas' },
  { key: 'CANCELLED', title: 'Cancelado', subtitle: 'Canceladas' },
]

function normalizeTaskStatus(task: TaskResponse) {
  const rawStatus = task.status ?? (task as TaskResponse & { current_status?: string }).current_status ?? (task as TaskResponse & { state?: string }).state
  const normalized = typeof rawStatus === 'string' ? rawStatus.trim().toLowerCase() : ''

  if (['pending', 'to-do', 'to_do', 'pending', 'backlog', 'new'].includes(normalized)) {
    return 'PENDING'
  }

  if (['doing', 'in_progress', 'in-progress', 'in progress', 'active', 'working'].includes(normalized)) {
    return 'IN_PROGRESS'
  }

  if (['done', 'completed', 'finish', 'finished'].includes(normalized)) {
    return 'COMPLETED'
  }

  if (['cancelled', 'canceled', 'cancel', 'blocked', 'rejected'].includes(normalized)) {
    return 'CANCELLED'
  }

  return task.active ? 'IN_PROGRESS' : 'PENDING'
}

const tasksByColumn = computed(() => ({
  PENDING: tasks.value.filter((task) => normalizeTaskStatus(task) === 'PENDING'),
  IN_PROGRESS: tasks.value.filter((task) => normalizeTaskStatus(task) === 'IN_PROGRESS'),
  COMPLETED: tasks.value.filter((task) => normalizeTaskStatus(task) === 'COMPLETED'),
  CANCELLED: tasks.value.filter((task) => normalizeTaskStatus(task) === 'CANCELLED'),
}))

async function loadTasks() {
  if (!authStore.token) {
    router.push({ name: 'login' })
    return
  }

  isLoading.value = true
  error.value = ''

  try {
    const response = await taskService.getAllTasksByGroup(taskGroupId.value, authStore.token)
    const payload = response as unknown

    let nextTasks: TaskResponse[] = []

    if (Array.isArray(payload)) {
      nextTasks = payload as TaskResponse[]
    } else if (payload && typeof payload === 'object') {
      const record = payload as Record<string, unknown>

      if (Array.isArray(record.tasks)) {
        nextTasks = record.tasks as TaskResponse[]
      } else if (Array.isArray((record.data as unknown[] | undefined))) {
        nextTasks = record.data as TaskResponse[]
      } else if (Array.isArray((record.result as unknown[] | undefined))) {
        nextTasks = record.result as TaskResponse[]
      }
    }

    tasks.value = nextTasks

    const groups = await taskService.getTaskGroups(authStore.token)
    const selectedGroup = groups.find((item) => item.id === taskGroupId.value)
    groupName.value = selectedGroup?.name ?? 'Grupo de tarefas'
  } catch {
    error.value = 'Não foi possível carregar as tarefas deste grupo.'
  } finally {
    isLoading.value = false
  }
}

function onDragStart(taskId: string) {
  draggedTaskId.value = taskId
}

async function onDrop(columnKey: string) {
  if (!draggedTaskId.value) return

  const task = tasks.value.find((item) => item.id === draggedTaskId.value)
  if (!task) return

  const nextStatus = columnKey as TaskResponse['status']
  const nextActive = columnKey === 'doing'

  const dataTask = {
    id: task.id,
    name: task.name,
    description: task.description,
    group_id: taskGroupId.value,
    status: nextStatus
  }
  try {
    await taskService.updateTask(
      dataTask,
      authStore.token,
    )

    await loadTasks()
  } catch {
    error.value = 'Não foi possível atualizar o status da tarefa.'
  } finally {
    draggedTaskId.value = null
  }
}

function goBack() {
  router.push({ name: 'tarefas' })
}

function openCreateTaskModal() {
  newTaskForm.value = {
    group_id: taskGroupId.value,
    name: '',
    description: '',
    status: 'PENDING',
  }
  createTaskError.value = ''
  isCreateTaskModalOpen.value = true
}

function closeCreateTaskModal() {
  isCreateTaskModalOpen.value = false
  createTaskError.value = ''
}

async function handleCreateTask() {
  if (!authStore.token) {
    router.push({ name: 'login' })
    return
  }

  const name = newTaskForm.value.name.trim()
  const description = newTaskForm.value.description.trim()

  if (!name) {
    createTaskError.value = 'Informe o nome da tarefa.'
    return
  }

  isCreatingTask.value = true
  createTaskError.value = ''

  try {
    await taskService.createTask(
      {
        name,
        description,
        active: newTaskForm.value.status === 'doing',
        status: newTaskForm.value.status,
        group_id: taskGroupId.value,
      },
      authStore.token,
    )

    closeCreateTaskModal()
    await loadTasks()
  } catch {
    createTaskError.value = 'Não foi possível criar a tarefa.'
  } finally {
    isCreatingTask.value = false
  }
}

onMounted(() => {
  loadTasks()
})
</script>

<template>
  <main class="tarefas-page">
    <section class="tarefas-panel kanban-panel">
      <header class="tarefas-header kanban-header">
        <div>
          <p class="eyebrow">Quadro de tarefas</p>
          <h1>{{ groupName }}</h1>
          <p class="subtitle">Arraste as tarefas entre os quadros para organizar o fluxo.</p>
        </div>
        <div class="header-actions">
          <button type="button" class="secondary-button" @click="openCreateTaskModal">Nova tarefa</button>
          <button type="button" class="logout" @click="goBack">Voltar</button>
        </div>
      </header>

      <p v-if="error" class="feedback-error">{{ error }}</p>

      <div v-if="isLoading" class="loading-state">Carregando tarefas...</div>

      <div v-else class="kanban-board">
        <section v-for="column in columns" :key="column.key" class="kanban-column" @dragover.prevent @drop="onDrop(column.key)">
          <div class="column-header">
            <div>
              <h3>{{ column.title }}</h3>
              <p>{{ column.subtitle }}</p>
            </div>
          </div>

          <div v-if="tasksByColumn[column.key as keyof typeof tasksByColumn].length" class="column-cards">
            <article
              v-for="task in tasksByColumn[column.key as keyof typeof tasksByColumn]"
              :key="task.id"
              class="kanban-card"
              draggable="true"
              @dragstart="onDragStart(task.id)"
            >
              <h4>{{ task.name }}</h4>
              <p>{{ task.description || 'Sem descrição' }}</p>
            </article>
          </div>
          <p v-else class="empty-state">Nenhuma tarefa aqui.</p>
        </section>
      </div>
    </section>
  </main>

  <div v-if="isCreateTaskModalOpen" class="modal-overlay" @click.self="closeCreateTaskModal">
    <div class="modal-card">
      <div class="modal-header">
        <h3>Criar tarefa</h3>
        <button type="button" class="close-button" @click="closeCreateTaskModal">×</button>
      </div>

      <form class="modal-form" @submit.prevent="handleCreateTask">

        <div class="form-field">
          <label for="task-name">Nome</label>
          <input id="task-name" v-model="newTaskForm.name" type="text" placeholder="Ex.: Comprar pão" />
        </div>

        <div class="form-field">
          <label for="task-description">Descrição</label>
          <textarea id="task-description" v-model="newTaskForm.description" rows="4" placeholder="Descreva a tarefa" />
        </div>

        <div class="form-field">
          <label for="task-status">Status</label>
          <select id="task-status" v-model="newTaskForm.status">
            <option v-for="option in statusOptions" :key="option.value" :value="option.value">
              {{ option.label }}
            </option>
          </select>
        </div>

        <p v-if="createTaskError" class="modal-error">{{ createTaskError }}</p>

        <div class="modal-actions">
          <button type="button" class="cancel-button" @click="closeCreateTaskModal">Cancelar</button>
          <button type="submit" class="submit-button" :disabled="isCreatingTask">
            {{ isCreatingTask ? 'Criando...' : 'Criar tarefa' }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>