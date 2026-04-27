import { defineStore } from 'pinia'
import { ref } from 'vue'
import { groupsApi } from '@/api/groups'
import type { GroupResponse, CreateGroupRequest } from '@/types'

export const useGroupsStore = defineStore('groups', () => {
  const items = ref<GroupResponse[]>([])
  const loading = ref(false)

  async function fetchAll() {
    loading.value = true
    try {
      const { data } = await groupsApi.getAll()
      items.value = data
    } finally {
      loading.value = false
    }
  }

  async function create(payload: CreateGroupRequest) {
    await groupsApi.create(payload)
    await fetchAll()
  }

  async function update(id: number, payload: CreateGroupRequest) {
    await groupsApi.update(id, payload)
    await fetchAll()
  }

  async function remove(id: number) {
    await groupsApi.delete(id)
    await fetchAll()
  }

  async function addStudents(groupId: number, studentIds: number[]) {
    await groupsApi.addStudents(groupId, { studentIds })
    await fetchAll()
  }

  async function removeStudent(groupId: number, studentId: number) {
    await groupsApi.removeStudent(groupId, studentId)
    await fetchAll()
  }

  return { items, loading, fetchAll, create, update, remove, addStudents, removeStudent }
})
