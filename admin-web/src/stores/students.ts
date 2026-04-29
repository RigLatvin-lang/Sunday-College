import { defineStore } from 'pinia'
import { ref } from 'vue'
import { studentsApi } from '@/api/students'
import type { StudentResponse, CreateStudentRequest, UpdateStudentRequest } from '@/types'

export const useStudentsStore = defineStore('students', () => {
  const items = ref<StudentResponse[]>([])
  const loading = ref(false)

  async function fetchAll() {
    loading.value = true
    try {
      const { data } = await studentsApi.getAll()
      items.value = data
    } finally {
      loading.value = false
    }
  }

  async function create(payload: CreateStudentRequest) {
    await studentsApi.create(payload)
    await fetchAll()
  }

  async function update(id: number, payload: UpdateStudentRequest) {
    await studentsApi.update(id, payload)
    await fetchAll()
  }

  async function remove(id: number) {
    await studentsApi.delete(id)
    await fetchAll()
  }

  return { items, loading, fetchAll, create, update, remove }
})
