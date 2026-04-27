package aur.diploma.kmp.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import aur.diploma.kmp.data.model.LoginResponse
import aur.diploma.kmp.data.repository.AuthRepository
import aur.diploma.kmp.ui.navigation.AppRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LoginUiState(
    val login: String = "",
    val password: String = "",
    val appRole: AppRole? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val loginResult: LoginResponse? = null
)

class LoginViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    val isLoggedIn: Boolean get() = authRepository.isLoggedIn

    fun onLoginChanged(value: String) {
        _uiState.value = _uiState.value.copy(login = value, error = null)
    }

    fun onPasswordChanged(value: String) {
        _uiState.value = _uiState.value.copy(password = value, error = null)
    }

    fun login() {
        val state = _uiState.value
        if (state.login.isBlank() || state.password.isBlank()) {
            _uiState.value = state.copy(error = "Заполните все поля")
            return
        }
        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, error = null)
            try {
                val result = authRepository.login(state.login, state.password)
                val appRole = result.role.toAppRole()
                if (appRole == null) {
                    authRepository.logout()
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Эта роль пока не поддерживается"
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        appRole = appRole,
                        loginResult = result
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Неверный логин или пароль"
                )
            }
        }
    }

    suspend fun restoreRole(): AppRole? {
        return authRepository.getCurrentRole()?.toAppRole()
    }

    fun logout() {
        authRepository.logout()
        _uiState.value = LoginUiState()
    }
}

private fun String.toAppRole(): AppRole? = when (uppercase()) {
    "TEACHER" -> AppRole.TEACHER
    "PARENT" -> AppRole.PARENT
    else -> null
}
