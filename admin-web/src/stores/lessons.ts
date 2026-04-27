import { defineStore } from 'pinia'
import { ref } from 'vue'
import { lessonsApi } from '@/api/lessons'
import type { LessonResponse, CreateLessonRequest } from '@/types'

export const useLessonsStore = defineStore('lessons', () => {
  const items = ref<LessonResponse[]>([])
  const loading = ref(false)

  async function fetchAll() {
    loading.value = true
    try {
      const { data } = await lessonsApi.getAll()
      items.value = data
    } finally {
      loading.value = false
    }
  }

  async function create(payload: CreateLessonRequest) {
    await lessonsApi.create(payload)
    await fetchAll()
  }

  async function remove(id: number) {
    await lessonsApi.delete(id)
    await fetchAll()
  }

  return { items, loading, fetchAll, create, remove }
})
