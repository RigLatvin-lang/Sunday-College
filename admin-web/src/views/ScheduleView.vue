<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useScheduleStore } from '@/stores/schedule'
import { useGroupsStore } from '@/stores/groups'
import { useTeachersStore } from '@/stores/teachers'

const schedule = useScheduleStore()
const groupsStore = useGroupsStore()
const teachersStore = useTeachersStore()

const selectedDate = ref('')
const selectedGroupId = ref<number | undefined>()
const selectedTeacherId = ref<number | undefined>()

onMounted(() => {
  const today = new Date().toISOString().slice(0, 10)
  selectedDate.value = today
  groupsStore.fetchAll()
  teachersStore.fetchAll()
  loadSchedule()
})

function loadSchedule() {
  schedule.fetch({
    date: selectedDate.value || undefined,
    groupId: selectedGroupId.value || undefined,
    teacherId: selectedTeacherId.value || undefined,
  })
}

const grouped = computed(() => {
  const map = new Map<string, typeof schedule.items>()
  for (const item of schedule.items) {
    const date = item.dateTime.slice(0, 10)
    if (!map.has(date)) map.set(date, [])
    map.get(date)!.push(item)
  }
  return [...map.entries()].sort(([a], [b]) => a.localeCompare(b))
})

function formatDate(d: string) {
  return new Date(d).toLocaleDateString('ru-RU', {
    weekday: 'long',
    day: 'numeric',
    month: 'long',
  })
}

function formatTime(dt: string) {
  return new Date(dt).toLocaleTimeString('ru-RU', { hour: '2-digit', minute: '2-digit' })
}

function endTime(dt: string, dur: number) {
  const d = new Date(dt)
  d.setMinutes(d.getMinutes() + dur)
  return d.toLocaleTimeString('ru-RU', { hour: '2-digit', minute: '2-digit' })
}
</script>

<template>
  <div class="page">
    <div class="filters">
      <div class="filter-field">
        <label>Дата</label>
        <input type="date" v-model="selectedDate" @change="loadSchedule" />
      </div>
      <div class="filter-field">
        <label>Группа</label>
        <select v-model.number="selectedGroupId" @change="loadSchedule">
          <option :value="undefined">Все группы</option>
          <option v-for="g in groupsStore.items" :key="g.id" :value="g.id">{{ g.name }}</option>
        </select>
      </div>
      <div class="filter-field">
        <label>Преподаватель</label>
        <select v-model.number="selectedTeacherId" @change="loadSchedule">
          <option :value="undefined">Все преподаватели</option>
          <option v-for="t in teachersStore.items" :key="t.id" :value="t.id">{{ t.fullName }}</option>
        </select>
      </div>
      <button class="btn-outline" @click="selectedDate = ''; selectedGroupId = undefined; selectedTeacherId = undefined; loadSchedule()">
        Сбросить
      </button>
    </div>

    <div v-if="grouped.length" class="schedule-timeline">
      <div v-for="[date, items] in grouped" :key="date" class="day-block">
        <h3 class="day-title">{{ formatDate(date) }}</h3>
        <div class="day-lessons">
          <div v-for="(lesson, i) in items" :key="lesson.id" class="lesson-card" :style="{ animationDelay: `${i * 0.05}s` }">
            <div class="lesson-time">
              <span class="time-start">{{ formatTime(lesson.dateTime) }}</span>
              <span class="time-end">{{ endTime(lesson.dateTime, lesson.durationMinutes) }}</span>
            </div>
            <div class="lesson-divider"></div>
            <div class="lesson-info">
              <span class="lesson-subject">{{ lesson.subjectName }}</span>
              <div class="lesson-meta">
                <span class="lesson-badge">{{ lesson.groupName }}</span>
                <span v-if="lesson.teacherName" class="lesson-teacher">{{ lesson.teacherName }}</span>
                <span class="lesson-room">Ауд. {{ lesson.classroom }}</span>
                <span class="lesson-dur">{{ lesson.durationMinutes }} мин</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div v-else-if="!schedule.loading" class="empty-state">
      <p>Нет занятий по выбранным фильтрам</p>
    </div>
  </div>
</template>

<style scoped>
.page { animation: fadeIn 0.3s ease; }

.filters {
  display: flex; align-items: flex-end; gap: 14px; margin-bottom: 24px;
  flex-wrap: wrap;
}

.filter-field {
  display: flex; flex-direction: column; gap: 6px;
}

.filter-field label {
  font-size: 0.75rem; font-weight: 600; color: var(--text-muted);
  text-transform: uppercase; letter-spacing: 0.5px;
}

.filter-field input,
.filter-field select {
  padding: 10px 14px; border: 1px solid var(--border); border-radius: var(--radius);
  background: var(--bg-card); color: var(--text); font-size: 0.85rem;
  transition: all var(--transition); box-shadow: var(--shadow-neumorphic-sm);
  min-width: 180px;
}
.filter-field input:focus,
.filter-field select:focus {
  outline: none; border-color: var(--primary); box-shadow: 0 0 0 3px var(--primary-200);
}

.btn-outline {
  padding: 10px 18px; background: transparent; color: var(--text-secondary);
  border: 1px solid var(--border); border-radius: var(--radius);
  font-size: 0.85rem; font-weight: 500; cursor: pointer; transition: all var(--transition);
}
.btn-outline:hover { border-color: var(--primary); color: var(--primary); background: var(--primary-50); }
.btn-outline:active { transform: scale(0.97); }

/* Timeline */
.schedule-timeline {
  display: flex; flex-direction: column; gap: 28px;
}

.day-title {
  font-size: 1rem; font-weight: 600; color: var(--text);
  margin-bottom: 12px; text-transform: capitalize;
  padding-left: 4px;
}

.day-lessons {
  display: flex; flex-direction: column; gap: 10px;
}

.lesson-card {
  background: var(--bg-card); border-radius: var(--radius-lg); padding: 18px 22px;
  box-shadow: var(--shadow-neumorphic); display: flex; align-items: center; gap: 18px;
  animation: fadeIn 0.3s ease both; transition: all var(--transition);
  border: 1px solid var(--border-light);
}
.lesson-card:hover {
  transform: translateX(4px);
  box-shadow: var(--shadow-lg);
}

.lesson-time {
  display: flex; flex-direction: column; align-items: center; min-width: 56px;
}
.time-start { font-size: 0.95rem; font-weight: 700; color: var(--primary-dark); }
.time-end { font-size: 0.75rem; color: var(--text-muted); }

.lesson-divider {
  width: 3px; height: 36px; border-radius: 2px;
  background: linear-gradient(to bottom, var(--primary), var(--primary-light));
}

.lesson-info { flex: 1; }
.lesson-subject { font-size: 0.95rem; font-weight: 600; color: var(--text); }
.lesson-meta { display: flex; align-items: center; gap: 12px; margin-top: 4px; }

.lesson-badge {
  display: inline-block; padding: 3px 10px; border-radius: 20px;
  font-size: 0.73rem; font-weight: 500;
  background: var(--primary-50); color: var(--primary-dark);
}

.lesson-teacher {
  font-size: 0.78rem; color: var(--text-secondary); font-weight: 500;
}

.lesson-room, .lesson-dur {
  font-size: 0.78rem; color: var(--text-muted);
}

.empty-state {
  text-align: center; padding: 60px 20px; color: var(--text-muted); font-size: 0.9rem;
}
</style>
