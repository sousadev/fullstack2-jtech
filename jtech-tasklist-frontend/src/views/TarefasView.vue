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
const editingGroupId = ref<string | null>(null)
const editingGroupName = ref('')
const groupActionError = ref('')
const groupActionLoadingId = ref<string | null>(null)

const selectedGroup = computed(() =>
  taskGroups.value.find((group) => group.id === selectedGroupId.value) ?? null,
)

const allTasks = computed(() => taskGroups.value.flatMap((group) => group.tasks ?? []))

const summary = computed(() => {
  const pendingCount = allTasks.value.filter((task) => {
    const normalized = (task.status ?? '').toString().trim().toLowerCase()
    return normalized === 'pending' || normalized === 'todo' || normalized === 'to-do' || normalized === 'to_do' || (!task.status && !task.active)
  }).length

  const completedCount = allTasks.value.filter((task) => {
    const normalized = (task.status ?? '').toString().trim().toLowerCase()
    return normalized === 'completed' || normalized === 'done' || normalized === 'finished' || normalized === 'complete'
  }).length

  const today = new Date()
  const startOfToday = new Date(today.getFullYear(), today.getMonth(), today.getDate())

  const createdTodayCount = allTasks.value.filter((task) => {
    const createdAt = task.created_at ? new Date(task.created_at) : null
    return createdAt && createdAt >= startOfToday
  }).length

  return [
    { label: 'Pendentes', value: String(pendingCount), accent: 'blue' },
    { label: 'Concluídas', value: String(completedCount), accent: 'indigo' },
    { label: 'Hoje', value: String(createdTodayCount), accent: 'slate' },
  ]
})

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

function startRenameGroup(group: TaskGroupResponse) {
  editingGroupId.value = group.id
  editingGroupName.value = group.name
  groupActionError.value = ''
}

function cancelRenameGroup() {
  editingGroupId.value = null
  editingGroupName.value = ''
  groupActionError.value = ''
}

async function handleRenameGroup(groupId: string) {
  const name = editingGroupName.value.trim()

  if (!name) {
    groupActionError.value = 'Informe o nome do grupo.'
    return
  }

  groupActionLoadingId.value = groupId
  groupActionError.value = ''

  try {
    const updatedGroup = await taskService.updateTaskGroup(groupId, name, authStore.token)
    taskGroups.value = taskGroups.value.map((group) => (group.id === groupId ? { ...group, name: updatedGroup.name } : group))
    cancelRenameGroup()
  } catch {
    groupActionError.value = 'Não foi possível renomear o grupo.'
  } finally {
    groupActionLoadingId.value = null
  }
}

async function handleDeleteGroup(groupId: string) {
  if (!window.confirm('Deseja excluir este grupo e todas as tarefas dele?')) {
    return
  }

  groupActionLoadingId.value = groupId
  groupActionError.value = ''

  try {
    await taskService.deleteTaskGroup(groupId, authStore.token)
    taskGroups.value = taskGroups.value.filter((group) => group.id !== groupId)

    if (selectedGroupId.value === groupId) {
      selectedGroupId.value = null
      tasks.value = []
    }

    cancelRenameGroup()
  } catch {
    groupActionError.value = 'Não foi possível excluir o grupo.'
  } finally {
    groupActionLoadingId.value = null
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
          <article v-for="group in taskGroups" :key="group.id" class="task-group">
            <div class="group-header">
                <template v-if="editingGroupId === group.id">
                  <form class="group-edit-form" @submit.prevent="handleRenameGroup(group.id)" @click.stop>
                    <input v-model="editingGroupName" type="text" placeholder="Novo nome do grupo" />
                    <div class="group-edit-actions">
                      <button type="submit" class="mini-action-button primary" :disabled="groupActionLoadingId === group.id">
                        {{ groupActionLoadingId === group.id ? 'Salvando...' : 'Salvar' }}
                      </button>
                      <button type="button" class="mini-action-button secondary" @click.stop="cancelRenameGroup">
                        Cancelar
                      </button>
                    </div>
                  </form>
                </template>
                <template v-else>
                  <h4>{{ group.name }}</h4>
                  <p>{{ group.description || 'Sem descrição' }}</p>
                </template>
              </div>

              <div class="group-meta">
                <span>{{ group.tasks?.length ?? 0 }} tarefas</span>
                <button type="button" class="group-action-link" @click.stop="loadTasks(group.id)">Ver tarefas</button>
              </div>

            <div v-if="editingGroupId !== group.id" class="task-group-actions" @click.stop>
              <button type="button" class="group-action-button edit" @click="startRenameGroup(group)">Renomear</button>
              <button type="button" class="group-action-button delete" @click="handleDeleteGroup(group.id)" :disabled="groupActionLoadingId === group.id">
                {{ groupActionLoadingId === group.id ? 'Excluindo...' : 'Excluir' }}
              </button>
            </div>
          </article>
        </div>

        <p v-if="groupActionError" class="feedback-error">{{ groupActionError }}</p>

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