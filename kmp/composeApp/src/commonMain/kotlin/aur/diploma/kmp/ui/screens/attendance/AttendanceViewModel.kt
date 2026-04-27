package aur.diploma.kmp.ui.screens.attendance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import aur.diploma.kmp.data.model.LessonAttendanceSummary
import aur.diploma.kmp.data.model.MarkAttendanceRequest
import aur.diploma.kmp.data.repository.TeacherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AttendanceUiState(
    val summary: LessonAttendanceSummary? = null,
    val attendanceMap: Map<Long, Boolean> = emptyMap(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val saveSuccess: Boolean = false
)

class AttendanceViewModel(
    private val lessonId: Long,
    private val teacherRepository: TeacherRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AttendanceUiState())
    val uiState: StateFlow<AttendanceUiState> = _uiState.asStateFlow()

    init {
        loadAttendance()
    }

    fun loadAttendance() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val summary = teacherRepository.getLessonAttendance(lessonId)
                val map = summary.records.associate { it.studentId to it.present }
                _uiState.value = AttendanceUiState(summary = summary, attendanceMap = map)
            } catch (e: Exception) {
                _uiState.value = AttendanceUiState(error = "Не удалось загрузить данные")
            }
        }
    }

    fun toggleAttendance(studentId: Long, present: Boolean) {
        val current = _uiState.value.attendanceMap.toMutableMap()
        current[studentId] = present
        _uiState.value = _uiState.value.copy(attendanceMap = current)
    }

    fun saveAttendance() {
        val state = _uiState.value
        val records = state.attendanceMap.map { (studentId, present) ->
            MarkAttendanceRequest(studentId = studentId, present = present)
        }
        if (records.isEmpty()) return

        viewModelScope.launch {
            _uiState.value = state.copy(isSaving = true, error = null, saveSuccess = false)
            try {
                teacherRepository.markAttendance(lessonId, records)
                _uiState.value = _uiState.value.copy(isSaving = false, saveSuccess = true)
                loadAttendance()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    error = "Ошибка при сохранении"
                )
            }
        }
    }
}
