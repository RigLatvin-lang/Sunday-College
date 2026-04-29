package aur.diploma.kmp.ui.screens.student

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import aur.diploma.kmp.data.model.LessonResponse
import aur.diploma.kmp.data.repository.StudentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class StudentLessonsUiState(
    val lessons: List<LessonResponse> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class StudentLessonsViewModel(
    private val studentRepository: StudentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StudentLessonsUiState())
    val uiState: StateFlow<StudentLessonsUiState> = _uiState.asStateFlow()

    init {
        loadLessons()
    }

    fun loadLessons() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val lessons = studentRepository.getMySchedule()
                _uiState.value = StudentLessonsUiState(lessons = lessons)
            } catch (_: Exception) {
                _uiState.value = StudentLessonsUiState(
                    error = "Не удалось загрузить ваше расписание"
                )
            }
        }
    }
}
