import api from './axios'
import type { GroupResponse, CreateGroupRequest, AddStudentsRequest } from '@/types'

export const groupsApi = {
  getAll() {
    return api.get<GroupResponse[]>('/groups')
  },
  getById(id: number) {
    return api.get<GroupResponse>(`/groups/${id}`)
  },
  create(data: CreateGroupRequest) {
    return api.post<GroupResponse>('/groups', data)
  },
  update(id: number, data: CreateGroupRequest) {
    return api.put<GroupResponse>(`/groups/${id}`, data)
  },
  delete(id: number) {
    return api.delete(`/groups/${id}`)
  },
  addStudents(groupId: number, data: AddStudentsRequest) {
    return api.post<GroupResponse>(`/groups/${groupId}/students`, data)
  },
  removeStudent(groupId: number, studentId: number) {
    return api.delete<GroupResponse>(`/groups/${groupId}/students/${studentId}`)
  },
}
