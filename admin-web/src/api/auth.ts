import api from './axios'
import type { LoginRequest, LoginResponse, UserProfile, CreateAdminRequest } from '@/types'

export const authApi = {
  login(data: LoginRequest) {
    return api.post<LoginResponse>('/auth/login', data)
  },
  me() {
    return api.get<UserProfile>('/auth/me')
  },
  createAdmin(data: CreateAdminRequest, securityKey: string) {
    return api.post('/admin/create', data, {
      headers: { 'X-Security-Key': securityKey },
    })
  },
}
