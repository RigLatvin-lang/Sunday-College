package aur.diploma.kmp.ui.screens.lessons

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import aur.diploma.kmp.data.model.LessonResponse
import aur.diploma.kmp.data.repository.TeacherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LessonsUiState(
    val lessons: List<LessonResponse> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class LessonsViewModel(private val teacherRepository: TeacherRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(LessonsUiState())
    val uiState: StateFlow<LessonsUiState> = _uiState.asStateFlow()

    init {
        loadLessons()
    }

    fun loadLessons() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val lessons = teacherRepository.getUpcomingLessons()
                _uiState.value = LessonsUiState(lessons = lessons)
            } catch (e: Exception) {
                _uiState.value = LessonsUiState(error = "Не удалось загрузить занятия")
            }
        }
    }
}
