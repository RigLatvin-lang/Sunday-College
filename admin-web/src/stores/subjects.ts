import { defineStore } from 'pinia'
import { ref } from 'vue'
import { subjectsApi } from '@/api/subjects'
import type { SubjectResponse, CreateSubjectRequest } from '@/types'

export const useSubjectsStore = defineStore('subjects', () => {
  const items = ref<SubjectResponse[]>([])
  const loading = ref(false)

  async function fetchAll() {
    loading.value = true
    try {
      const { data } = await subjectsApi.getAll()
      items.value = data
    } finally {
      loading.value = false
    }
  }

  async function create(payload: CreateSubjectRequest) {
    await subjectsApi.create(payload)
    await fetchAll()
  }

  async function update(id: number, payload: CreateSubjectRequest) {
    await subjectsApi.update(id, payload)
    await fetchAll()
  }

  async function remove(id: number) {
    await subjectsApi.delete(id)
    await fetchAll()
  }

  return { items, loading, fetchAll, create, update, remove }
})
