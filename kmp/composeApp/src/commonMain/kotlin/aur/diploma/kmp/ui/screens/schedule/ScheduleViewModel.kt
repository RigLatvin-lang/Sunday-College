package aur.diploma.kmp.ui.screens.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import aur.diploma.kmp.data.model.GroupResponse
import aur.diploma.kmp.data.model.LessonResponse
import aur.diploma.kmp.data.repository.ScheduleRepository
import aur.diploma.kmp.util.todayIsoDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ScheduleUiState(
    val lessons: List<LessonResponse> = emptyList(),
    val groups: List<GroupResponse> = emptyList(),
    val selectedDate: String? = null,
    val selectedGroupId: Long? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class ScheduleViewModel(private val scheduleRepository: ScheduleRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(ScheduleUiState())
    val uiState: StateFlow<ScheduleUiState> = _uiState.asStateFlow()

    init {
        loadSchedule()
        loadGroups()
    }

    fun onDateSelected(date: String?) {
        _uiState.value = _uiState.value.copy(selectedDate = date)
        loadSchedule()
    }

    fun onGroupSelected(groupId: Long?) {
        _uiState.value = _uiState.value.copy(selectedGroupId = groupId)
        loadSchedule()
    }

    fun loadSchedule() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val state = _uiState.value
                val lessons = scheduleRepository.getSchedule(state.selectedDate, state.selectedGroupId)
                _uiState.value = _uiState.value.copy(lessons = lessons, isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Не удалось загрузить расписание"
                )
            }
        }
    }

    private fun loadGroups() {
        viewModelScope.launch {
            try {
                val groups = scheduleRepository.getGroups()
                _uiState.value = _uiState.value.copy(groups = groups)
            } catch (_: Exception) { }
        }
    }
}
