import api from './axios'
import type { SubjectResponse, CreateSubjectRequest } from '@/types'

export const subjectsApi = {
  getAll() {
    return api.get<SubjectResponse[]>('/subjects')
  },
  getById(id: number) {
    return api.get<SubjectResponse>(`/subjects/${id}`)
  },
  create(data: CreateSubjectRequest) {
    return api.post<SubjectResponse>('/subjects', data)
  },
  update(id: number, data: CreateSubjectRequest) {
    return api.put<SubjectResponse>(`/subjects/${id}`, data)
  },
  delete(id: number) {
    return api.delete(`/subjects/${id}`)
  },
}
