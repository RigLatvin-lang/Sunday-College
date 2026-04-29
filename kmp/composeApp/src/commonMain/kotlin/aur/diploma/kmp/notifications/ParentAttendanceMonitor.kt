package aur.diploma.kmp.notifications

import aur.diploma.kmp.data.model.AttendanceRecordResponse
import aur.diploma.kmp.data.repository.ParentRepository
import aur.diploma.kmp.util.formatDateTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class ParentAttendanceMonitor(
    private val parentRepository: ParentRepository,
    private val attendanceNotifier: AttendanceNotifier
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var monitoringJob: Job? = null
    private var knownRecordKeys: Set<String> = emptySet()
    private var isInitialized = false

    fun start() {
        if (monitoringJob?.isActive == true) return
        monitoringJob = scope.launch {
            while (isActive) {
                runCatching { parentRepository.getChildAttendance() }
                    .onSuccess { profile ->
                        val records = profile.recentAttendance
                        if (!isInitialized) {
                            knownRecordKeys = records.map(::fingerprint).toSet()
                            isInitialized = true
                        } else {
                            val currentKeys = records.map(::fingerprint).toSet()
                            records
                                .asReversed()
                                .filter { fingerprint(it) !in knownRecordKeys }
                                .forEach { record ->
                                    attendanceNotifier.show(
                                        AttendanceStatusNotification(
                                            title = "Посещаемость обновлена",
                                            message = buildString {
                                                append(profile.student.fullName)
                                                append(": ")
                                                append(record.subjectName)
                                                append(", ")
                                                append(formatDateTime(record.dateTime))
                                                append(", статус: ")
                                                append(if (record.present) "присутствовал" else "отсутствовал")
                                            }
                                        )
                                    )
                                }
                            knownRecordKeys = currentKeys
                        }
                    }
                delay(POLL_INTERVAL_MILLIS)
            }
        }
    }

    fun stop() {
        monitoringJob?.cancel()
        monitoringJob = null
        knownRecordKeys = emptySet()
        isInitialized = false
    }

    private fun fingerprint(record: AttendanceRecordResponse): String {
        return "${record.subjectName}|${record.dateTime}|${record.present}"
    }

    private companion object {
        const val POLL_INTERVAL_MILLIS = 30_000L
    }
}
