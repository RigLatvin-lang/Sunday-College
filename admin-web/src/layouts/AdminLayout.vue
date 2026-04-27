<script setup lang="ts">
import { useAuthStore } from '@/stores/auth'
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()

const navItems = [
	{ name: 'dashboard', label: 'Дашборд', icon: 'grid' },
	{ name: 'groups', label: 'Группы', icon: 'users' },
	{ name: 'students', label: 'Студенты', icon: 'user' },
	{ name: 'teachers', label: 'Преподаватели', icon: 'briefcase' },
	{ name: 'subjects', label: 'Предметы', icon: 'book' },
	{ name: 'lessons', label: 'Занятия', icon: 'calendar' },
	{ name: 'schedule', label: 'Расписание', icon: 'clock' },
	{ name: 'parents', label: 'Родители', icon: 'heart' }, // ← НОВОЕ
	{ name: 'admin', label: 'Администраторы', icon: 'shield' },
]

const currentTitle = computed(() => {
	const item = navItems.find(i => i.name === route.name)
	return item?.label || 'Панель управления'
})

function handleLogout() {
	auth.logout()
	router.push('/login')
}
</script>

<template>
	<div class="layout">
		<aside class="sidebar">
			<div class="sidebar-header">
				<div class="logo">
					<div class="logo-icon">
						<svg
							width="28"
							height="28"
							viewBox="0 0 24 24"
							fill="none"
							stroke="currentColor"
							stroke-width="2"
							stroke-linecap="round"
							stroke-linejoin="round"
						>
							<path d="M2 3h6a4 4 0 0 1 4 4v14a3 3 0 0 0-3-3H2z" />
							<path d="M22 3h-6a4 4 0 0 0-4 4v14a3 3 0 0 1 3-3h7z" />
						</svg>
					</div>
					<div class="logo-text">
						<span class="logo-title">Колледж</span>
						<span class="logo-subtitle">Панель управления</span>
					</div>
				</div>
			</div>

			<nav class="nav">
				<router-link
					v-for="item in navItems"
					:key="item.name"
					:to="{ name: item.name }"
					class="nav-item"
					:class="{ active: route.name === item.name }"
				>
					<span class="nav-icon">
						<!-- Grid / Dashboard -->
						<svg
							v-if="item.icon === 'grid'"
							width="20"
							height="20"
							viewBox="0 0 24 24"
							fill="none"
							stroke="currentColor"
							stroke-width="2"
							stroke-linecap="round"
							stroke-linejoin="round"
						>
							<rect x="3" y="3" width="7" height="7" rx="1" />
							<rect x="14" y="3" width="7" height="7" rx="1" />
							<rect x="3" y="14" width="7" height="7" rx="1" />
							<rect x="14" y="14" width="7" height="7" rx="1" />
						</svg>
						<!-- Users -->
						<svg
							v-else-if="item.icon === 'users'"
							width="20"
							height="20"
							viewBox="0 0 24 24"
							fill="none"
							stroke="currentColor"
							stroke-width="2"
							stroke-linecap="round"
							stroke-linejoin="round"
						>
							<path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2" />
							<circle cx="9" cy="7" r="4" />
							<path d="M23 21v-2a4 4 0 0 0-3-3.87" />
							<path d="M16 3.13a4 4 0 0 1 0 7.75" />
						</svg>
						<!-- User -->
						<svg
							v-else-if="item.icon === 'user'"
							width="20"
							height="20"
							viewBox="0 0 24 24"
							fill="none"
							stroke="currentColor"
							stroke-width="2"
							stroke-linecap="round"
							stroke-linejoin="round"
						>
							<path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" />
							<circle cx="12" cy="7" r="4" />
						</svg>
						<!-- Briefcase -->
						<svg
							v-else-if="item.icon === 'briefcase'"
							width="20"
							height="20"
							viewBox="0 0 24 24"
							fill="none"
							stroke="currentColor"
							stroke-width="2"
							stroke-linecap="round"
							stroke-linejoin="round"
						>
							<rect x="2" y="7" width="20" height="14" rx="2" ry="2" />
							<path d="M16 21V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v16" />
						</svg>
						<!-- Book -->
						<svg
							v-else-if="item.icon === 'book'"
							width="20"
							height="20"
							viewBox="0 0 24 24"
							fill="none"
							stroke="currentColor"
							stroke-width="2"
							stroke-linecap="round"
							stroke-linejoin="round"
						>
							<path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20" />
							<path
								d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z"
							/>
						</svg>
						<!-- Calendar -->
						<svg
							v-else-if="item.icon === 'calendar'"
							width="20"
							height="20"
							viewBox="0 0 24 24"
							fill="none"
							stroke="currentColor"
							stroke-width="2"
							stroke-linecap="round"
							stroke-linejoin="round"
						>
							<rect x="3" y="4" width="18" height="18" rx="2" ry="2" />
							<line x1="16" y1="2" x2="16" y2="6" />
							<line x1="8" y1="2" x2="8" y2="6" />
							<line x1="3" y1="10" x2="21" y2="10" />
						</svg>
						<!-- Clock -->
						<svg
							v-else-if="item.icon === 'clock'"
							width="20"
							height="20"
							viewBox="0 0 24 24"
							fill="none"
							stroke="currentColor"
							stroke-width="2"
							stroke-linecap="round"
							stroke-linejoin="round"
						>
							<circle cx="12" cy="12" r="10" />
							<polyline points="12 6 12 12 16 14" />
						</svg>
						<!-- Heart / Parents -->
						<svg
							v-else-if="item.icon === 'heart'"
							width="20"
							height="20"
							viewBox="0 0 24 24"
							fill="none"
							stroke="currentColor"
							stroke-width="2"
							stroke-linecap="round"
							stroke-linejoin="round"
						>
							<path
								d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"
							/>
						</svg>
						<!-- Shield -->
						<svg
							v-else-if="item.icon === 'shield'"
							width="20"
							height="20"
							viewBox="0 0 24 24"
							fill="none"
							stroke="currentColor"
							stroke-width="2"
							stroke-linecap="round"
							stroke-linejoin="round"
						>
							<path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z" />
						</svg>
					</span>
					<span class="nav-label">{{ item.label }}</span>
				</router-link>
			</nav>

			<div class="sidebar-footer">
				<div class="user-info">
					<div class="user-avatar">{{ auth.fullName?.charAt(0) || 'A' }}</div>
					<div class="user-details">
						<span class="user-name">{{ auth.fullName || 'Админ' }}</span>
						<span class="user-role">Администратор</span>
					</div>
				</div>
				<button class="logout-btn" @click="handleLogout" title="Выйти">
					<svg
						width="18"
						height="18"
						viewBox="0 0 24 24"
						fill="none"
						stroke="currentColor"
						stroke-width="2"
						stroke-linecap="round"
						stroke-linejoin="round"
					>
						<path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4" />
						<polyline points="16 17 21 12 16 7" />
						<line x1="21" y1="12" x2="9" y2="12" />
					</svg>
				</button>
			</div>
		</aside>

		<main class="main">
			<header class="topbar">
				<h1 class="page-title">{{ currentTitle }}</h1>
			</header>
			<div class="content">
				<router-view v-slot="{ Component }">
					<transition name="fade" mode="out-in">
						<component :is="Component" />
					</transition>
				</router-view>
			</div>
		</main>
	</div>
</template>

<style scoped>
.layout {
	display: flex;
	min-height: 100vh;
}

/* Sidebar */
.sidebar {
	width: 260px;
	background: var(--bg-card);
	border-right: 1px solid var(--border-light);
	display: flex;
	flex-direction: column;
	position: fixed;
	top: 0;
	left: 0;
	bottom: 0;
	z-index: 100;
	box-shadow: var(--shadow-sm);
}

.sidebar-header {
	padding: 24px 20px;
	border-bottom: 1px solid var(--border-light);
}

.logo {
	display: flex;
	align-items: center;
	gap: 12px;
}

.logo-icon {
	width: 44px;
	height: 44px;
	background: linear-gradient(135deg, var(--primary), var(--primary-dark));
	border-radius: var(--radius);
	display: flex;
	align-items: center;
	justify-content: center;
	color: white;
	box-shadow: 0 4px 12px rgba(0, 188, 212, 0.3);
}

.logo-text {
	display: flex;
	flex-direction: column;
}

.logo-title {
	font-size: 1.1rem;
	font-weight: 700;
	color: var(--text);
	line-height: 1.2;
}

.logo-subtitle {
	font-size: 0.7rem;
	color: var(--text-muted);
	text-transform: uppercase;
	letter-spacing: 0.5px;
	font-weight: 500;
}

/* Navigation */
.nav {
	flex: 1;
	padding: 12px 10px;
	overflow-y: auto;
	display: flex;
	flex-direction: column;
	gap: 2px;
}

.nav-item {
	display: flex;
	align-items: center;
	gap: 12px;
	padding: 11px 14px;
	border-radius: var(--radius-sm);
	color: var(--text-secondary);
	text-decoration: none;
	font-size: 0.875rem;
	font-weight: 500;
	transition: all var(--transition);
	position: relative;
	overflow: hidden;
}

.nav-item::before {
	content: '';
	position: absolute;
	left: 0;
	top: 50%;
	transform: translateY(-50%) scaleY(0);
	width: 3px;
	height: 60%;
	background: var(--primary);
	border-radius: 0 3px 3px 0;
	transition: transform var(--transition);
}

.nav-item:hover {
	background: var(--primary-50);
	color: var(--primary-dark);
	transform: translateX(2px);
}

.nav-item.active {
	background: var(--primary-100);
	color: var(--primary-dark);
	font-weight: 600;
	box-shadow: var(--shadow-neumorphic-sm);
}

.nav-item.active::before {
	transform: translateY(-50%) scaleY(1);
}

.nav-item:active {
	transform: scale(0.98);
}

.nav-icon {
	display: flex;
	align-items: center;
	justify-content: center;
	width: 20px;
	height: 20px;
	flex-shrink: 0;
}

.nav-label {
	white-space: nowrap;
}

/* Sidebar footer */
.sidebar-footer {
	padding: 16px 14px;
	border-top: 1px solid var(--border-light);
	display: flex;
	align-items: center;
	justify-content: space-between;
	gap: 8px;
}

.user-info {
	display: flex;
	align-items: center;
	gap: 10px;
	flex: 1;
	min-width: 0;
}

.user-avatar {
	width: 36px;
	height: 36px;
	background: linear-gradient(135deg, var(--primary), var(--primary-dark));
	border-radius: 10px;
	display: flex;
	align-items: center;
	justify-content: center;
	color: white;
	font-weight: 600;
	font-size: 0.85rem;
	flex-shrink: 0;
}

.user-details {
	display: flex;
	flex-direction: column;
	min-width: 0;
}

.user-name {
	font-size: 0.8rem;
	font-weight: 600;
	color: var(--text);
	white-space: nowrap;
	overflow: hidden;
	text-overflow: ellipsis;
}

.user-role {
	font-size: 0.7rem;
	color: var(--text-muted);
}

.logout-btn {
	width: 36px;
	height: 36px;
	border-radius: var(--radius-sm);
	border: 1px solid var(--border);
	background: var(--bg-card);
	color: var(--text-muted);
	display: flex;
	align-items: center;
	justify-content: center;
	cursor: pointer;
	transition: all var(--transition);
	flex-shrink: 0;
}

.logout-btn:hover {
	color: var(--danger);
	border-color: var(--danger);
	background: rgba(239, 68, 68, 0.05);
	box-shadow: var(--shadow-sm);
}

.logout-btn:active {
	transform: scale(0.93);
}

/* Main content */
.main {
	flex: 1;
	margin-left: 260px;
	min-height: 100vh;
	display: flex;
	flex-direction: column;
}

.topbar {
	padding: 24px 32px 0;
}

.page-title {
	font-size: 1.6rem;
	font-weight: 700;
	color: var(--text);
	letter-spacing: -0.02em;
}

.content {
	flex: 1;
	padding: 20px 32px 32px;
}
</style>
