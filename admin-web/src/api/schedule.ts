import api from './axios'
import type { LessonResponse } from '@/types'

export const scheduleApi = {
  get(params?: { date?: string; groupId?: number; teacherId?: number }) {
    return api.get<LessonResponse[]>('/schedule', { params })
  },
}
