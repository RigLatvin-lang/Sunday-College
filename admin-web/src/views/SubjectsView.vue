<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useSubjectsStore } from '@/stores/subjects'
import type { SubjectResponse } from '@/types'

const store = useSubjectsStore()
const showModal = ref(false)
const editingId = ref<number | null>(null)
const form = ref({ name: '' })
const search = ref('')

onMounted(() => store.fetchAll())

const filtered = computed(() => {
  if (!search.value) return store.items
  const q = search.value.toLowerCase()
  return store.items.filter((s) => s.name.toLowerCase().includes(q))
})

function openCreate() {
  editingId.value = null
  form.value = { name: '' }
  showModal.value = true
}

function openEdit(s: SubjectResponse) {
  editingId.value = s.id
  form.value = { name: s.name }
  showModal.value = true
}

async function handleSubmit() {
  if (editingId.value) {
    await store.update(editingId.value, form.value)
  } else {
    await store.create(form.value)
  }
  showModal.value = false
}

async function handleDelete(id: number) {
  if (confirm('Удалить предмет?')) await store.remove(id)
}
</script>

<template>
  <div class="page">
    <div class="page-header">
      <input v-model="search" class="search-input" placeholder="Поиск предметов..." />
      <button class="btn-primary" @click="openCreate">+ Новый предмет</button>
    </div>

    <div class="cards-grid">
      <div v-for="(subject, i) in filtered" :key="subject.id" class="entity-card" :style="{ animationDelay: `${i * 0.04}s` }">
        <div class="entity-header">
          <div class="entity-icon subject-icon">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20"/>
              <path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z"/>
            </svg>
          </div>
          <div class="entity-title-block">
            <h3>{{ subject.name }}</h3>
            <span class="entity-subtitle">ID: {{ subject.id }}</span>
          </div>
        </div>
        <div class="entity-actions">
          <button class="btn-icon" title="Редактировать" @click="openEdit(subject)">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/></svg>
          </button>
          <button class="btn-icon danger" title="Удалить" @click="handleDelete(subject.id)">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/></svg>
          </button>
        </div>
      </div>
    </div>

    <div v-if="!filtered.length && !store.loading" class="empty-state">Нет предметов</div>

    <Teleport to="body">
      <div v-if="showModal" class="modal-overlay" @click.self="showModal = false">
        <div class="modal" @click.stop>
          <h2>{{ editingId ? 'Редактировать предмет' : 'Новый предмет' }}</h2>
          <form @submit.prevent="handleSubmit">
            <div class="field">
              <label>Название</label>
              <input v-model="form.name" required placeholder="Математика" />
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

.cards-grid {
  display: grid; grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); gap: 14px;
}

.entity-card {
  background: var(--bg-card); border-radius: var(--radius-lg); padding: 20px;
  box-shadow: var(--shadow-neumorphic); display: flex; align-items: center;
  justify-content: space-between; gap: 12px; animation: fadeIn 0.35s ease both;
  transition: all var(--transition); border: 1px solid var(--border-light);
}
.entity-card:hover { transform: translateY(-2px); box-shadow: var(--shadow-lg); }

.entity-header { display: flex; align-items: center; gap: 14px; flex: 1; min-width: 0; }

.entity-icon {
  width: 42px; height: 42px; border-radius: var(--radius);
  display: flex; align-items: center; justify-content: center; flex-shrink: 0;
}
.subject-icon { background: rgba(245, 158, 11, 0.12); color: #f59e0b; }

.entity-title-block { min-width: 0; }
.entity-title-block h3 {
  font-size: 0.95rem; font-weight: 600; color: var(--text);
  white-space: nowrap; overflow: hidden; text-overflow: ellipsis;
}
.entity-subtitle { font-size: 0.75rem; color: var(--text-muted); }

.entity-actions { display: flex; gap: 4px; flex-shrink: 0; }

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
  width: 100%; max-width: 440px; box-shadow: var(--shadow-xl); animation: scaleIn 0.25s ease;
}
.modal h2 { font-size: 1.15rem; font-weight: 700; color: var(--text); margin-bottom: 20px; }

.field { display: flex; flex-direction: column; gap: 6px; margin-bottom: 16px; }
.field label { font-size: 0.78rem; font-weight: 600; color: var(--text-secondary); text-transform: uppercase; letter-spacing: 0.5px; }
.field input {
  padding: 11px 14px; border: 1px solid var(--border); border-radius: var(--radius);
  background: var(--bg-input); color: var(--text); font-size: 0.875rem;
  transition: all var(--transition); box-shadow: var(--shadow-inset);
}
.field input:focus { outline: none; border-color: var(--primary); box-shadow: 0 0 0 3px var(--primary-200), var(--shadow-inset); }
.field input::placeholder { color: var(--text-muted); }

.modal-actions { display: flex; gap: 10px; justify-content: flex-end; margin-top: 20px; }

.empty-state { text-align: center; padding: 60px 20px; color: var(--text-muted); font-size: 0.9rem; }
</style>
