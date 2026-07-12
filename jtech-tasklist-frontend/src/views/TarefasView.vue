<script setup lang="ts">
import { useAuthStore } from '@/stores/auth'
import { useRouter } from 'vue-router'

const authStore = useAuthStore()
const router = useRouter()

function handleLogout() {
  authStore.logout()
  router.push({ name: 'login' })
}
</script>

<template>
  <main class="tarefas-page">
    <header class="tarefas-header">
      <div>
        <h1>Tarefas</h1>
        <p v-if="authStore.user">Olá, {{ authStore.user.name }}</p>
      </div>
      <button type="button" class="logout" @click="handleLogout">Sair</button>
    </header>
  </main>
</template>

<style scoped>
.tarefas-page {
  min-height: 100vh;
  padding: 2rem;
}

.tarefas-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 1rem;
}

h1 {
  margin: 0 0 0.25rem;
}

p {
  margin: 0;
  color: #6b7280;
}

.logout {
  padding: 0.5rem 1rem;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  background: #fff;
  cursor: pointer;
}

.logout:hover {
  background: #f9fafb;
}
</style>
