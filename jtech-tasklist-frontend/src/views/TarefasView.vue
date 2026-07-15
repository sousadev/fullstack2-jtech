<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { useRouter } from 'vue-router'
import { api } from '@/services/api'
import type { TaskGroupResponse } from '@/types/taskGroup'
import type { TaskResponse } from '@/types/task'
import '@/styles/Tarefas.css'
import { taskService } from '@/services/taskService'

const authStore = useAuthStore()
const router = useRouter()

const summary = [
  { label: 'Pendentes', value: '6', accent: 'orange' },
  { label: 'Concluídas', value: '12', accent: 'gold' },
  { label: 'Hoje', value: '3', accent: 'cream' },
]

const taskGroups = ref<TaskGroupResponse[]>([])
const selectedGroupId = ref<string | null>(null)
const tasks = ref<TaskResponse[]>([])
const isLoading = ref(false)
const isLoadingTasks = ref(false)
const error = ref('')
const isCreateGroupModalOpen = ref(false)
const isCreatingGroup = ref(false)
const createGroupError = ref('')
const newGroup = ref({ name: '', description: '' })

const selectedGroup = computed(() =>
  taskGroups.value.find((group) => group.id === selectedGroupId.value) ?? null,
)

async function loadTaskGroups() {
  if (!authStore.token) {
    window.location.href = '/login'
    return
  }

  isLoading.value = true
  error.value = ''

  try {
    const response = await taskService.getTaskGroups(authStore.token)
    taskGroups.value = response
  } catch {
    error.value = 'Não foi possível carregar os grupos de tarefas.'
  } finally {
    isLoading.value = false
  }
}

async function loadTasks(groupId: string) {
  window.location.href = `/ver-tarefas/${groupId}`
  if (!authStore.token) return

  selectedGroupId.value = groupId
  isLoadingTasks.value = true

  try {
    const response = await taskService.getTasks(groupId, authStore.token)
    tasks.value = response
  } catch {
    tasks.value = []
    error.value = 'Não foi possível carregar as tarefas do grupo.'
  } finally {
    isLoadingTasks.value = false
  }
}

function openCreateGroupModal() {
  createGroupError.value = ''
  newGroup.value = { name: '', description: '' }
  isCreateGroupModalOpen.value = true
}

function closeCreateGroupModal() {
  isCreateGroupModalOpen.value = false
  createGroupError.value = ''
  newGroup.value = { name: '', description: '' }
}

async function handleCreateGroup() {
  if (!authStore.token) return

  const name = newGroup.value.name.trim()
  const description = newGroup.value.description.trim()

  if (!name) {
    createGroupError.value = 'Informe o nome do grupo.'
    return
  }

  isCreatingGroup.value = true
  createGroupError.value = ''

  try {
    const createdGroup = await taskService.createTaskGroup(name, description, authStore.token)
    taskGroups.value = [createdGroup, ...taskGroups.value]
    closeCreateGroupModal()
    selectedGroupId.value = null
    tasks.value = []
  } catch {
    createGroupError.value = 'Não foi possível criar o grupo.'
  } finally {
    isCreatingGroup.value = false
  }
}

function verifyAuthentication() {
  if (!authStore.token) {
    router.push({ name: 'login' })
  }
}

onMounted(() => {
  loadTaskGroups()
  verifyAuthentication()
})

function handleLogout() {
  authStore.logout()
  router.push({ name: 'login' })
}
</script>

<template>
  <main class="tarefas-page">
    <section class="tarefas-panel">
      <header class="tarefas-header">
        <div>
          <p class="eyebrow">Painel do dia</p>
          <h1>Tarefas</h1>
          <p v-if="authStore.user" class="subtitle">Olá, {{ authStore.user.name }}. Aqui estão os seus próximos compromissos.</p>
        </div>
        <button type="button" class="logout" @click="handleLogout">Sair</button>
      </header>

      <div class="hero-card">
        <div>
          <p class="hero-label">Resumo da semana</p>
          <h2>Você está no caminho certo!</h2>
          <p>Continue mantendo o foco e finalize as tarefas mais importantes hoje.</p>
        </div>
        <button type="button" class="primary-action">+ Nova tarefa</button>
      </div>

      <div class="summary-grid">
        <article v-for="item in summary" :key="item.label" class="summary-card" :class="item.accent">
          <span>{{ item.label }}</span>
          <strong>{{ item.value }}</strong>
        </article>
      </div>

      <section class="tasks-section">
        <div class="section-title">
          <h3>Grupos de tarefas</h3>
          <button type="button" class="link-button" @click="openCreateGroupModal">Novo Grupo</button>
        </div>

        <p v-if="error" class="feedback-error">{{ error }}</p>

        <div v-if="isLoading" class="loading-state">Carregando grupos...</div>

        <div v-else class="groups-grid">
          <button
            v-for="group in taskGroups"
            :key="group.id"
            type="button"
            class="task-group"
            @click="loadTasks(group.id)"
          >
            <div class="group-header">
              <h4>{{ group.name }}</h4>
              <p>{{ group.description || 'Sem descrição' }}</p>
            </div>

            <div class="group-meta">
              <span>{{ group.tasks?.length ?? 0 }} tarefas</span>
              <span class="group-action">Ver tarefas</span>
            </div>
          </button>
        </div>

        <div v-if="selectedGroup" class="task-details">
          <div class="section-title compact">
            <h3>{{ selectedGroup.name }}</h3>
            <span>{{ tasks.length }} itens</span>
          </div>

          <div v-if="isLoadingTasks" class="loading-state">Carregando tarefas...</div>
          <ul v-else-if="tasks.length" class="task-list">
            <li v-for="task in tasks" :key="task.id" class="task-item">
              <div>
                <h5>{{ task.name }}</h5>
                <p>{{ task.description || 'Sem descrição' }}</p>
              </div>
            </li>
          </ul>
          <p v-else class="empty-state">Nenhuma tarefa cadastrada neste grupo.</p>
        </div>
      </section>
    </section>

    <div v-if="isCreateGroupModalOpen" class="modal-overlay" @click.self="closeCreateGroupModal">
      <div class="modal-card">
        <div class="modal-header">
          <h3>Novo grupo de tarefas</h3>
          <button type="button" class="close-button" @click="closeCreateGroupModal">×</button>
        </div>

        <form class="modal-form" @submit.prevent="handleCreateGroup">
          <div class="form-field">
            <label for="group-name">Nome</label>
            <input id="group-name" v-model="newGroup.name" type="text" placeholder="Ex.: Atividades domésticas" />
          </div>

          <div class="form-field">
            <label for="group-description">Descrição</label>
            <textarea id="group-description" v-model="newGroup.description" rows="3" placeholder="Descreva este grupo..."></textarea>
          </div>

          <p v-if="createGroupError" class="modal-error">{{ createGroupError }}</p>

          <div class="modal-actions">
            <button type="button" class="cancel-button" @click="closeCreateGroupModal">Cancelar</button>
            <button type="submit" class="submit-button" :disabled="isCreatingGroup">
              {{ isCreatingGroup ? 'Criando...' : 'Criar grupo' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </main>
</template>