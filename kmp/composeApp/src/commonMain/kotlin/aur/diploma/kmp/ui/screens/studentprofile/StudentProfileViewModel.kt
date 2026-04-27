package aur.diploma.kmp.ui.screens.studentprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import aur.diploma.kmp.data.model.StudentProfileResponse
import aur.diploma.kmp.data.repository.TeacherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class StudentProfileUiState(
    val profile: StudentProfileResponse? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class StudentProfileViewModel(
    private val studentId: Long,
    private val teacherRepository: TeacherRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StudentProfileUiState())
    val uiState: StateFlow<StudentProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = StudentProfileUiState(isLoading = true)
            try {
                val profile = teacherRepository.getStudentProfile(studentId)
                _uiState.value = StudentProfileUiState(profile = profile)
            } catch (e: Exception) {
                _uiState.value = StudentProfileUiState(error = "Не удалось загрузить профиль")
            }
        }
    }
}
