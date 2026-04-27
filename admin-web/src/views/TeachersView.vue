<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useTeachersStore } from '@/stores/teachers'
import type { TeacherInfo } from '@/types'

const store = useTeachersStore()
const showModal = ref(false)
const editingId = ref<number | null>(null)
const form = ref({ fullName: '', login: '', password: '' })
const search = ref('')

onMounted(() => store.fetchAll())

const filtered = computed(() => {
  if (!search.value) return store.items
  const q = search.value.toLowerCase()
  return store.items.filter(
    (t) => t.fullName.toLowerCase().includes(q) || t.login.toLowerCase().includes(q),
  )
})

function openCreate() {
  editingId.value = null
  form.value = { fullName: '', login: '', password: '' }
  showModal.value = true
}

function openEdit(t: TeacherInfo) {
  editingId.value = t.id
  form.value = { fullName: t.fullName, login: t.login, password: '' }
  showModal.value = true
}

async function handleSubmit() {
  if (editingId.value) {
    const payload: Record<string, string> = { fullName: form.value.fullName }
    if (form.value.login) payload.login = form.value.login
    if (form.value.password) payload.password = form.value.password
    await store.update(editingId.value, payload)
  } else {
    await store.create(form.value)
  }
  showModal.value = false
}

async function handleDelete(id: number) {
  if (confirm('Удалить преподавателя?')) await store.remove(id)
}
</script>

<template>
  <div class="page">
    <div class="page-header">
      <input v-model="search" class="search-input" placeholder="Поиск преподавателей..." />
      <button class="btn-primary" @click="openCreate">+ Новый преподаватель</button>
    </div>

    <div class="table-card">
      <table class="data-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>ФИО</th>
            <th>Логин</th>
            <th>Действия</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="t in filtered" :key="t.id">
            <td class="td-id">{{ t.id }}</td>
            <td class="td-name">{{ t.fullName }}</td>
            <td class="td-login">{{ t.login }}</td>
            <td class="td-actions">
              <button class="btn-icon" title="Редактировать" @click="openEdit(t)">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/></svg>
              </button>
              <button class="btn-icon danger" title="Удалить" @click="handleDelete(t.id)">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/></svg>
              </button>
            </td>
          </tr>
        </tbody>
      </table>
      <div v-if="!filtered.length && !store.loading" class="empty-state">Нет преподавателей</div>
    </div>

    <Teleport to="body">
      <div v-if="showModal" class="modal-overlay" @click.self="showModal = false">
        <div class="modal" @click.stop>
          <h2>{{ editingId ? 'Редактировать' : 'Новый преподаватель' }}</h2>
          <form @submit.prevent="handleSubmit">
            <div class="field">
              <label>ФИО</label>
              <input v-model="form.fullName" required placeholder="Иванов Иван Иванович" />
            </div>
            <div class="field">
              <label>Логин</label>
              <input v-model="form.login" :required="!editingId" placeholder="teacher_login" />
            </div>
            <div class="field">
              <label>Пароль {{ editingId ? '(оставьте пустым, чтобы не менять)' : '' }}</label>
              <input v-model="form.password" type="password" :required="!editingId" placeholder="••••••" />
            </div>
            <div class="modal-actions">
              <button type="button" class="btn-secondary" @click="showModal = false">Отмена</button>
              <button type="submit" class="btn-primary">{{ editingId ? 'Сохранить' : 'Создать' }}</button>
            </div>
          </form>
        </div>
      </div>
    </Teleport>
  </div>
</template>

<style scoped>
.page { animation: fadeIn 0.3s ease; }

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
.search-input::placeholder { color: var(--text-muted); }

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
.btn-primary:hover { transform: translateY(-1px); box-shadow: 0 5px 16px rgba(0, 188, 212, 0.35); }
.btn-primary:active { transform: scale(0.97); }

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
.btn-secondary:hover { background: var(--bg-hover); }
.btn-secondary:active { transform: scale(0.97); }

.table-card {
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-neumorphic);
  overflow: hidden;
  animation: fadeIn 0.35s ease both;
  border: 1px solid var(--border-light);
}

.data-table {
  width: 100%;
  border-collapse: collapse;
}

.data-table th {
  text-align: left;
  padding: 14px 18px;
  font-size: 0.75rem;
  font-weight: 600;
  color: var(--text-muted);
  text-transform: uppercase;
  letter-spacing: 0.5px;
  border-bottom: 1px solid var(--border-light);
  background: var(--bg-input);
}

.data-table td {
  padding: 14px 18px;
  font-size: 0.875rem;
  border-bottom: 1px solid var(--border-light);
  transition: background var(--transition);
}

.data-table tr:hover td {
  background: var(--primary-50);
}

.data-table tr:last-child td {
  border-bottom: none;
}

.td-id {
  color: var(--text-muted);
  font-size: 0.8rem;
  width: 60px;
}

.td-name { font-weight: 500; }

.td-login {
  color: var(--text-secondary);
  font-family: 'SF Mono', monospace;
  font-size: 0.82rem;
}

.td-actions {
  width: 100px;
  display: flex;
  gap: 4px;
}

.btn-icon {
  width: 34px; height: 34px;
  border-radius: var(--radius-sm);
  border: 1px solid var(--border-light);
  background: var(--bg-card);
  color: var(--text-muted);
  display: flex; align-items: center; justify-content: center;
  cursor: pointer;
  transition: all var(--transition);
}
.btn-icon:hover { color: var(--primary); border-color: var(--primary); background: var(--primary-50); }
.btn-icon:active { transform: scale(0.9); }
.btn-icon.danger:hover { color: var(--danger); border-color: var(--danger); background: rgba(239,68,68,0.06); }

.modal-overlay {
  position: fixed; inset: 0;
  background: rgba(0,0,0,0.3);
  backdrop-filter: blur(4px);
  display: flex; align-items: center; justify-content: center;
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

.modal h2 {
  font-size: 1.15rem; font-weight: 700; color: var(--text); margin-bottom: 20px;
}

.field {
  display: flex; flex-direction: column; gap: 6px; margin-bottom: 16px;
}
.field label {
  font-size: 0.78rem; font-weight: 600; color: var(--text-secondary);
  text-transform: uppercase; letter-spacing: 0.5px;
}
.field input {
  padding: 11px 14px;
  border: 1px solid var(--border);
  border-radius: var(--radius);
  background: var(--bg-input);
  color: var(--text);
  font-size: 0.875rem;
  transition: all var(--transition);
  box-shadow: var(--shadow-inset);
}
.field input:focus {
  outline: none;
  border-color: var(--primary);
  box-shadow: 0 0 0 3px var(--primary-200), var(--shadow-inset);
}
.field input::placeholder { color: var(--text-muted); }

.modal-actions {
  display: flex; gap: 10px; justify-content: flex-end; margin-top: 20px;
}

.empty-state {
  text-align: center; padding: 40px 20px; color: var(--text-muted); font-size: 0.9rem;
}
</style>
