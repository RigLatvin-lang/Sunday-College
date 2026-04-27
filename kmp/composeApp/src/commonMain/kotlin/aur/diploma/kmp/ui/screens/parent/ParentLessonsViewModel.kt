package aur.diploma.kmp.ui.screens.parent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import aur.diploma.kmp.data.model.LessonResponse
import aur.diploma.kmp.data.repository.ParentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ParentLessonsUiState(
    val lessons: List<LessonResponse> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class ParentLessonsViewModel(
    private val parentRepository: ParentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ParentLessonsUiState())
    val uiState: StateFlow<ParentLessonsUiState> = _uiState.asStateFlow()

    init {
        loadLessons()
    }

    fun loadLessons() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val lessons = parentRepository.getChildSchedule()
                _uiState.value = ParentLessonsUiState(lessons = lessons)
            } catch (e: Exception) {
                _uiState.value = ParentLessonsUiState(
                    error = "Не удалось загрузить расписание ребенка"
                )
            }
        }
    }
}
