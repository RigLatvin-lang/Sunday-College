<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useGroupsStore } from '@/stores/groups'
import { useStudentsStore } from '@/stores/students'
import type { GroupResponse } from '@/types'

const groups = useGroupsStore()
const studentsStore = useStudentsStore()

const showModal = ref(false)
const showStudentsModal = ref(false)
const editingId = ref<number | null>(null)
const form = ref({ name: '' })
const selectedGroup = ref<GroupResponse | null>(null)
const selectedStudentIds = ref<number[]>([])
const search = ref('')

onMounted(() => {
  groups.fetchAll()
  studentsStore.fetchAll()
})

const filteredGroups = computed(() => {
  if (!search.value) return groups.items
  const q = search.value.toLowerCase()
  return groups.items.filter((g) => g.name.toLowerCase().includes(q))
})

const availableStudents = computed(() => {
  if (!selectedGroup.value) return []
  const inGroup = new Set(selectedGroup.value.students.map((s) => s.id))
  return studentsStore.items.filter((s) => !inGroup.has(s.id))
})

function openCreate() {
  editingId.value = null
  form.value = { name: '' }
  showModal.value = true
}

function openEdit(g: GroupResponse) {
  editingId.value = g.id
  form.value = { name: g.name }
  showModal.value = true
}

function openStudents(g: GroupResponse) {
  selectedGroup.value = g
  selectedStudentIds.value = []
  showStudentsModal.value = true
}

async function handleSubmit() {
  if (editingId.value) {
    await groups.update(editingId.value, form.value)
  } else {
    await groups.create(form.value)
  }
  showModal.value = false
}

async function handleAddStudents() {
  if (selectedGroup.value && selectedStudentIds.value.length) {
    await groups.addStudents(selectedGroup.value.id, selectedStudentIds.value)
    selectedGroup.value = groups.items.find((g) => g.id === selectedGroup.value!.id) || null
    selectedStudentIds.value = []
  }
}

async function handleRemoveStudent(studentId: number) {
  if (selectedGroup.value) {
    await groups.removeStudent(selectedGroup.value.id, studentId)
    selectedGroup.value = groups.items.find((g) => g.id === selectedGroup.value!.id) || null
  }
}

async function handleDelete(id: number) {
  if (confirm('Удалить группу?')) {
    await groups.remove(id)
  }
}

function toggleStudent(id: number) {
  const idx = selectedStudentIds.value.indexOf(id)
  if (idx >= 0) selectedStudentIds.value.splice(idx, 1)
  else selectedStudentIds.value.push(id)
}
</script>

<template>
  <div class="page">
    <div class="page-header">
      <input v-model="search" class="search-input" placeholder="Поиск групп..." />
      <button class="btn-primary" @click="openCreate">+ Новая группа</button>
    </div>

    <div class="cards-grid">
      <div v-for="(group, i) in filteredGroups" :key="group.id" class="entity-card" :style="{ animationDelay: `${i * 0.04}s` }">
        <div class="entity-header">
          <div class="entity-icon group-icon">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/>
              <path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/>
            </svg>
          </div>
          <div class="entity-title-block">
            <h3>{{ group.name }}</h3>
            <span class="entity-subtitle">{{ group.students.length }} студентов</span>
          </div>
        </div>
        <div class="entity-actions">
          <button class="btn-icon" title="Студенты" @click="openStudents(group)">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
          </button>
          <button class="btn-icon" title="Редактировать" @click="openEdit(group)">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/></svg>
          </button>
          <button class="btn-icon danger" title="Удалить" @click="handleDelete(group.id)">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/></svg>
          </button>
        </div>
      </div>
    </div>

    <div v-if="!filteredGroups.length && !groups.loading" class="empty-state">
      <p>Нет групп</p>
    </div>

    <!-- Create/Edit Modal -->
    <Teleport to="body">
      <div v-if="showModal" class="modal-overlay" @click.self="showModal = false">
        <div class="modal" @click.stop>
          <h2>{{ editingId ? 'Редактировать группу' : 'Новая группа' }}</h2>
          <form @submit.prevent="handleSubmit">
            <div class="field">
              <label>Название</label>
              <input v-model="form.name" required placeholder="Название группы" />
            </div>
            <div class="modal-actions">
              <button type="button" class="btn-secondary" @click="showModal = false">Отмена</button>
              <button type="submit" class="btn-primary">{{ editingId ? 'Сохранить' : 'Создать' }}</button>
            </div>
          </form>
        </div>
      </div>
    </Teleport>

    <!-- Students Modal -->
    <Teleport to="body">
      <div v-if="showStudentsModal" class="modal-overlay" @click.self="showStudentsModal = false">
        <div class="modal modal-wide" @click.stop>
          <h2>Студенты — {{ selectedGroup?.name }}</h2>

          <div class="students-section" v-if="selectedGroup?.students.length">
            <h4>В группе</h4>
            <div class="student-chips">
              <span v-for="s in selectedGroup.students" :key="s.id" class="chip">
                {{ s.lastName }} {{ s.firstName }}
                <button class="chip-remove" @click="handleRemoveStudent(s.id)">&times;</button>
              </span>
            </div>
          </div>

          <div class="students-section" v-if="availableStudents.length">
            <h4>Добавить студентов</h4>
            <div class="student-list">
              <label v-for="s in availableStudents" :key="s.id" class="student-check">
                <input type="checkbox" :checked="selectedStudentIds.includes(s.id)" @change="toggleStudent(s.id)" />
                <span>{{ s.lastName }} {{ s.firstName }}</span>
              </label>
            </div>
            <button class="btn-primary" style="margin-top: 12px" :disabled="!selectedStudentIds.length" @click="handleAddStudents">
              Добавить выбранных ({{ selectedStudentIds.length }})
            </button>
          </div>

          <div class="modal-actions">
            <button class="btn-secondary" @click="showStudentsModal = false">Закрыть</button>
          </div>
        </div>
      </div>
    </Teleport>
  </div>
</template>

<style scoped>
.page {
  animation: fadeIn 0.3s ease;
}

.page-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
}

.search-input {
  flex: 1;
  padding: 11px 16px;
  border: 1px solid var(--border);
  border-radius: var(--radius);
  background: var(--bg-card);
  color: var(--text);
  font-size: 0.875rem;
  transition: all var(--transition);
  box-shadow: var(--shadow-neumorphic-sm);
}

.search-input:focus {
  outline: none;
  border-color: var(--primary);
  box-shadow: 0 0 0 3px var(--primary-200);
}

.search-input::placeholder {
  color: var(--text-muted);
}

.btn-primary {
  padding: 11px 20px;
  background: linear-gradient(135deg, var(--primary), var(--primary-dark));
  color: white;
  border: none;
  border-radius: var(--radius);
  font-size: 0.85rem;
  font-weight: 600;
  cursor: pointer;
  transition: all var(--transition);
  box-shadow: 0 3px 10px rgba(0, 188, 212, 0.25);
  white-space: nowrap;
}

.btn-primary:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 5px 16px rgba(0, 188, 212, 0.35);
}

.btn-primary:active:not(:disabled) {
  transform: scale(0.97);
}

.btn-primary:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.btn-secondary {
  padding: 11px 20px;
  background: var(--bg-card);
  color: var(--text-secondary);
  border: 1px solid var(--border);
  border-radius: var(--radius);
  font-size: 0.85rem;
  font-weight: 500;
  cursor: pointer;
  transition: all var(--transition);
}

.btn-secondary:hover {
  background: var(--bg-hover);
  border-color: var(--text-muted);
}

.btn-secondary:active {
  transform: scale(0.97);
}

/* Cards grid */
.cards-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 14px;
}

.entity-card {
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  padding: 20px;
  box-shadow: var(--shadow-neumorphic);
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  animation: fadeIn 0.35s ease both;
  transition: all var(--transition);
  border: 1px solid var(--border-light);
}

.entity-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-lg);
}

.entity-header {
  display: flex;
  align-items: center;
  gap: 14px;
  flex: 1;
  min-width: 0;
}

.entity-icon {
  width: 42px;
  height: 42px;
  border-radius: var(--radius);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.group-icon {
  background: rgba(16, 185, 129, 0.12);
  color: #10b981;
}

.entity-title-block {
  min-width: 0;
}

.entity-title-block h3 {
  font-size: 0.95rem;
  font-weight: 600;
  color: var(--text);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.entity-subtitle {
  font-size: 0.75rem;
  color: var(--text-muted);
}

.entity-actions {
  display: flex;
  gap: 4px;
  flex-shrink: 0;
}

.btn-icon {
  width: 34px;
  height: 34px;
  border-radius: var(--radius-sm);
  border: 1px solid var(--border-light);
  background: var(--bg-card);
  color: var(--text-muted);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all var(--transition);
}

.btn-icon:hover {
  color: var(--primary);
  border-color: var(--primary);
  background: var(--primary-50);
  box-shadow: var(--shadow-sm);
}

.btn-icon:active {
  transform: scale(0.9);
}

.btn-icon.danger:hover {
  color: var(--danger);
  border-color: var(--danger);
  background: rgba(239, 68, 68, 0.06);
}

/* Modal */
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.3);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  animation: fadeIn 0.2s ease;
}

.modal {
  background: var(--bg-card);
  border-radius: var(--radius-xl);
  padding: 28px;
  width: 100%;
  max-width: 440px;
  box-shadow: var(--shadow-xl);
  animation: scaleIn 0.25s ease;
}

.modal-wide {
  max-width: 560px;
  max-height: 80vh;
  overflow-y: auto;
}

.modal h2 {
  font-size: 1.15rem;
  font-weight: 700;
  color: var(--text);
  margin-bottom: 20px;
}

.field {
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin-bottom: 16px;
}

.field label {
  font-size: 0.78rem;
  font-weight: 600;
  color: var(--text-secondary);
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.field input,
.field select {
  padding: 11px 14px;
  border: 1px solid var(--border);
  border-radius: var(--radius);
  background: var(--bg-input);
  color: var(--text);
  font-size: 0.875rem;
  transition: all var(--transition);
  box-shadow: var(--shadow-inset);
}

.field input:focus,
.field select:focus {
  outline: none;
  border-color: var(--primary);
  box-shadow: 0 0 0 3px var(--primary-200), var(--shadow-inset);
}

.field input::placeholder {
  color: var(--text-muted);
}

.modal-actions {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
  margin-top: 20px;
}

/* Students modal */
.students-section {
  margin-bottom: 20px;
}

.students-section h4 {
  font-size: 0.85rem;
  font-weight: 600;
  color: var(--text-secondary);
  margin-bottom: 10px;
}

.student-chips {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.chip {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  background: var(--primary-50);
  color: var(--primary-dark);
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: 500;
}

.chip-remove {
  background: none;
  border: none;
  color: var(--primary-dark);
  font-size: 1.1rem;
  cursor: pointer;
  padding: 0;
  line-height: 1;
  opacity: 0.6;
  transition: opacity var(--transition);
}

.chip-remove:hover {
  opacity: 1;
}

.student-list {
  display: flex;
  flex-direction: column;
  gap: 4px;
  max-height: 200px;
  overflow-y: auto;
}

.student-check {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 12px;
  border-radius: var(--radius-sm);
  cursor: pointer;
  font-size: 0.85rem;
  transition: background var(--transition);
}

.student-check:hover {
  background: var(--bg-hover);
}

.student-check input[type="checkbox"] {
  accent-color: var(--primary);
  width: 16px;
  height: 16px;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: var(--text-muted);
  font-size: 0.9rem;
}
</style>
