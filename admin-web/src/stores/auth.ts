import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authApi } from '@/api/auth'
import type { UserProfile } from '@/types'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('token') || '')
  const user = ref<UserProfile | null>(
    JSON.parse(localStorage.getItem('user') || 'null'),
  )

  const isAuthenticated = computed(() => !!token.value)
  const fullName = computed(() => user.value?.fullName || '')

  async function login(login: string, password: string) {
    const { data } = await authApi.login({ login, password })
    token.value = data.token
    localStorage.setItem('token', data.token)
    const profile: UserProfile = {
      id: 0,
      fullName: data.fullName,
      login,
      role: data.role,
    }
    user.value = profile
    localStorage.setItem('user', JSON.stringify(profile))
  }

  async function fetchProfile() {
    const { data } = await authApi.me()
    user.value = data
    localStorage.setItem('user', JSON.stringify(data))
  }

  function logout() {
    token.value = ''
    user.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }

  return { token, user, isAuthenticated, fullName, login, fetchProfile, logout }
})
