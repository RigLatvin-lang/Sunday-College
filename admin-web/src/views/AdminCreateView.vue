<script setup lang="ts">
import { ref } from 'vue'
import { authApi } from '@/api/auth'

const form = ref({ login: '', password: '' })
const securityKey = ref('')
const success = ref(false)
const error = ref('')
const loading = ref(false)

async function handleSubmit() {
  error.value = ''
  success.value = false
  loading.value = true
  try {
    await authApi.createAdmin(form.value, securityKey.value)
    success.value = true
    form.value = { login: '', password: '' }
    securityKey.value = ''
  } catch (e: any) {
    error.value = e.response?.data?.message || 'Ошибка при создании администратора'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="page">
    <div class="form-card">
      <div class="form-header">
        <div class="form-icon">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/>
          </svg>
        </div>
        <div>
          <h2>Новый администратор</h2>
          <p>Создание учетной записи администратора</p>
        </div>
      </div>

      <form @submit.prevent="handleSubmit">
        <div class="field">
          <label>Логин</label>
          <input v-model="form.login" required placeholder="admin_login" autocomplete="off" />
        </div>
        <div class="field">
          <label>Пароль</label>
          <input v-model="form.password" type="password" required placeholder="Надежный пароль" autocomplete="new-password" />
        </div>
        <div class="field">
          <label>Секретный ключ (X-Security-Key)</label>
          <input v-model="securityKey" type="password" required placeholder="Ключ безопасности" />
        </div>

        <div v-if="error" class="alert alert-error">{{ error }}</div>
        <div v-if="success" class="alert alert-success">Администратор успешно создан</div>

        <button type="submit" class="btn-primary" :disabled="loading">
          <span v-if="loading" class="spinner"></span>
          <span v-else>Создать администратора</span>
        </button>
      </form>
    </div>
  </div>
</template>

<style scoped>
.page { animation: fadeIn 0.3s ease; }

.form-card {
  max-width: 500px;
  background: var(--bg-card);
  border-radius: var(--radius-xl);
  padding: 32px;
  box-shadow: var(--shadow-neumorphic);
  border: 1px solid var(--border-light);
}

.form-header {
  display: flex; align-items: center; gap: 14px; margin-bottom: 28px;
}

.form-icon {
  width: 48px; height: 48px;
  background: linear-gradient(135deg, var(--primary), var(--primary-dark));
  border-radius: var(--radius);
  display: flex; align-items: center; justify-content: center;
  color: white;
  box-shadow: 0 4px 12px rgba(0, 188, 212, 0.3);
  flex-shrink: 0;
}

.form-header h2 {
  font-size: 1.1rem; font-weight: 700; color: var(--text); line-height: 1.3;
}
.form-header p {
  font-size: 0.8rem; color: var(--text-muted);
}

.field {
  display: flex; flex-direction: column; gap: 6px; margin-bottom: 18px;
}
.field label {
  font-size: 0.78rem; font-weight: 600; color: var(--text-secondary);
  text-transform: uppercase; letter-spacing: 0.5px;
}
.field input {
  padding: 12px 14px; border: 1px solid var(--border); border-radius: var(--radius);
  background: var(--bg-input); color: var(--text); font-size: 0.875rem;
  transition: all var(--transition); box-shadow: var(--shadow-inset);
}
.field input:focus {
  outline: none; border-color: var(--primary);
  box-shadow: 0 0 0 3px var(--primary-200), var(--shadow-inset);
}
.field input::placeholder { color: var(--text-muted); }

.alert {
  padding: 12px 16px; border-radius: var(--radius); font-size: 0.85rem;
  margin-bottom: 16px; font-weight: 500;
}
.alert-error {
  background: rgba(239, 68, 68, 0.08); color: var(--danger);
  border: 1px solid rgba(239, 68, 68, 0.15);
}
.alert-success {
  background: rgba(16, 185, 129, 0.08); color: var(--success);
  border: 1px solid rgba(16, 185, 129, 0.15);
}

.btn-primary {
  width: 100%; padding: 13px;
  background: linear-gradient(135deg, var(--primary), var(--primary-dark));
  color: white; border: none; border-radius: var(--radius);
  font-size: 0.9rem; font-weight: 600; cursor: pointer;
  transition: all var(--transition);
  box-shadow: 0 4px 14px rgba(0, 188, 212, 0.3);
  display: flex; align-items: center; justify-content: center; min-height: 48px;
}
.btn-primary:hover:not(:disabled) {
  transform: translateY(-1px); box-shadow: 0 6px 20px rgba(0, 188, 212, 0.4);
}
.btn-primary:active:not(:disabled) { transform: scale(0.98); }
.btn-primary:disabled { opacity: 0.7; cursor: not-allowed; }

.spinner {
  width: 20px; height: 20px;
  border: 2px solid rgba(255,255,255,0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 0.6s linear infinite;
}
@keyframes spin { to { transform: rotate(360deg); } }
</style>
