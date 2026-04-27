import api from './axios'
import type { TeacherInfo, CreateTeacherRequest, UpdateTeacherRequest, TeacherResponse } from '@/types'

export const teachersApi = {
  getAll() {
    return api.get<TeacherInfo[]>('/teachers')
  },
  getById(id: number) {
    return api.get<TeacherInfo>(`/teachers/${id}`)
  },
  create(data: CreateTeacherRequest) {
    return api.post<TeacherResponse>('/teachers', data)
  },
  update(id: number, data: UpdateTeacherRequest) {
    return api.put<TeacherInfo>(`/teachers/${id}`, data)
  },
  delete(id: number) {
    return api.delete(`/teachers/${id}`)
  },
}
