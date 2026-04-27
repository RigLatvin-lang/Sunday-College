import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
	history: createWebHistory(),
	routes: [
		{
			path: '/login',
			name: 'login',
			component: () => import('@/views/LoginView.vue'),
			meta: { guest: true },
		},
		{
			path: '/',
			component: () => import('@/layouts/AdminLayout.vue'),
			meta: { requiresAuth: true },
			children: [
				{
					path: '',
					name: 'dashboard',
					component: () => import('@/views/DashboardView.vue'),
				},
				{
					path: 'groups',
					name: 'groups',
					component: () => import('@/views/GroupsView.vue'),
				},
				{
					path: 'teachers',
					name: 'teachers',
					component: () => import('@/views/TeachersView.vue'),
				},
				{
					path: 'students',
					name: 'students',
					component: () => import('@/views/StudentsView.vue'),
				},
				{
					path: 'subjects',
					name: 'subjects',
					component: () => import('@/views/SubjectsView.vue'),
				},
				{
					path: 'lessons',
					name: 'lessons',
					component: () => import('@/views/LessonsView.vue'),
				},
				{
					path: 'schedule',
					name: 'schedule',
					component: () => import('@/views/ScheduleView.vue'),
				},
				{
					path: 'parents',
					name: 'parents',
					component: () => import('@/views/ParentsView.vue'),
				},
				{
					path: 'admin',
					name: 'admin',
					component: () => import('@/views/AdminCreateView.vue'),
				},
			],
		},
	],
})

router.beforeEach(to => {
	const token = localStorage.getItem('token')
	if (to.meta.requiresAuth && !token) {
		return { name: 'login' }
	}
	if (to.meta.guest && token) {
		return { name: 'dashboard' }
	}
})

export default router
