package aur.diploma.kmp.ui.screens.student

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import aur.diploma.kmp.data.model.StudentProfileResponse
import aur.diploma.kmp.data.repository.StudentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class StudentStatsUiState(
    val profile: StudentProfileResponse? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class StudentStatsViewModel(
    private val studentRepository: StudentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StudentStatsUiState())
    val uiState: StateFlow<StudentStatsUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = StudentStatsUiState(isLoading = true)
            try {
                val profile = studentRepository.getMyAttendance()
                _uiState.value = StudentStatsUiState(profile = profile)
            } catch (_: Exception) {
                _uiState.value = StudentStatsUiState(
                    error = "Не удалось загрузить вашу статистику"
                )
            }
        }
    }
}
