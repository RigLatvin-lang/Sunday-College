<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useLessonsStore } from '@/stores/lessons'
import { useSubjectsStore } from '@/stores/subjects'
import { useGroupsStore } from '@/stores/groups'
import { useTeachersStore } from '@/stores/teachers'

const lessons = useLessonsStore()
const subjects = useSubjectsStore()
const groups = useGroupsStore()
const teachers = useTeachersStore()

const showModal = ref(false)
const search = ref('')
const form = ref({
  subjectId: 0,
  groupId: 0,
  teacherId: 0 as number | undefined,
  dateTime: '',
  classroom: '',
  durationMinutes: 90,
})

onMounted(() => {
  lessons.fetchAll()
  subjects.fetchAll()
  groups.fetchAll()
  teachers.fetchAll()
})

const filtered = computed(() => {
  if (!search.value) return lessons.items
  const q = search.value.toLowerCase()
  return lessons.items.filter(
    (l) =>
      l.subjectName.toLowerCase().includes(q) ||
      l.groupName.toLowerCase().includes(q) ||
      l.classroom.toLowerCase().includes(q) ||
      (l.teacherName && l.teacherName.toLowerCase().includes(q)),
  )
})

function openCreate() {
  form.value = {
    subjectId: subjects.items[0]?.id || 0,
    groupId: groups.items[0]?.id || 0,
    teacherId: undefined,
    dateTime: '',
    classroom: '',
    durationMinutes: 90,
  }
  showModal.value = true
}

async function handleSubmit() {
  await lessons.create({
    ...form.value,
    teacherId: form.value.teacherId || undefined,
    durationMinutes: form.value.durationMinutes || undefined,
  })
  showModal.value = false
}

async function handleDelete(id: number) {
  if (confirm('Удалить занятие?')) await lessons.remove(id)
}

function formatDateTime(dt: string) {
  return new Date(dt).toLocaleString('ru-RU', {
    day: 'numeric',
    month: 'short',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  })
}
</script>

<template>
  <div class="page">
    <div class="page-header">
      <input v-model="search" class="search-input" placeholder="Поиск занятий..." />
      <button class="btn-primary" @click="openCreate">+ Новое занятие</button>
    </div>

    <div class="table-card">
      <table class="data-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Предмет</th>
            <th>Группа</th>
            <th>Преподаватель</th>
            <th>Дата и время</th>
            <th>Аудитория</th>
            <th>Длит. (мин)</th>
            <th>Действия</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="l in filtered" :key="l.id">
            <td class="td-id">{{ l.id }}</td>
            <td class="td-name">{{ l.subjectName }}</td>
            <td>
              <span class="badge">{{ l.groupName }}</span>
            </td>
            <td>{{ l.teacherName || '—' }}</td>
            <td class="td-muted">{{ formatDateTime(l.dateTime) }}</td>
            <td>{{ l.classroom }}</td>
            <td class="td-muted">{{ l.durationMinutes }}</td>
            <td class="td-actions">
              <button class="btn-icon danger" title="Удалить" @click="handleDelete(l.id)">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/></svg>
              </button>
            </td>
          </tr>
        </tbody>
      </table>
      <div v-if="!filtered.length && !lessons.loading" class="empty-state">Нет занятий</div>
    </div>

    <Teleport to="body">
      <div v-if="showModal" class="modal-overlay" @click.self="showModal = false">
        <div class="modal" @click.stop>
          <h2>Новое занятие</h2>
          <form @submit.prevent="handleSubmit">
            <div class="field">
              <label>Предмет</label>
              <select v-model.number="form.subjectId" required>
                <option v-for="s in subjects.items" :key="s.id" :value="s.id">{{ s.name }}</option>
              </select>
            </div>
            <div class="field">
              <label>Группа</label>
              <select v-model.number="form.groupId" required>
                <option v-for="g in groups.items" :key="g.id" :value="g.id">{{ g.name }}</option>
              </select>
            </div>
            <div class="field">
              <label>Преподаватель</label>
              <select v-model.number="form.teacherId">
                <option :value="undefined">Не указан</option>
                <option v-for="t in teachers.items" :key="t.id" :value="t.id">{{ t.fullName }}</option>
              </select>
            </div>
            <div class="fields-row">
              <div class="field">
                <label>Дата и время</label>
                <input v-model="form.dateTime" type="datetime-local" required />
              </div>
              <div class="field">
                <label>Аудитория</label>
                <input v-model="form.classroom" required placeholder="301" />
              </div>
            </div>
            <div class="field">
              <label>Длительность (минуты)</label>
              <input v-model.number="form.durationMinutes" type="number" min="15" max="480" />
            </div>
            <div class="modal-actions">
              <button type="button" class="btn-secondary" @click="showModal = false">Отмена</button>
              <button type="submit" class="btn-primary">Создать</button>
            </div>
          </form>
        </div>
      </div>
    </Teleport>
  </div>
</template>

<style scoped>
.page { animation: fadeIn 0.3s ease; }

.page-header { display: flex; align-items: center; gap: 12px; margin-bottom: 20px; }

.search-input {
  flex: 1; padding: 11px 16px; border: 1px solid var(--border); border-radius: var(--radius);
  background: var(--bg-card); color: var(--text); font-size: 0.875rem;
  transition: all var(--transition); box-shadow: var(--shadow-neumorphic-sm);
}
.search-input:focus { outline: none; border-color: var(--primary); box-shadow: 0 0 0 3px var(--primary-200); }
.search-input::placeholder { color: var(--text-muted); }

.btn-primary {
  padding: 11px 20px; background: linear-gradient(135deg, var(--primary), var(--primary-dark));
  color: white; border: none; border-radius: var(--radius); font-size: 0.85rem;
  font-weight: 600; cursor: pointer; transition: all var(--transition);
  box-shadow: 0 3px 10px rgba(0,188,212,0.25); white-space: nowrap;
}
.btn-primary:hover { transform: translateY(-1px); box-shadow: 0 5px 16px rgba(0,188,212,0.35); }
.btn-primary:active { transform: scale(0.97); }

.btn-secondary {
  padding: 11px 20px; background: var(--bg-card); color: var(--text-secondary);
  border: 1px solid var(--border); border-radius: var(--radius); font-size: 0.85rem;
  font-weight: 500; cursor: pointer; transition: all var(--transition);
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
.td-actions { width: 60px; }

.badge {
  display: inline-block; padding: 4px 10px; border-radius: 20px;
  font-size: 0.78rem; font-weight: 500;
  background: var(--primary-50); color: var(--primary-dark);
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

.modal-overlay {
  position: fixed; inset: 0; background: rgba(0,0,0,0.3); backdrop-filter: blur(4px);
  display: flex; align-items: center; justify-content: center; z-index: 1000; animation: fadeIn 0.2s ease;
}
.modal {
  background: var(--bg-card); border-radius: var(--radius-xl); padding: 28px;
  width: 100%; max-width: 500px; box-shadow: var(--shadow-xl); animation: scaleIn 0.25s ease;
}
.modal h2 { font-size: 1.15rem; font-weight: 700; color: var(--text); margin-bottom: 20px; }

.fields-row { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; }

.field { display: flex; flex-direction: column; gap: 6px; margin-bottom: 16px; }
.field label { font-size: 0.78rem; font-weight: 600; color: var(--text-secondary); text-transform: uppercase; letter-spacing: 0.5px; }
.field input,
.field select {
  padding: 11px 14px; border: 1px solid var(--border); border-radius: var(--radius);
  background: var(--bg-input); color: var(--text); font-size: 0.875rem;
  transition: all var(--transition); box-shadow: var(--shadow-inset);
}
.field input:focus,
.field select:focus { outline: none; border-color: var(--primary); box-shadow: 0 0 0 3px var(--primary-200), var(--shadow-inset); }
.field input::placeholder { color: var(--text-muted); }

.modal-actions { display: flex; gap: 10px; justify-content: flex-end; margin-top: 20px; }

.empty-state { text-align: center; padding: 40px 20px; color: var(--text-muted); font-size: 0.9rem; }
</style>
