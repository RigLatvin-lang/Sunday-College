<script setup lang="ts">
import { useParentsStore } from '@/stores/parents'
import { useStudentsStore } from '@/stores/students'
import type { CreateParentRequest, StudentResponse } from '@/types'
import { computed, onMounted, ref } from 'vue'

const parentsStore = useParentsStore()
const studentsStore = useStudentsStore()

// ─── Поиск ───────────────────────────────────────────────────────────────────
const search = ref('')

const filtered = computed(() => {
	const q = search.value.toLowerCase()
	if (!q) return parentsStore.items
	return parentsStore.items.filter(p => {
		const fullName =
			`${p.lastName} ${p.firstName} ${p.middleName ?? ''}`.toLowerCase()
		const child =
			`${p.child.lastName} ${p.child.firstName} ${p.child.middleName ?? ''}`.toLowerCase()
		return (
			fullName.includes(q) ||
			child.includes(q) ||
			p.login.toLowerCase().includes(q)
		)
	})
})

// ─── Модалка создания ────────────────────────────────────────────────────────
const showModal = ref(false)
const formError = ref<string | null>(null)
const submitting = ref(false)

const form = ref<CreateParentRequest>({
	login: '',
	password: '',
	lastName: '',
	firstName: '',
	middleName: '',
	phone: '',
	studentId: 0,
})

// Поиск ученика внутри формы
const studentSearch = ref('')
const selectedStudent = ref<StudentResponse | null>(null)
const studentDropdownOpen = ref(false)

const studentSuggestions = computed(() => {
	const q = studentSearch.value.toLowerCase().trim()
	if (!q) return []
	return studentsStore.items
		.filter(s => {
			const full =
				`${s.lastName} ${s.firstName} ${s.middleName ?? ''}`.toLowerCase()
			return full.includes(q)
		})
		.slice(0, 8)
})

function onStudentInput() {
	studentDropdownOpen.value = true
	if (!studentSearch.value) clearStudent()
}

function closeDropdownDelayed() {
	setTimeout(() => {
		studentDropdownOpen.value = false
	}, 150)
}

function selectStudent(s: StudentResponse) {
	selectedStudent.value = s
	form.value.studentId = s.id
	studentSearch.value = `${s.lastName} ${s.firstName}${s.middleName ? ' ' + s.middleName : ''}`
	studentDropdownOpen.value = false
}

function clearStudent() {
	selectedStudent.value = null
	form.value.studentId = 0
	studentSearch.value = ''
}

function openCreate() {
	form.value = {
		login: '',
		password: '',
		lastName: '',
		firstName: '',
		middleName: '',
		phone: '',
		studentId: 0,
	}
	studentSearch.value = ''
	selectedStudent.value = null
	studentDropdownOpen.value = false
	formError.value = null
	showModal.value = true
}

function closeModal() {
	showModal.value = false
	formError.value = null
}

async function handleSubmit() {
	if (!selectedStudent.value) {
		formError.value = 'Выберите ученика из списка'
		return
	}
	submitting.value = true
	formError.value = null
	try {
		await parentsStore.create(form.value)
		showModal.value = false
	} catch (e: any) {
		formError.value =
			e?.response?.data?.message || 'Произошла ошибка при создании'
	} finally {
		submitting.value = false
	}
}

// ─── Удаление ────────────────────────────────────────────────────────────────
const deletingId = ref<number | null>(null)
const showConfirm = ref(false)
const confirmName = ref('')

function askDelete(id: number, name: string) {
	deletingId.value = id
	confirmName.value = name
	showConfirm.value = true
}

async function confirmDelete() {
	if (!deletingId.value) return
	await parentsStore.remove(deletingId.value)
	showConfirm.value = false
	deletingId.value = null
}

// ─── Init ─────────────────────────────────────────────────────────────────────
onMounted(async () => {
	await Promise.all([parentsStore.fetchAll(), studentsStore.fetchAll()])
})
</script>

<template>
	<div class="page">
		<!-- Шапка страницы -->
		<div class="page-header">
			<input
				v-model="search"
				class="search-input"
				placeholder="Поиск по ФИО родителя или ученика..."
			/>
			<button class="btn-primary" @click="openCreate">
				<svg
					width="16"
					height="16"
					viewBox="0 0 24 24"
					fill="none"
					stroke="currentColor"
					stroke-width="2.5"
				>
					<line x1="12" y1="5" x2="12" y2="19" />
					<line x1="5" y1="12" x2="19" y2="12" />
				</svg>
				Добавить родителя
			</button>
		</div>

		<!-- Счётчик -->
		<div class="stats-bar">
			<span class="stats-count"
				>Всего родителей: <strong>{{ parentsStore.items.length }}</strong></span
			>
		</div>

		<!-- Загрузка -->
		<div v-if="parentsStore.loading" class="loading-state">
			<div class="spinner"></div>
			<span>Загрузка...</span>
		</div>

		<!-- Таблица -->
		<div v-else class="table-card">
			<table class="data-table">
				<thead>
					<tr>
						<th>ID</th>
						<th>Родитель</th>
						<th>Логин</th>
						<th>Телефон</th>
						<th>Ученик</th>
						<th>Действия</th>
					</tr>
				</thead>
				<tbody>
					<tr v-for="p in filtered" :key="p.id">
						<td class="td-id">{{ p.id }}</td>
						<td class="td-name">
							<div class="person-cell">
								<div class="avatar">{{ p.lastName.charAt(0) }}</div>
								<div>
									<div class="person-name">
										{{ p.lastName }} {{ p.firstName
										}}{{ p.middleName ? ' ' + p.middleName : '' }}
									</div>
								</div>
							</div>
						</td>
						<td class="td-login">{{ p.login }}</td>
						<td class="td-phone">{{ p.phone || '—' }}</td>
						<td class="td-child">
							<div class="child-badge">
								<svg
									width="13"
									height="13"
									viewBox="0 0 24 24"
									fill="none"
									stroke="currentColor"
									stroke-width="2"
								>
									<path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" />
									<circle cx="12" cy="7" r="4" />
								</svg>
								{{ p.child.lastName }} {{ p.child.firstName
								}}{{ p.child.middleName ? ' ' + p.child.middleName : '' }}
							</div>
						</td>
						<td class="td-actions">
							<button
								class="btn-icon danger"
								title="Удалить родителя"
								@click="askDelete(p.id, `${p.lastName} ${p.firstName}`)"
							>
								<svg
									width="16"
									height="16"
									viewBox="0 0 24 24"
									fill="none"
									stroke="currentColor"
									stroke-width="2"
								>
									<polyline points="3 6 5 6 21 6" />
									<path
										d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"
									/>
								</svg>
							</button>
						</td>
					</tr>
				</tbody>
			</table>
			<div v-if="!filtered.length && !parentsStore.loading" class="empty-state">
				<svg
					width="40"
					height="40"
					viewBox="0 0 24 24"
					fill="none"
					stroke="currentColor"
					stroke-width="1.5"
					opacity="0.3"
				>
					<path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2" />
					<circle cx="9" cy="7" r="4" />
					<path d="M23 21v-2a4 4 0 0 0-3-3.87" />
					<path d="M16 3.13a4 4 0 0 1 0 7.75" />
				</svg>
				<p>Родителей не найдено</p>
			</div>
		</div>

		<!-- ───── Модалка создания ───── -->
		<Teleport to="body">
			<div v-if="showModal" class="modal-overlay" @click.self="closeModal">
				<div class="modal" @click.stop>
					<div class="modal-header">
						<h2>Новый родитель</h2>
						<button class="modal-close" @click="closeModal">
							<svg
								width="18"
								height="18"
								viewBox="0 0 24 24"
								fill="none"
								stroke="currentColor"
								stroke-width="2"
							>
								<line x1="18" y1="6" x2="6" y2="18" />
								<line x1="6" y1="6" x2="18" y2="18" />
							</svg>
						</button>
					</div>

					<form @submit.prevent="handleSubmit">
						<div class="form-section-label">Данные для входа</div>

						<div class="fields-row">
							<div class="field">
								<label>Логин <span class="required">*</span></label>
								<input
									v-model="form.login"
									required
									placeholder="parent_ivanov"
									autocomplete="off"
								/>
							</div>
							<div class="field">
								<label>Пароль <span class="required">*</span></label>
								<input
									v-model="form.password"
									type="password"
									required
									placeholder="••••••"
								/>
							</div>
						</div>

						<div class="form-section-label">ФИО родителя</div>

						<div class="field">
							<label>Фамилия <span class="required">*</span></label>
							<input v-model="form.lastName" required placeholder="Иванов" />
						</div>
						<div class="fields-row">
							<div class="field">
								<label>Имя <span class="required">*</span></label>
								<input v-model="form.firstName" required placeholder="Иван" />
							</div>
							<div class="field">
								<label>Отчество</label>
								<input v-model="form.middleName" placeholder="Иванович" />
							</div>
						</div>

						<div class="field">
							<label>Телефон</label>
							<input
								v-model="form.phone"
								placeholder="+7 (999) 000-00-00"
								type="tel"
							/>
						</div>

						<div class="form-section-label">Закрепить за учеником</div>

						<div class="field">
							<label>Поиск ученика <span class="required">*</span></label>
							<div class="student-search-wrap">
								<div class="student-input-row">
									<svg
										class="search-icon"
										width="16"
										height="16"
										viewBox="0 0 24 24"
										fill="none"
										stroke="currentColor"
										stroke-width="2"
									>
										<circle cx="11" cy="11" r="8" />
										<line x1="21" y1="21" x2="16.65" y2="16.65" />
									</svg>
									<input
										v-model="studentSearch"
										:class="[
											'student-input',
											selectedStudent ? 'selected' : '',
										]"
										placeholder="Начните вводить фамилию..."
										autocomplete="off"
										@input="onStudentInput"
										@focus="studentDropdownOpen = true"
										@blur="closeDropdownDelayed"
									/>
									<button
										v-if="selectedStudent"
										type="button"
										class="clear-btn"
										@click="clearStudent"
									>
										<svg
											width="14"
											height="14"
											viewBox="0 0 24 24"
											fill="none"
											stroke="currentColor"
											stroke-width="2.5"
										>
											<line x1="18" y1="6" x2="6" y2="18" />
											<line x1="6" y1="6" x2="18" y2="18" />
										</svg>
									</button>
								</div>

								<!-- Выбранный ученик -->
								<div v-if="selectedStudent" class="selected-badge">
									<svg
										width="14"
										height="14"
										viewBox="0 0 24 24"
										fill="none"
										stroke="currentColor"
										stroke-width="2"
									>
										<polyline points="20 6 9 17 4 12" />
									</svg>
									Выбран:
									<strong>
										{{ selectedStudent.lastName }} {{ selectedStudent.firstName
										}}{{
											selectedStudent.middleName
												? ' ' + selectedStudent.middleName
												: ''
										}}
									</strong>
								</div>

								<!-- Выпадающий список -->
								<div
									v-if="studentDropdownOpen && studentSuggestions.length"
									class="student-dropdown"
								>
									<div
										v-for="s in studentSuggestions"
										:key="s.id"
										class="dropdown-item"
										@mousedown.prevent="selectStudent(s)"
									>
										<div class="dropdown-avatar">
											{{ s.lastName.charAt(0) }}
										</div>
										<div>
											<div class="dropdown-name">
												{{ s.lastName }} {{ s.firstName
												}}{{ s.middleName ? ' ' + s.middleName : '' }}
											</div>
											<div class="dropdown-id">ID: {{ s.id }}</div>
										</div>
									</div>
								</div>
								<div
									v-if="
										studentDropdownOpen &&
										studentSearch &&
										!studentSuggestions.length
									"
									class="student-dropdown"
								>
									<div class="dropdown-empty">Ученики не найдены</div>
								</div>
							</div>
						</div>

						<!-- Ошибка -->
						<div v-if="formError" class="form-error">
							<svg
								width="15"
								height="15"
								viewBox="0 0 24 24"
								fill="none"
								stroke="currentColor"
								stroke-width="2"
							>
								<circle cx="12" cy="12" r="10" />
								<line x1="12" y1="8" x2="12" y2="12" />
								<line x1="12" y1="16" x2="12.01" y2="16" />
							</svg>
							{{ formError }}
						</div>

						<div class="modal-actions">
							<button type="button" class="btn-secondary" @click="closeModal">
								Отмена
							</button>
							<button type="submit" class="btn-primary" :disabled="submitting">
								<span v-if="submitting">Создание...</span>
								<span v-else>Создать родителя</span>
							</button>
						</div>
					</form>
				</div>
			</div>
		</Teleport>

		<!-- ───── Диалог подтверждения удаления ───── -->
		<Teleport to="body">
			<div
				v-if="showConfirm"
				class="modal-overlay"
				@click.self="showConfirm = false"
			>
				<div class="modal confirm-modal" @click.stop>
					<div class="confirm-icon">
						<svg
							width="28"
							height="28"
							viewBox="0 0 24 24"
							fill="none"
							stroke="currentColor"
							stroke-width="1.8"
						>
							<polyline points="3 6 5 6 21 6" />
							<path
								d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"
							/>
						</svg>
					</div>
					<h2>Удалить родителя?</h2>
					<p class="confirm-text">
						Вы собираетесь удалить <strong>{{ confirmName }}</strong
						>.<br />
						Его учётная запись и профиль будут удалены без возможности
						восстановления.
					</p>
					<div class="modal-actions">
						<button class="btn-secondary" @click="showConfirm = false">
							Отмена
						</button>
						<button class="btn-danger" @click="confirmDelete">Удалить</button>
					</div>
				</div>
			</div>
		</Teleport>
	</div>
</template>

<style scoped>
.page {
	animation: fadeIn 0.3s ease;
}

/* ── Шапка ── */
.page-header {
	display: flex;
	align-items: center;
	gap: 12px;
	margin-bottom: 12px;
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
.search-input::placeholder {
	color: var(--text-muted);
}

.btn-primary {
	display: flex;
	align-items: center;
	gap: 8px;
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
.btn-primary:hover {
	transform: translateY(-1px);
	box-shadow: 0 5px 16px rgba(0, 188, 212, 0.35);
}
.btn-primary:active {
	transform: scale(0.97);
}
.btn-primary:disabled {
	opacity: 0.6;
	cursor: not-allowed;
	transform: none;
}

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
.btn-secondary:hover {
	background: var(--bg-hover);
}

.btn-danger {
	padding: 11px 20px;
	background: linear-gradient(135deg, #ef4444, #dc2626);
	color: white;
	border: none;
	border-radius: var(--radius);
	font-size: 0.85rem;
	font-weight: 600;
	cursor: pointer;
	transition: all var(--transition);
	box-shadow: 0 3px 10px rgba(239, 68, 68, 0.25);
}
.btn-danger:hover {
	transform: translateY(-1px);
	box-shadow: 0 5px 16px rgba(239, 68, 68, 0.35);
}
.btn-danger:active {
	transform: scale(0.97);
}

/* ── Счётчик ── */
.stats-bar {
	margin-bottom: 16px;
}
.stats-count {
	font-size: 0.82rem;
	color: var(--text-muted);
}
.stats-count strong {
	color: var(--text-secondary);
	font-weight: 600;
}

/* ── Загрузка ── */
.loading-state {
	display: flex;
	align-items: center;
	gap: 12px;
	padding: 40px;
	justify-content: center;
	color: var(--text-muted);
}
.spinner {
	width: 22px;
	height: 22px;
	border: 2px solid var(--border);
	border-top-color: var(--primary);
	border-radius: 50%;
	animation: spin 0.8s linear infinite;
}
@keyframes spin {
	to {
		transform: rotate(360deg);
	}
}

/* ── Таблица ── */
.table-card {
	background: var(--bg-card);
	border-radius: var(--radius-lg);
	box-shadow: var(--shadow-neumorphic);
	overflow: hidden;
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
	padding: 13px 18px;
	font-size: 0.875rem;
	border-bottom: 1px solid var(--border-light);
	transition: background var(--transition);
}

.data-table tr:last-child td {
	border-bottom: none;
}
.data-table tr:hover td {
	background: var(--primary-50);
}

.td-id {
	color: var(--text-muted);
	font-size: 0.8rem;
	width: 50px;
}
.td-login {
	color: var(--text-secondary);
	font-family: 'SF Mono', monospace;
	font-size: 0.82rem;
}
.td-phone {
	color: var(--text-secondary);
	font-size: 0.85rem;
}

.person-cell {
	display: flex;
	align-items: center;
	gap: 10px;
}
.avatar {
	width: 32px;
	height: 32px;
	border-radius: 10px;
	background: linear-gradient(135deg, var(--primary), var(--primary-dark));
	color: white;
	font-weight: 600;
	font-size: 0.8rem;
	display: flex;
	align-items: center;
	justify-content: center;
	flex-shrink: 0;
}
.person-name {
	font-weight: 500;
	color: var(--text);
}

.child-badge {
	display: inline-flex;
	align-items: center;
	gap: 6px;
	padding: 4px 10px;
	background: var(--primary-50);
	border: 1px solid var(--primary-100);
	border-radius: 20px;
	font-size: 0.8rem;
	color: var(--primary-dark);
	font-weight: 500;
}

.td-actions {
	width: 60px;
}

.btn-icon {
	width: 34px;
	height: 34px;
	border-radius: var(--radius-sm);
	border: 1px solid var(--border-light);
	background: var(--bg-card);
	color: var(--text-muted);
	display: flex;
	align-items: center;
	justify-content: center;
	cursor: pointer;
	transition: all var(--transition);
}
.btn-icon.danger:hover {
	color: var(--danger);
	border-color: var(--danger);
	background: rgba(239, 68, 68, 0.06);
}
.btn-icon:active {
	transform: scale(0.9);
}

.empty-state {
	display: flex;
	flex-direction: column;
	align-items: center;
	gap: 12px;
	padding: 56px 20px;
	color: var(--text-muted);
	font-size: 0.9rem;
}

/* ── Модалка ── */
.modal-overlay {
	position: fixed;
	inset: 0;
	background: rgba(0, 0, 0, 0.35);
	backdrop-filter: blur(4px);
	display: flex;
	align-items: center;
	justify-content: center;
	z-index: 1000;
	animation: fadeIn 0.2s ease;
}

.modal {
	background: var(--bg-card);
	border-radius: var(--radius-xl);
	padding: 28px;
	width: 100%;
	max-width: 500px;
	box-shadow: var(--shadow-xl);
	animation: scaleIn 0.25s ease;
	max-height: 90vh;
	overflow-y: auto;
}

.modal-header {
	display: flex;
	align-items: center;
	justify-content: space-between;
	margin-bottom: 22px;
}

.modal-header h2 {
	font-size: 1.15rem;
	font-weight: 700;
	color: var(--text);
}

.modal-close {
	width: 32px;
	height: 32px;
	border-radius: var(--radius-sm);
	border: 1px solid var(--border-light);
	background: transparent;
	color: var(--text-muted);
	display: flex;
	align-items: center;
	justify-content: center;
	cursor: pointer;
	transition: all var(--transition);
}
.modal-close:hover {
	color: var(--text);
	background: var(--bg-hover);
}

/* ── Форма ── */
.form-section-label {
	font-size: 0.72rem;
	font-weight: 700;
	color: var(--primary-dark);
	text-transform: uppercase;
	letter-spacing: 0.6px;
	margin: 18px 0 12px;
	padding-bottom: 6px;
	border-bottom: 1px solid var(--border-light);
}
.form-section-label:first-of-type {
	margin-top: 0;
}

.fields-row {
	display: grid;
	grid-template-columns: 1fr 1fr;
	gap: 12px;
}

.field {
	display: flex;
	flex-direction: column;
	gap: 6px;
	margin-bottom: 12px;
}
.field label {
	font-size: 0.78rem;
	font-weight: 600;
	color: var(--text-secondary);
	text-transform: uppercase;
	letter-spacing: 0.5px;
}
.required {
	color: var(--danger);
	margin-left: 2px;
}

.field input {
	padding: 10px 14px;
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
	box-shadow:
		0 0 0 3px var(--primary-200),
		var(--shadow-inset);
}
.field input::placeholder {
	color: var(--text-muted);
}

/* ── Поиск ученика ── */
.student-search-wrap {
	position: relative;
}

.student-input-row {
	position: relative;
	display: flex;
	align-items: center;
}
.search-icon {
	position: absolute;
	left: 12px;
	color: var(--text-muted);
	pointer-events: none;
}
.student-input {
	width: 100%;
	padding: 10px 14px 10px 36px !important;
	border: 1px solid var(--border);
	border-radius: var(--radius);
	background: var(--bg-input);
	color: var(--text);
	font-size: 0.875rem;
	transition: all var(--transition);
	box-shadow: var(--shadow-inset);
}
.student-input:focus {
	outline: none;
	border-color: var(--primary);
	box-shadow:
		0 0 0 3px var(--primary-200),
		var(--shadow-inset);
}
.student-input.selected {
	border-color: #22c55e;
	background: rgba(34, 197, 94, 0.04);
}
.student-input::placeholder {
	color: var(--text-muted);
}

.clear-btn {
	position: absolute;
	right: 10px;
	background: none;
	border: none;
	color: var(--text-muted);
	cursor: pointer;
	display: flex;
	align-items: center;
	padding: 4px;
	border-radius: 4px;
	transition: color var(--transition);
}
.clear-btn:hover {
	color: var(--danger);
}

.selected-badge {
	display: flex;
	align-items: center;
	gap: 6px;
	margin-top: 6px;
	padding: 6px 10px;
	background: rgba(34, 197, 94, 0.08);
	border: 1px solid rgba(34, 197, 94, 0.25);
	border-radius: var(--radius-sm);
	font-size: 0.8rem;
	color: #16a34a;
}
.selected-badge strong {
	font-weight: 600;
}

.student-dropdown {
	position: absolute;
	top: calc(100% + 4px);
	left: 0;
	right: 0;
	background: var(--bg-card);
	border: 1px solid var(--border);
	border-radius: var(--radius);
	box-shadow: var(--shadow-xl);
	z-index: 100;
	overflow: hidden;
	animation: fadeIn 0.15s ease;
}

.dropdown-item {
	display: flex;
	align-items: center;
	gap: 10px;
	padding: 10px 14px;
	cursor: pointer;
	transition: background var(--transition);
	border-bottom: 1px solid var(--border-light);
}
.dropdown-item:last-child {
	border-bottom: none;
}
.dropdown-item:hover {
	background: var(--primary-50);
}

.dropdown-avatar {
	width: 28px;
	height: 28px;
	border-radius: 8px;
	background: linear-gradient(135deg, var(--primary), var(--primary-dark));
	color: white;
	font-weight: 600;
	font-size: 0.75rem;
	display: flex;
	align-items: center;
	justify-content: center;
	flex-shrink: 0;
}
.dropdown-name {
	font-size: 0.85rem;
	font-weight: 500;
	color: var(--text);
}
.dropdown-id {
	font-size: 0.75rem;
	color: var(--text-muted);
}
.dropdown-empty {
	padding: 14px;
	text-align: center;
	color: var(--text-muted);
	font-size: 0.85rem;
}

/* ── Ошибка формы ── */
.form-error {
	display: flex;
	align-items: center;
	gap: 8px;
	padding: 10px 14px;
	background: rgba(239, 68, 68, 0.08);
	border: 1px solid rgba(239, 68, 68, 0.25);
	border-radius: var(--radius);
	color: #dc2626;
	font-size: 0.83rem;
	margin-top: 4px;
}

.modal-actions {
	display: flex;
	gap: 10px;
	justify-content: flex-end;
	margin-top: 22px;
}

/* ── Диалог удаления ── */
.confirm-modal {
	max-width: 400px;
	text-align: center;
}
.confirm-icon {
	width: 60px;
	height: 60px;
	border-radius: 18px;
	background: rgba(239, 68, 68, 0.1);
	border: 1px solid rgba(239, 68, 68, 0.2);
	display: flex;
	align-items: center;
	justify-content: center;
	margin: 0 auto 16px;
	color: #ef4444;
}
.confirm-modal h2 {
	font-size: 1.1rem;
	font-weight: 700;
	color: var(--text);
	margin-bottom: 8px;
}
.confirm-text {
	font-size: 0.875rem;
	color: var(--text-muted);
	line-height: 1.6;
	margin-bottom: 4px;
}
.confirm-modal .modal-actions {
	justify-content: center;
}
</style>
