import api from './axios'
import type { StudentResponse, CreateStudentRequest, UpdateStudentRequest } from '@/types'

export const studentsApi = {
  getAll() {
    return api.get<StudentResponse[]>('/students')
  },
  getById(id: number) {
    return api.get<StudentResponse>(`/students/${id}`)
  },
  create(data: CreateStudentRequest) {
    return api.post<StudentResponse>('/students', data)
  },
  update(id: number, data: UpdateStudentRequest) {
    return api.put<StudentResponse>(`/students/${id}`, data)
  },
  delete(id: number) {
    return api.delete(`/students/${id}`)
  },
}
