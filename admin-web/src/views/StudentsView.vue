<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useStudentsStore } from '@/stores/students'
import type { StudentResponse, CreateStudentRequest, UpdateStudentRequest, StudentImportResult, StudentImportError } from '@/types'

const store = useStudentsStore()
const showModal = ref(false)
const editingId = ref<number | null>(null)
const search = ref('')
const submitting = ref(false)
const formError = ref<string | null>(null)
const showPassword = ref(false)

// ─── CSV Import ──────────────────────────────────────────────────────────────
const showImportModal = ref(false)
const importFile = ref<File | null>(null)
const importDragging = ref(false)
const importUploading = ref(false)
const importResult = ref<StudentImportResult | null>(null)
const importError = ref<string | null>(null)
const fileInputRef = ref<HTMLInputElement | null>(null)

function openImportModal() {
  importFile.value = null
  importResult.value = null
  importError.value = null
  importDragging.value = false
  showImportModal.value = true
}

function onFileDrop(e: DragEvent) {
  importDragging.value = false
  const file = e.dataTransfer?.files?.[0]
  if (file && file.name.endsWith('.csv')) {
    importFile.value = file
    importError.value = null
  } else {
    importError.value = 'Пожалуйста, загрузите файл в формате .csv'
  }
}

function onFileInput(e: Event) {
  const file = (e.target as HTMLInputElement).files?.[0]
  if (file) {
    importFile.value = file
    importError.value = null
    importResult.value = null
  }
}

async function handleImport() {
  if (!importFile.value) return
  importUploading.value = true
  importError.value = null
  importResult.value = null
  try {
    importResult.value = await store.importFromCsv(importFile.value)
  } catch (e: any) {
    importError.value = e?.response?.data?.error ?? 'Ошибка при загрузке файла'
  } finally {
    importUploading.value = false
  }
}

function closeImportModal() {
  showImportModal.value = false
  importFile.value = null
  importResult.value = null
  importError.value = null
}

// ─── Форма создания ──────────────────────────────────────────────────────────
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
      <button class="btn-outline-primary" @click="openImportModal">
        <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/><polyline points="17 8 12 3 7 8"/><line x1="12" y1="3" x2="12" y2="15"/></svg>
        Загрузить CSV
      </button>
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

    <!-- МОДАЛКА РУЧНОГО СОЗДАНИЯ/РЕДАКТИРОВАНИЯ -->
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
    <!-- МОДАЛКА CSV ИМПОРТА -->
    <Teleport to="body">
      <div v-if="showImportModal" class="modal-overlay" @click.self="closeImportModal">
        <div class="modal modal-import" @click.stop>
          <div class="modal-import-header">
            <h2>Импорт студентов из CSV</h2>
            <button class="modal-close" @click="closeImportModal">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
            </button>
          </div>

          <!-- Формат файла -->
          <div class="csv-hint">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>
            <span>Ожидаемый формат: <code>login,password,lastName,firstName,middleName,birthDate,inn,phone</code></span>
          </div>

          <!-- Зона перетаскивания / выбора файла -->
          <div
            v-if="!importResult"
            class="drop-zone"
            :class="{ 'drop-zone--over': importDragging, 'drop-zone--ready': !!importFile }"
            @dragover.prevent="importDragging = true"
            @dragleave.prevent="importDragging = false"
            @drop.prevent="onFileDrop"
            @click="fileInputRef?.click()"
          >
            <input
              ref="fileInputRef"
              type="file"
              accept=".csv"
              class="file-input-hidden"
              @change="onFileInput"
            />
            <template v-if="!importFile">
              <svg width="36" height="36" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" class="drop-icon"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/><polyline points="17 8 12 3 7 8"/><line x1="12" y1="3" x2="12" y2="15"/></svg>
              <p class="drop-text">Перетащите CSV-файл сюда<br /><span>или нажмите для выбора</span></p>
            </template>
            <template v-else>
              <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" class="drop-icon drop-icon--ready"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/><polyline points="10 9 9 9 8 9"/></svg>
              <p class="drop-text drop-file-name">{{ importFile.name }}<br /><span>{{ (importFile.size / 1024).toFixed(1) }} KB · Нажмите для замены</span></p>
            </template>
          </div>

          <!-- Ошибка загрузки -->
          <div v-if="importError" class="form-error">{{ importError }}</div>

          <!-- Результат импорта -->
          <div v-if="importResult" class="import-result">
            <div class="import-summary">
              <div class="summary-badge summary-badge--success">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polyline points="20 6 9 17 4 12"/></svg>
                Создано: {{ importResult.created.length }}
              </div>
              <div v-if="importResult.errors.length" class="summary-badge summary-badge--error">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
                Ошибок: {{ importResult.errors.length }}
              </div>
            </div>

            <div v-if="importResult.errors.length" class="errors-table-wrap">
              <p class="errors-label">Строки с ошибками:</p>
              <table class="errors-table">
                <thead>
                  <tr><th>Строка</th><th>Причина</th></tr>
                </thead>
                <tbody>
                  <tr v-for="err in importResult.errors" :key="err.lineNumber">
                    <td class="err-line">{{ err.lineNumber === 0 ? 'файл' : err.lineNumber }}</td>
                    <td>{{ err.reason }}</td>
                  </tr>
                </tbody>
              </table>
            </div>

            <div v-if="importResult.errors.length === 0" class="import-all-ok">
              Все строки успешно обработаны 🎉
            </div>
          </div>

          <!-- Кнопки -->
          <div class="modal-actions">
            <button class="btn-secondary" @click="closeImportModal">
              {{ importResult ? 'Закрыть' : 'Отмена' }}
            </button>
            <button
              v-if="!importResult"
              class="btn-primary"
              :disabled="!importFile || importUploading"
              @click="handleImport"
            >
              {{ importUploading ? 'Загрузка…' : 'Импортировать' }}
            </button>
            <button
              v-else
              class="btn-outline-primary"
              @click="() => { importResult = null; importFile = null }"
            >
              Загрузить ещё
            </button>
          </div>
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

/* ── CSV Import Button ────────────────────────────────── */
.btn-outline-primary {
  display: inline-flex; align-items: center; gap: 7px;
  padding: 11px 18px;
  background: transparent;
  color: var(--primary); border: 1.5px solid var(--primary);
  border-radius: var(--radius); font-size: 0.85rem; font-weight: 600;
  cursor: pointer; transition: all var(--transition); white-space: nowrap;
}
.btn-outline-primary:hover {
  background: var(--primary-50);
  box-shadow: 0 0 0 3px var(--primary-200);
}
.btn-outline-primary:active { transform: scale(0.97); }

/* ── Import Modal ─────────────────────────────────────── */
.modal-import { max-width: 580px; }

.modal-import-header {
  display: flex; align-items: center; justify-content: space-between;
  margin-bottom: 14px;
}
.modal-import-header h2 { margin: 0; }

.modal-close {
  background: none; border: none; color: var(--text-muted);
  cursor: pointer; padding: 4px; display: flex; align-items: center;
  border-radius: var(--radius-sm); transition: all var(--transition);
}
.modal-close:hover { color: var(--danger); background: rgba(239,68,68,0.07); }

.csv-hint {
  display: flex; align-items: flex-start; gap: 8px;
  background: var(--bg-input); border: 1px solid var(--border-light);
  border-radius: var(--radius); padding: 10px 14px;
  font-size: 0.8rem; color: var(--text-secondary); margin-bottom: 18px;
}
.csv-hint code {
  font-family: 'JetBrains Mono', monospace; font-size: 0.75rem;
  color: var(--primary); background: var(--primary-50);
  padding: 1px 4px; border-radius: 3px;
}
.csv-hint svg { flex-shrink: 0; margin-top: 1px; color: var(--primary); }

/* Drop zone */
.drop-zone {
  border: 2px dashed var(--border);
  border-radius: var(--radius-lg);
  padding: 32px 24px;
  display: flex; flex-direction: column; align-items: center; gap: 12px;
  cursor: pointer; transition: all var(--transition);
  background: var(--bg-input); margin-bottom: 18px; position: relative;
}
.drop-zone:hover, .drop-zone--over {
  border-color: var(--primary);
  background: var(--primary-50);
}
.drop-zone--ready {
  border-color: var(--primary); border-style: solid;
  background: var(--primary-50);
}
.file-input-hidden {
  position: absolute; inset: 0; opacity: 0; cursor: pointer; width: 100%; height: 100%;
}
.drop-icon { color: var(--text-muted); }
.drop-icon--ready { color: var(--primary); }
.drop-text {
  text-align: center; font-size: 0.9rem; color: var(--text-secondary);
  margin: 0; line-height: 1.5;
}
.drop-text span { font-size: 0.8rem; color: var(--text-muted); }
.drop-file-name { color: var(--primary); font-weight: 600; }

/* Import result */
.import-result { margin-bottom: 18px; }

.import-summary {
  display: flex; gap: 10px; margin-bottom: 16px; flex-wrap: wrap;
}
.summary-badge {
  display: inline-flex; align-items: center; gap: 6px;
  padding: 8px 16px; border-radius: 20px;
  font-size: 0.875rem; font-weight: 600;
}
.summary-badge--success {
  background: rgba(34,197,94,0.1); color: #16a34a;
  border: 1px solid rgba(34,197,94,0.25);
}
.summary-badge--error {
  background: rgba(239,68,68,0.08); color: var(--danger);
  border: 1px solid rgba(239,68,68,0.2);
}

.errors-label {
  font-size: 0.78rem; font-weight: 700; color: var(--text-muted);
  text-transform: uppercase; letter-spacing: 0.5px; margin-bottom: 8px;
}
.errors-table-wrap { max-height: 220px; overflow-y: auto; border-radius: var(--radius); border: 1px solid var(--border-light); }
.errors-table { width: 100%; border-collapse: collapse; font-size: 0.825rem; }
.errors-table th {
  text-align: left; padding: 9px 14px; background: var(--bg-input);
  color: var(--text-muted); font-size: 0.72rem; text-transform: uppercase;
  letter-spacing: 0.4px; border-bottom: 1px solid var(--border-light);
  position: sticky; top: 0;
}
.errors-table td { padding: 9px 14px; border-bottom: 1px solid var(--border-light); color: var(--text-secondary); }
.errors-table tr:last-child td { border-bottom: none; }
.err-line { font-weight: 700; color: var(--danger); width: 60px; }

.import-all-ok {
  text-align: center; padding: 24px;
  background: rgba(34,197,94,0.07); border: 1px solid rgba(34,197,94,0.2);
  border-radius: var(--radius); color: #15803d; font-weight: 500; font-size: 0.9rem;
}
</style>
