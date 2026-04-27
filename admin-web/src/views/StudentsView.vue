<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useStudentsStore } from '@/stores/students'
import type { StudentResponse } from '@/types'

const store = useStudentsStore()
const showModal = ref(false)
const editingId = ref<number | null>(null)
const search = ref('')

const form = ref({
  lastName: '',
  firstName: '',
  middleName: '',
  birthDate: '',
  inn: '',
  phone: '',
})

onMounted(() => store.fetchAll())

const filtered = computed(() => {
  if (!search.value) return store.items
  const q = search.value.toLowerCase()
  return store.items.filter(
    (s) =>
      s.lastName.toLowerCase().includes(q) ||
      s.firstName.toLowerCase().includes(q) ||
      (s.phone && s.phone.includes(q)),
  )
})

function openCreate() {
  editingId.value = null
  form.value = { lastName: '', firstName: '', middleName: '', birthDate: '', inn: '', phone: '' }
  showModal.value = true
}

function openEdit(s: StudentResponse) {
  editingId.value = s.id
  form.value = {
    lastName: s.lastName,
    firstName: s.firstName,
    middleName: s.middleName || '',
    birthDate: s.birthDate || '',
    inn: s.inn || '',
    phone: s.phone || '',
  }
  showModal.value = true
}

async function handleSubmit() {
  const payload = {
    ...form.value,
    middleName: form.value.middleName || undefined,
    birthDate: form.value.birthDate || undefined,
    inn: form.value.inn || undefined,
    phone: form.value.phone || undefined,
  }
  if (editingId.value) {
    await store.update(editingId.value, payload)
  } else {
    await store.create(payload)
  }
  showModal.value = false
}

async function handleDelete(id: number) {
  if (confirm('Удалить студента?')) await store.remove(id)
}

function formatDate(d?: string) {
  if (!d) return '—'
  return new Date(d).toLocaleDateString('ru-RU')
}
</script>

<template>
  <div class="page">
    <div class="page-header">
      <input v-model="search" class="search-input" placeholder="Поиск студентов..." />
      <button class="btn-primary" @click="openCreate">+ Новый студент</button>
    </div>

    <div class="table-card">
      <table class="data-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Фамилия</th>
            <th>Имя</th>
            <th>Отчество</th>
            <th>Дата рождения</th>
            <th>Телефон</th>
            <th>Действия</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="s in filtered" :key="s.id">
            <td class="td-id">{{ s.id }}</td>
            <td class="td-name">{{ s.lastName }}</td>
            <td>{{ s.firstName }}</td>
            <td class="td-muted">{{ s.middleName || '—' }}</td>
            <td class="td-muted">{{ formatDate(s.birthDate) }}</td>
            <td class="td-muted">{{ s.phone || '—' }}</td>
            <td class="td-actions">
              <button class="btn-icon" title="Редактировать" @click="openEdit(s)">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/></svg>
              </button>
              <button class="btn-icon danger" title="Удалить" @click="handleDelete(s.id)">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/></svg>
              </button>
            </td>
          </tr>
        </tbody>
      </table>
      <div v-if="!filtered.length && !store.loading" class="empty-state">Нет студентов</div>
    </div>

    <Teleport to="body">
      <div v-if="showModal" class="modal-overlay" @click.self="showModal = false">
        <div class="modal" @click.stop>
          <h2>{{ editingId ? 'Редактировать' : 'Новый студент' }}</h2>
          <form @submit.prevent="handleSubmit">
            <div class="fields-row">
              <div class="field">
                <label>Фамилия *</label>
                <input v-model="form.lastName" required placeholder="Иванов" />
              </div>
              <div class="field">
                <label>Имя *</label>
                <input v-model="form.firstName" required placeholder="Иван" />
              </div>
            </div>
            <div class="field">
              <label>Отчество</label>
              <input v-model="form.middleName" placeholder="Иванович" />
            </div>
            <div class="fields-row">
              <div class="field">
                <label>Дата рождения</label>
                <input v-model="form.birthDate" type="date" />
              </div>
              <div class="field">
                <label>Телефон</label>
                <input v-model="form.phone" placeholder="+7 999 123 45 67" />
              </div>
            </div>
            <div class="field">
              <label>ИНН</label>
              <input v-model="form.inn" placeholder="1234567890" />
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
  display: flex; align-items: center; gap: 12px; margin-bottom: 20px;
}

.search-input {
  flex: 1; padding: 11px 16px;
  border: 1px solid var(--border); border-radius: var(--radius);
  background: var(--bg-card); color: var(--text); font-size: 0.875rem;
  transition: all var(--transition); box-shadow: var(--shadow-neumorphic-sm);
}
.search-input:focus { outline: none; border-color: var(--primary); box-shadow: 0 0 0 3px var(--primary-200); }
.search-input::placeholder { color: var(--text-muted); }

.btn-primary {
  padding: 11px 20px;
  background: linear-gradient(135deg, var(--primary), var(--primary-dark));
  color: white; border: none; border-radius: var(--radius);
  font-size: 0.85rem; font-weight: 600; cursor: pointer;
  transition: all var(--transition);
  box-shadow: 0 3px 10px rgba(0,188,212,0.25); white-space: nowrap;
}
.btn-primary:hover { transform: translateY(-1px); box-shadow: 0 5px 16px rgba(0,188,212,0.35); }
.btn-primary:active { transform: scale(0.97); }

.btn-secondary {
  padding: 11px 20px; background: var(--bg-card); color: var(--text-secondary);
  border: 1px solid var(--border); border-radius: var(--radius);
  font-size: 0.85rem; font-weight: 500; cursor: pointer; transition: all var(--transition);
}
.btn-secondary:hover { background: var(--bg-hover); }
.btn-secondary:active { transform: scale(0.97); }

.table-card {
  background: var(--bg-card); border-radius: var(--radius-lg);
  box-shadow: var(--shadow-neumorphic); overflow: hidden;
  animation: fadeIn 0.35s ease both; border: 1px solid var(--border-light);
}

.data-table { width: 100%; border-collapse: collapse; }

.data-table th {
  text-align: left; padding: 14px 18px; font-size: 0.75rem;
  font-weight: 600; color: var(--text-muted); text-transform: uppercase;
  letter-spacing: 0.5px; border-bottom: 1px solid var(--border-light); background: var(--bg-input);
}

.data-table td {
  padding: 14px 18px; font-size: 0.875rem;
  border-bottom: 1px solid var(--border-light); transition: background var(--transition);
}
.data-table tr:hover td { background: var(--primary-50); }
.data-table tr:last-child td { border-bottom: none; }

.td-id { color: var(--text-muted); font-size: 0.8rem; width: 60px; }
.td-name { font-weight: 500; }
.td-muted { color: var(--text-secondary); }

.td-actions { width: 100px; display: flex; gap: 4px; }

.btn-icon {
  width: 34px; height: 34px; border-radius: var(--radius-sm);
  border: 1px solid var(--border-light); background: var(--bg-card);
  color: var(--text-muted); display: flex; align-items: center; justify-content: center;
  cursor: pointer; transition: all var(--transition);
}
.btn-icon:hover { color: var(--primary); border-color: var(--primary); background: var(--primary-50); }
.btn-icon:active { transform: scale(0.9); }
.btn-icon.danger:hover { color: var(--danger); border-color: var(--danger); background: rgba(239,68,68,0.06); }

.modal-overlay {
  position: fixed; inset: 0; background: rgba(0,0,0,0.3); backdrop-filter: blur(4px);
  display: flex; align-items: center; justify-content: center; z-index: 1000;
  animation: fadeIn 0.2s ease;
}
.modal {
  background: var(--bg-card); border-radius: var(--radius-xl); padding: 28px;
  width: 100%; max-width: 500px; box-shadow: var(--shadow-xl); animation: scaleIn 0.25s ease;
}
.modal h2 { font-size: 1.15rem; font-weight: 700; color: var(--text); margin-bottom: 20px; }

.fields-row { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; }

.field { display: flex; flex-direction: column; gap: 6px; margin-bottom: 16px; }
.field label {
  font-size: 0.78rem; font-weight: 600; color: var(--text-secondary);
  text-transform: uppercase; letter-spacing: 0.5px;
}
.field input {
  padding: 11px 14px; border: 1px solid var(--border); border-radius: var(--radius);
  background: var(--bg-input); color: var(--text); font-size: 0.875rem;
  transition: all var(--transition); box-shadow: var(--shadow-inset);
}
.field input:focus { outline: none; border-color: var(--primary); box-shadow: 0 0 0 3px var(--primary-200), var(--shadow-inset); }
.field input::placeholder { color: var(--text-muted); }

.modal-actions { display: flex; gap: 10px; justify-content: flex-end; margin-top: 20px; }

.empty-state { text-align: center; padding: 40px 20px; color: var(--text-muted); font-size: 0.9rem; }
</style>
