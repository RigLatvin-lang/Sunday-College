import api from './axios'
import type { ParentResponse, CreateParentRequest } from '@/types'

export const parentsApi = {
  getAll() {
    return api.get<ParentResponse[]>('/parents')
  },
  create(data: CreateParentRequest) {
    return api.post<ParentResponse>('/parents', data)
  },
  delete(id: number) {
    return api.delete(`/parents/${id}`)
  },
}
