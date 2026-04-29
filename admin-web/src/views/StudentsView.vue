<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useStudentsStore } from '@/stores/students'
import type { StudentResponse, CreateStudentRequest, UpdateStudentRequest } from '@/types'

const store = useStudentsStore()
const showModal = ref(false)
const editingId = ref<number | null>(null)
const search = ref('')
const submitting = ref(false)
const formError = ref<string | null>(null)
const showPassword = ref(false)

// Форма создания
const createForm = ref<CreateStudentRequest>({
  login: '',
  password: '',
  lastName: '',
  firstName: '',
  middleName: '',
  birthDate: '',
  inn: '',
  phone: '',
})

// Форма редактирования (пароль опционален)
const editForm = ref<UpdateStudentRequest & { login: string; password: string }>({
  login: '',
  password: '',
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
      (s.login && s.login.toLowerCase().includes(q)) ||
      (s.phone && s.phone.includes(q)),
  )
})

function openCreate() {
  editingId.value = null
  showPassword.value = false
  formError.value = null
  createForm.value = {
    login: '',
    password: '',
    lastName: '',
    firstName: '',
    middleName: '',
    birthDate: '',
    inn: '',
    phone: '',
  }
  showModal.value = true
}

function openEdit(s: StudentResponse) {
  editingId.value = s.id
  showPassword.value = false
  formError.value = null
  editForm.value = {
    login: s.login || '',
    password: '',
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
  formError.value = null
  submitting.value = true
  try {
    if (editingId.value) {
      const payload: UpdateStudentRequest = {
        lastName: editForm.value.lastName,
        firstName: editForm.value.firstName,
        middleName: editForm.value.middleName || undefined,
        birthDate: editForm.value.birthDate || undefined,
        inn: editForm.value.inn || undefined,
        phone: editForm.value.phone || undefined,
        login: editForm.value.login || undefined,
        password: editForm.value.password || undefined,
      }
      await store.update(editingId.value, payload)
    } else {
      const payload: CreateStudentRequest = {
        login: createForm.value.login,
        password: createForm.value.password,
        lastName: createForm.value.lastName,
        firstName: createForm.value.firstName,
        middleName: createForm.value.middleName || undefined,
        birthDate: createForm.value.birthDate || undefined,
        inn: createForm.value.inn || undefined,
        phone: createForm.value.phone || undefined,
      }
      await store.create(payload)
    }
    showModal.value = false
  } catch (e: any) {
    formError.value = e?.response?.data?.error ?? 'Произошла ошибка'
  } finally {
    submitting.value = false
  }
}

async function handleDelete(id: number) {
  if (confirm('Удалить студента и его аккаунт?')) await store.remove(id)
}

function formatDate(d?: string) {
  if (!d) return '—'
  return new Date(d).toLocaleDateString('ru-RU')
}
</script>

<template>
  <div class="page">
    <div class="page-header">
      <input v-model="search" class="search-input" placeholder="Поиск по имени, логину, телефону…" />
      <button class="btn-primary" @click="openCreate">+ Новый студент</button>
    </div>

    <div class="table-card">
      <table class="data-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Фамилия Имя</th>
            <th>Отчество</th>
            <th>Логин</th>
            <th>Дата рождения</th>
            <th>Телефон</th>
            <th>Действия</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="s in filtered" :key="s.id">
            <td class="td-id">{{ s.id }}</td>
            <td class="td-name">{{ s.lastName }} {{ s.firstName }}</td>
            <td class="td-muted">{{ s.middleName || '—' }}</td>
            <td>
              <span v-if="s.login" class="login-badge">
                <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/>
                </svg>
                {{ s.login }}
              </span>
              <span v-else class="td-muted">—</span>
            </td>
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
      <div v-if="store.loading" class="empty-state">Загрузка…</div>
      <div v-else-if="!filtered.length" class="empty-state">Нет студентов</div>
    </div>

    <!-- МОДАЛКА -->
    <Teleport to="body">
      <div v-if="showModal" class="modal-overlay" @click.self="showModal = false">
        <div class="modal" @click.stop>
          <h2>{{ editingId ? 'Редактировать студента' : 'Новый студент' }}</h2>

          <form @submit.prevent="handleSubmit">

            <!-- ─── Аккаунт ─────────────────────────────────── -->
            <div class="section-label">Аккаунт для входа</div>

            <div class="fields-row">
              <div class="field">
                <label>Логин *</label>
                <input
                  v-if="!editingId"
                  v-model="createForm.login"
                  required
                  placeholder="ivanov_p"
                  autocomplete="off"
                />
                <input
                  v-else
                  v-model="editForm.login"
                  placeholder="ivanov_p"
                  autocomplete="off"
                />
              </div>
              <div class="field">
                <label>{{ editingId ? 'Новый пароль' : 'Пароль *' }}</label>
                <div class="input-with-icon">
                  <input
                    v-if="!editingId"
                    v-model="createForm.password"
                    :type="showPassword ? 'text' : 'password'"
                    required
                    placeholder="••••••••"
                    autocomplete="new-password"
                  />
                  <input
                    v-else
                    v-model="editForm.password"
                    :type="showPassword ? 'text' : 'password'"
                    placeholder="Оставьте пустым — не менять"
                    autocomplete="new-password"
                  />
                  <button type="button" class="toggle-pw" @click="showPassword = !showPassword" tabindex="-1">
                    <svg v-if="!showPassword" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>
                    <svg v-else width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94"/><path d="M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19"/><line x1="1" y1="1" x2="23" y2="23"/></svg>
                  </button>
                </div>
              </div>
            </div>

            <!-- ─── Личные данные ───────────────────────────── -->
            <div class="section-label" style="margin-top: 4px;">Личные данные</div>

            <div class="fields-row">
              <div class="field">
                <label>Фамилия *</label>
                <input v-if="!editingId" v-model="createForm.lastName" required placeholder="Иванов" />
                <input v-else v-model="editForm.lastName" required placeholder="Иванов" />
              </div>
              <div class="field">
                <label>Имя *</label>
                <input v-if="!editingId" v-model="createForm.firstName" required placeholder="Иван" />
                <input v-else v-model="editForm.firstName" required placeholder="Иван" />
              </div>
            </div>

            <div class="field">
              <label>Отчество</label>
              <input v-if="!editingId" v-model="createForm.middleName" placeholder="Иванович" />
              <input v-else v-model="editForm.middleName" placeholder="Иванович" />
            </div>

            <div class="fields-row">
              <div class="field">
                <label>Дата рождения</label>
                <input v-if="!editingId" v-model="createForm.birthDate" type="date" />
                <input v-else v-model="editForm.birthDate" type="date" />
              </div>
              <div class="field">
                <label>Телефон</label>
                <input v-if="!editingId" v-model="createForm.phone" placeholder="+7 999 123 45 67" />
                <input v-else v-model="editForm.phone" placeholder="+7 999 123 45 67" />
              </div>
            </div>

            <div class="field">
              <label>ИНН</label>
              <input v-if="!editingId" v-model="createForm.inn" placeholder="1234567890" />
              <input v-else v-model="editForm.inn" placeholder="1234567890" />
            </div>

            <!-- Ошибка -->
            <div v-if="formError" class="form-error">{{ formError }}</div>

            <div class="modal-actions">
              <button type="button" class="btn-secondary" @click="showModal = false">Отмена</button>
              <button type="submit" class="btn-primary" :disabled="submitting">
                {{ submitting ? 'Сохранение…' : editingId ? 'Сохранить' : 'Создать' }}
              </button>
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
.btn-primary:disabled { opacity: 0.6; cursor: not-allowed; transform: none; }

.btn-secondary {
  padding: 11px 20px; background: var(--bg-card); color: var(--text-secondary);
  border: 1px solid var(--border); border-radius: var(--radius);
  font-size: 0.85rem; font-weight: 500; cursor: pointer; transition: all var(--transition);
}
.btn-secondary:hover { background: var(--bg-hover); }

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

.td-id { color: var(--text-muted); font-size: 0.8rem; width: 50px; }
.td-name { font-weight: 500; }
.td-muted { color: var(--text-secondary); }
.td-actions { width: 100px; }
.td-actions { display: flex; gap: 4px; }

.login-badge {
  display: inline-flex; align-items: center; gap: 5px;
  padding: 3px 10px; border-radius: 20px;
  background: var(--primary-50); color: var(--primary);
  font-size: 0.8rem; font-weight: 500; border: 1px solid var(--primary-200);
}

.btn-icon {
  width: 34px; height: 34px; border-radius: var(--radius-sm);
  border: 1px solid var(--border-light); background: var(--bg-card);
  color: var(--text-muted); display: flex; align-items: center; justify-content: center;
  cursor: pointer; transition: all var(--transition);
}
.btn-icon:hover { color: var(--primary); border-color: var(--primary); background: var(--primary-50); }
.btn-icon:active { transform: scale(0.9); }
.btn-icon.danger:hover { color: var(--danger); border-color: var(--danger); background: rgba(239,68,68,0.06); }

/* ── Modal ─────────────────────────────────────── */
.modal-overlay {
  position: fixed; inset: 0; background: rgba(0,0,0,0.3); backdrop-filter: blur(4px);
  display: flex; align-items: center; justify-content: center; z-index: 1000;
  animation: fadeIn 0.2s ease;
}
.modal {
  background: var(--bg-card); border-radius: var(--radius-xl); padding: 28px;
  width: 100%; max-width: 520px; box-shadow: var(--shadow-xl); animation: scaleIn 0.25s ease;
  max-height: 90vh; overflow-y: auto;
}
.modal h2 { font-size: 1.15rem; font-weight: 700; color: var(--text); margin-bottom: 20px; }

.section-label {
  font-size: 0.7rem; font-weight: 700; color: var(--primary);
  text-transform: uppercase; letter-spacing: 0.8px;
  margin-bottom: 12px; padding-bottom: 6px;
  border-bottom: 1px solid var(--border-light);
}

.fields-row { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; }

.field { display: flex; flex-direction: column; gap: 6px; margin-bottom: 16px; }
.field label {
  font-size: 0.78rem; font-weight: 600; color: var(--text-secondary);
  text-transform: uppercase; letter-spacing: 0.5px;
}
.field input {
  padding: 11px 14px; border: 1px solid var(--border); border-radius: var(--radius);
  background: var(--bg-input); color: var(--text); font-size: 0.875rem;
  transition: all var(--transition); box-shadow: var(--shadow-inset); width: 100%; box-sizing: border-box;
}
.field input:focus { outline: none; border-color: var(--primary); box-shadow: 0 0 0 3px var(--primary-200), var(--shadow-inset); }
.field input::placeholder { color: var(--text-muted); }

.input-with-icon { position: relative; }
.input-with-icon input { padding-right: 42px; width: 100%; box-sizing: border-box; }
.toggle-pw {
  position: absolute; right: 10px; top: 50%; transform: translateY(-50%);
  background: none; border: none; color: var(--text-muted); cursor: pointer;
  display: flex; align-items: center; padding: 2px;
}
.toggle-pw:hover { color: var(--primary); }

.form-error {
  background: rgba(239,68,68,0.08); border: 1px solid rgba(239,68,68,0.3);
  color: var(--danger); border-radius: var(--radius); padding: 10px 14px;
  font-size: 0.85rem; margin-bottom: 12px;
}

.modal-actions { display: flex; gap: 10px; justify-content: flex-end; margin-top: 20px; }

.empty-state { text-align: center; padding: 40px 20px; color: var(--text-muted); font-size: 0.9rem; }
</style>
