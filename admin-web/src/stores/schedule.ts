import { defineStore } from 'pinia'
import { ref } from 'vue'
import { scheduleApi } from '@/api/schedule'
import type { LessonResponse } from '@/types'

export const useScheduleStore = defineStore('schedule', () => {
  const items = ref<LessonResponse[]>([])
  const loading = ref(false)

  async function fetch(params?: { date?: string; groupId?: number; teacherId?: number }) {
    loading.value = true
    try {
      const { data } = await scheduleApi.get(params)
      items.value = data
    } finally {
      loading.value = false
    }
  }

  return { items, loading, fetch }
})
