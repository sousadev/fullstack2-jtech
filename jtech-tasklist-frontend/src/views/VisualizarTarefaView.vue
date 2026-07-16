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
  status: 'todo' | 'doing' | 'done' | 'cancelled'
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
  status: 'todo',
})
const statusOptions = [
  { value: 'todo', label: 'A fazer' },
  { value: 'doing', label: 'Em andamento' },
  { value: 'done', label: 'Concluído' },
  { value: 'cancelled', label: 'Cancelado' },
]

const columns = [
  { key: 'todo', title: 'A fazer', subtitle: 'Pendentes' },
  { key: 'doing', title: 'Em andamento', subtitle: 'Em progresso' },
  { key: 'done', title: 'Concluído', subtitle: 'Finalizadas' },
  { key: 'cancelled', title: 'Cancelado', subtitle: 'Canceladas' },
]

const tasksByColumn = computed(() => ({
  todo: tasks.value.filter((task) => !task.active),
  doing: tasks.value.filter((task) => task.active),
  done: [],
  cancelled: [],
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
    tasks.value = response.tasks
    // const groups = await taskService.getTaskGroups(authStore.token)
    // const selectedGroup = groups.find((item) => item.id === taskGroupId.value)
    // groupName.value = selectedGroup?.name ?? 'Grupo de tarefas'
  } catch {
    error.value = 'Não foi possível carregar as tarefas deste grupo.'
  } finally {
    isLoading.value = false
  }
}

function onDragStart(taskId: string) {
  draggedTaskId.value = taskId
}

function onDrop(columnKey: string) {
  if (!draggedTaskId.value) return

  const task = tasks.value.find((item) => item.id === draggedTaskId.value)
  if (!task) return

  if (columnKey === 'done') {
    task.active = false
  } else if (columnKey === 'doing') {
    task.active = true
  } else {
    task.active = false
  }

  draggedTaskId.value = null
}

function goBack() {
  router.push({ name: 'tarefas' })
}

function openCreateTaskModal() {
  newTaskForm.value = {
    group_id: taskGroupId.value,
    name: '',
    description: '',
    status: 'todo',
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
    const taskData = {
      group_id: taskGroupId.value,
      name,
      description,
      status: null,
      group_id: taskGroupId.value
    }
    await taskService.createTask(
      taskData,
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
          <label for="task-group-id">Grupo</label>
          <input id="task-group-id" v-model="newTaskForm.group_id" type="text" readonly />
        </div>

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