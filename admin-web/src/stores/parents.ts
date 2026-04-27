import { defineStore } from 'pinia'
import { ref } from 'vue'
import { parentsApi } from '@/api/parents'
import type { ParentResponse, CreateParentRequest } from '@/types'

export const useParentsStore = defineStore('parents', () => {
  const items = ref<ParentResponse[]>([])
  const loading = ref(false)
  const error = ref<string | null>(null)

  async function fetchAll() {
    loading.value = true
    error.value = null
    try {
      const { data } = await parentsApi.getAll()
      items.value = data
    } catch (e: any) {
      error.value = e?.response?.data?.message || 'Ошибка загрузки'
    } finally {
      loading.value = false
    }
  }

  async function create(payload: CreateParentRequest) {
    await parentsApi.create(payload)
    await fetchAll()
  }

  async function remove(id: number) {
    await parentsApi.delete(id)
    await fetchAll()
  }

  return { items, loading, error, fetchAll, create, remove }
})
