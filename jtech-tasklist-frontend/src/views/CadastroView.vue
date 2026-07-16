<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const form = ref({
  name: '',
  email: '',
  password: '',
})
const errors = ref<{ name?: string; email?: string; password?: string; general?: string }>({})
const isLoading = ref(false)

function validate(): boolean {
  errors.value = {}

  if (!form.value.name.trim()) {
    errors.value.name = 'Informe o nome'
  }

  if (!form.value.email.trim()) {
    errors.value.email = 'Informe o e-mail'
  }

  if (!form.value.password.trim()) {
    errors.value.password = 'Informe a senha'
  }

  return !errors.value.name && !errors.value.email && !errors.value.password
}

async function handleSubmit() {
  if (!validate()) return

  isLoading.value = true
  errors.value.general = undefined

  try {
    await authStore.register(form.value.name.trim(), form.value.email.trim(), form.value.password)
    router.push({ name: 'tarefas' })
  } catch {
    errors.value.general = 'Não foi possível criar a conta. Tente novamente.'
  } finally {
    isLoading.value = false
  }
}
</script>

<template>
  <div class="login-page">
    <form class="login-card" @submit.prevent="handleSubmit">
      <h1>Criar conta</h1>
      <p class="subtitle">Cadastre-se para organizar suas tarefas</p>

      <div class="field">
        <label for="name">Nome</label>
        <input
          id="name"
          v-model="form.name"
          type="text"
          autocomplete="name"
          placeholder="Seu nome"
          :aria-invalid="!!errors.name"
        />
        <span v-if="errors.name" class="error">{{ errors.name }}</span>
      </div>

      <div class="field">
        <label for="email">E-mail</label>
        <input
          id="email"
          v-model="form.email"
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
          v-model="form.password"
          type="password"
          autocomplete="new-password"
          placeholder="••••••••"
          :aria-invalid="!!errors.password"
        />
        <span v-if="errors.password" class="error">{{ errors.password }}</span>
      </div>

      <p v-if="errors.general" class="error general">{{ errors.general }}</p>

      <button type="submit" class="submit" :disabled="isLoading">
        {{ isLoading ? 'Cadastrando...' : 'Criar conta' }}
      </button>
    </form>

    <div class="login-register-card">
      <router-link to="/login" class="link">Já tenho conta</router-link>
    </div>
  </div>
</template>

<style src="@/styles/Login.css"></style>
