<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { taskService } from '@/services/taskService'
import type { TaskResponse } from '@/types/task'
import '@/styles/Tarefas.css'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const taskGroupId = computed(() => route.params.id as string)
const groupName = ref('')
const tasks = ref<TaskResponse[]>([])
const isLoading = ref(false)
const error = ref('')
const draggedTaskId = ref<string | null>(null)

const columns = [
  { key: 'todo', title: 'A fazer', subtitle: 'Pendentes' },
  { key: 'doing', title: 'Em andamento', subtitle: 'Em progresso' },
  { key: 'done', title: 'Concluído', subtitle: 'Finalizadas' },
]

const tasksByColumn = computed(() => ({
  todo: tasks.value.filter((task) => !task.active),
  doing: tasks.value.filter((task) => task.active),
  done: [],
}))

async function loadTasks() {
  if (!authStore.token) {
    router.push({ name: 'login' })
    return
  }

  isLoading.value = true
  error.value = ''

  try {
    const response = await taskService.getTasks(taskGroupId.value, authStore.token)
    tasks.value = response
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

onMounted(() => {
  loadTasks()
})
</script>

<template>
  <main class="tarefas-page">
    <section class="tarefas-panel kanban-panel">
      <header class="tarefas-header">
        <div>
          <p class="eyebrow">Visualização de tarefas</p>
          <h1>{{ groupName }}</h1>
          <p class="subtitle">Arraste as tarefas entre os quadros para organizar o fluxo.</p>
        </div>
        <button type="button" class="logout" @click="goBack">Voltar</button>
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
</template>