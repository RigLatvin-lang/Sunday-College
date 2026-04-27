<script setup lang="ts">
import { onMounted, computed } from 'vue'
import { Bar, Doughnut } from 'vue-chartjs'
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  ArcElement,
  Title,
  Tooltip,
  Legend,
} from 'chart.js'
import { useStudentsStore } from '@/stores/students'
import { useTeachersStore } from '@/stores/teachers'
import { useGroupsStore } from '@/stores/groups'
import { useSubjectsStore } from '@/stores/subjects'
import { useLessonsStore } from '@/stores/lessons'

ChartJS.register(CategoryScale, LinearScale, BarElement, ArcElement, Title, Tooltip, Legend)

const students = useStudentsStore()
const teachers = useTeachersStore()
const groups = useGroupsStore()
const subjects = useSubjectsStore()
const lessons = useLessonsStore()

onMounted(() => {
  students.fetchAll()
  teachers.fetchAll()
  groups.fetchAll()
  subjects.fetchAll()
  lessons.fetchAll()
})

const stats = computed(() => [
  { label: 'Студентов', value: students.items.length, icon: 'user', color: '#00bcd4' },
  { label: 'Преподавателей', value: teachers.items.length, icon: 'briefcase', color: '#3b82f6' },
  { label: 'Групп', value: groups.items.length, icon: 'users', color: '#10b981' },
  { label: 'Предметов', value: subjects.items.length, icon: 'book', color: '#f59e0b' },
  { label: 'Занятий', value: lessons.items.length, icon: 'calendar', color: '#8b5cf6' },
])

const groupChartData = computed(() => ({
  labels: groups.items.map((g) => g.name),
  datasets: [
    {
      label: 'Студентов в группе',
      data: groups.items.map((g) => g.students.length),
      backgroundColor: [
        'rgba(0, 188, 212, 0.7)',
        'rgba(59, 130, 246, 0.7)',
        'rgba(16, 185, 129, 0.7)',
        'rgba(245, 158, 11, 0.7)',
        'rgba(139, 92, 246, 0.7)',
        'rgba(236, 72, 153, 0.7)',
      ],
      borderRadius: 8,
      borderSkipped: false,
    },
  ],
}))

const subjectChartData = computed(() => {
  const subjectCounts: Record<string, number> = {}
  for (const l of lessons.items) {
    subjectCounts[l.subjectName] = (subjectCounts[l.subjectName] || 0) + 1
  }
  return {
    labels: Object.keys(subjectCounts),
    datasets: [
      {
        data: Object.values(subjectCounts),
        backgroundColor: [
          'rgba(0, 188, 212, 0.8)',
          'rgba(59, 130, 246, 0.8)',
          'rgba(16, 185, 129, 0.8)',
          'rgba(245, 158, 11, 0.8)',
          'rgba(139, 92, 246, 0.8)',
          'rgba(236, 72, 153, 0.8)',
          'rgba(20, 184, 166, 0.8)',
          'rgba(249, 115, 22, 0.8)',
        ],
        borderWidth: 0,
      },
    ],
  }
})

const barOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: { display: false },
    tooltip: {
      backgroundColor: '#1a2332',
      titleFont: { family: 'Inter' },
      bodyFont: { family: 'Inter' },
      cornerRadius: 8,
      padding: 12,
    },
  },
  scales: {
    x: {
      grid: { display: false },
      ticks: { font: { family: 'Inter', size: 12 }, color: '#94a3b8' },
    },
    y: {
      grid: { color: '#f1f5f9' },
      ticks: { font: { family: 'Inter', size: 12 }, color: '#94a3b8', stepSize: 1 },
    },
  },
}

const doughnutOptions = {
  responsive: true,
  maintainAspectRatio: false,
  cutout: '65%',
  plugins: {
    legend: {
      position: 'bottom' as const,
      labels: { font: { family: 'Inter', size: 12 }, color: '#64748b', padding: 16, usePointStyle: true },
    },
    tooltip: {
      backgroundColor: '#1a2332',
      titleFont: { family: 'Inter' },
      bodyFont: { family: 'Inter' },
      cornerRadius: 8,
      padding: 12,
    },
  },
}

const recentLessons = computed(() =>
  [...lessons.items]
    .sort((a, b) => new Date(b.dateTime).getTime() - new Date(a.dateTime).getTime())
    .slice(0, 6),
)

function formatDate(dt: string) {
  return new Date(dt).toLocaleDateString('ru-RU', {
    day: 'numeric',
    month: 'short',
    hour: '2-digit',
    minute: '2-digit',
  })
}
</script>

<template>
  <div class="dashboard">
    <!-- Stats Cards -->
    <div class="stats-grid">
      <div v-for="(stat, i) in stats" :key="stat.label" class="stat-card" :style="{ animationDelay: `${i * 0.06}s` }">
        <div class="stat-icon" :style="{ background: stat.color + '15', color: stat.color }">
          <svg v-if="stat.icon === 'user'" width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/>
          </svg>
          <svg v-else-if="stat.icon === 'briefcase'" width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <rect x="2" y="7" width="20" height="14" rx="2" ry="2"/><path d="M16 21V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v16"/>
          </svg>
          <svg v-else-if="stat.icon === 'users'" width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/>
          </svg>
          <svg v-else-if="stat.icon === 'book'" width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20"/><path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z"/>
          </svg>
          <svg v-else-if="stat.icon === 'calendar'" width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <rect x="3" y="4" width="18" height="18" rx="2" ry="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/>
          </svg>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ stat.value }}</span>
          <span class="stat-label">{{ stat.label }}</span>
        </div>
      </div>
    </div>

    <!-- Charts -->
    <div class="charts-grid">
      <div class="chart-card" v-if="groups.items.length">
        <h3>Студенты по группам</h3>
        <div class="chart-container">
          <Bar :data="groupChartData" :options="barOptions" />
        </div>
      </div>

      <div class="chart-card" v-if="lessons.items.length">
        <h3>Занятия по предметам</h3>
        <div class="chart-container">
          <Doughnut :data="subjectChartData" :options="doughnutOptions" />
        </div>
      </div>
    </div>

    <!-- Recent lessons -->
    <div class="recent-card" v-if="recentLessons.length">
      <h3>Последние занятия</h3>
      <div class="recent-list">
        <div v-for="lesson in recentLessons" :key="lesson.id" class="recent-item">
          <div class="recent-dot"></div>
          <div class="recent-info">
            <span class="recent-title">{{ lesson.subjectName }}</span>
            <span class="recent-meta">{{ lesson.groupName }} &middot; Аудитория {{ lesson.classroom }}</span>
          </div>
          <span class="recent-date">{{ formatDate(lesson.dateTime) }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.dashboard {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* Stats */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
}

.stat-card {
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  padding: 22px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: var(--shadow-neumorphic);
  animation: fadeIn 0.4s ease both;
  transition: all var(--transition);
  cursor: default;
  border: 1px solid var(--border-light);
}

.stat-card:hover {
  transform: translateY(-3px);
  box-shadow: var(--shadow-xl);
}

.stat-card:active {
  transform: translateY(-1px);
  box-shadow: var(--shadow-neumorphic-pressed);
}

.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: var(--radius);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-info {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 1.6rem;
  font-weight: 700;
  color: var(--text);
  line-height: 1.2;
}

.stat-label {
  font-size: 0.8rem;
  color: var(--text-muted);
  font-weight: 500;
}

/* Charts */
.charts-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.chart-card {
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  padding: 24px;
  box-shadow: var(--shadow-neumorphic);
  animation: fadeIn 0.5s ease both;
  border: 1px solid var(--border-light);
}

.chart-card h3 {
  font-size: 1rem;
  font-weight: 600;
  color: var(--text);
  margin-bottom: 20px;
}

.chart-container {
  height: 280px;
  position: relative;
}

/* Recent */
.recent-card {
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  padding: 24px;
  box-shadow: var(--shadow-neumorphic);
  animation: fadeIn 0.5s ease both;
  animation-delay: 0.15s;
  border: 1px solid var(--border-light);
}

.recent-card h3 {
  font-size: 1rem;
  font-weight: 600;
  color: var(--text);
  margin-bottom: 16px;
}

.recent-list {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.recent-item {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 12px 14px;
  border-radius: var(--radius-sm);
  transition: all var(--transition);
}

.recent-item:hover {
  background: var(--primary-50);
}

.recent-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: var(--primary);
  flex-shrink: 0;
}

.recent-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.recent-title {
  font-size: 0.875rem;
  font-weight: 600;
  color: var(--text);
}

.recent-meta {
  font-size: 0.75rem;
  color: var(--text-muted);
}

.recent-date {
  font-size: 0.78rem;
  color: var(--text-secondary);
  white-space: nowrap;
}

@media (max-width: 900px) {
  .charts-grid {
    grid-template-columns: 1fr;
  }
}
</style>
