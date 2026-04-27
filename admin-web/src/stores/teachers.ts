import { defineStore } from 'pinia'
import { ref } from 'vue'
import { teachersApi } from '@/api/teachers'
import type { TeacherInfo, CreateTeacherRequest, UpdateTeacherRequest } from '@/types'

export const useTeachersStore = defineStore('teachers', () => {
  const items = ref<TeacherInfo[]>([])
  const loading = ref(false)

  async function fetchAll() {
    loading.value = true
    try {
      const { data } = await teachersApi.getAll()
      items.value = data
    } finally {
      loading.value = false
    }
  }

  async function create(payload: CreateTeacherRequest) {
    await teachersApi.create(payload)
    await fetchAll()
  }

  async function update(id: number, payload: UpdateTeacherRequest) {
    await teachersApi.update(id, payload)
    await fetchAll()
  }

  async function remove(id: number) {
    await teachersApi.delete(id)
    await fetchAll()
  }

  return { items, loading, fetchAll, create, update, remove }
})
