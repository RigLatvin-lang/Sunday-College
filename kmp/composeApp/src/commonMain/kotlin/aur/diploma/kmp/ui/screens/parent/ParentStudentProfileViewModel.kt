package aur.diploma.kmp.ui.screens.parent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import aur.diploma.kmp.data.model.StudentProfileResponse
import aur.diploma.kmp.data.repository.ParentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ParentStudentProfileUiState(
    val profile: StudentProfileResponse? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class ParentStudentProfileViewModel(
    private val parentRepository: ParentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ParentStudentProfileUiState())
    val uiState: StateFlow<ParentStudentProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = ParentStudentProfileUiState(isLoading = true)
            try {
                val profile = parentRepository.getChildAttendance()
                _uiState.value = ParentStudentProfileUiState(profile = profile)
            } catch (e: Exception) {
                _uiState.value = ParentStudentProfileUiState(
                    error = "Не удалось загрузить данные ребенка"
                )
            }
        }
    }
}
