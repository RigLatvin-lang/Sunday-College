import api from './axios'
import type { LessonResponse, CreateLessonRequest } from '@/types'

export const lessonsApi = {
  getAll() {
    return api.get<LessonResponse[]>('/lessons')
  },
  getById(id: number) {
    return api.get<LessonResponse>(`/lessons/${id}`)
  },
  create(data: CreateLessonRequest) {
    return api.post<LessonResponse>('/lessons', data)
  },
  delete(id: number) {
    return api.delete(`/lessons/${id}`)
  },
}
