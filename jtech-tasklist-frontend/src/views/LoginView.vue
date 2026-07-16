<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const email = ref('')
const password = ref('')
const errors = ref<{ email?: string; password?: string; general?: string }>({})
const isLoading = ref(false)

function validate(): boolean {
  errors.value = {}

  if (!email.value.trim()) {
    errors.value.email = 'Informe o e-mail'
  }

  if (!password.value.trim()) {
    errors.value.password = 'Informe a senha'
  }

  return !errors.value.email && !errors.value.password
}

async function handleSubmit() {
  if (!validate()) return

  isLoading.value = true
  errors.value.general = undefined

  try {
    await authStore.login(email.value.trim(), password.value)
    router.push({ name: 'tarefas' })
  } catch {
    errors.value.general = 'Não foi possível fazer login. Verifique suas credenciais.'
  } finally {
    isLoading.value = false
  }
}
</script>

<template>
  <div class="login-page">
    <div class="login-card">
      <img src="@/assets/tasker1.png" alt="Logo" class="logo" width="330px" />
    </div>
    <form class="login-card" @submit.prevent="handleSubmit">
      <h1>Entrar</h1>
      <p class="subtitle">Acesse sua conta para gerenciar suas tarefas</p>

      <div class="field">
        <label for="email">E-mail</label>
        <input
          id="email"
          v-model="email"
          type="email"
          autocomplete="email"
          placeholder="seu@email.com"
          :aria-invalid="!!errors.email"
        />
        <span v-if="errors.email" class="error">{{ errors.email }}</span>
      </div>

      <div class="field">
        <label for="password">Senha</label>
        <input
          id="password"
          v-model="password"
          type="password"
          autocomplete="current-password"
          placeholder="••••••••"
          :aria-invalid="!!errors.password"
        />
        <span v-if="errors.password" class="error">{{ errors.password }}</span>
      </div>

      <p v-if="errors.general" class="error general">{{ errors.general }}</p>

      <button type="submit" class="submit" :disabled="isLoading">
        {{ isLoading ? 'Entrando...' : 'Entrar' }}
      </button>
    </form>
    <div class="login-register-card">
      <router-link to="/cadastrar" class="link">Cadastrar-se</router-link>
    </div>
  </div>
</template>

<style src="@/styles/Login.css"></style>
