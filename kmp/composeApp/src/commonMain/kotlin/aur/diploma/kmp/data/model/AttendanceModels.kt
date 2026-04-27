package aur.diploma.kmp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class MarkAttendanceRequest(
    val studentId: Long,
    val present: Boolean
)

@Serializable
data class BulkAttendanceRequest(
    val records: List<MarkAttendanceRequest>
)

@Serializable
data class AttendanceResponse(
    val id: Long?,
    val studentId: Long,
    val studentName: String,
    val lessonId: Long,
    val present: Boolean
)

@Serializable
data class LessonAttendanceSummary(
    val lesson: LessonResponse,
    val totalStudents: Long,
    val presentCount: Long,
    val attendancePercentage: Double,
    val records: List<AttendanceResponse>
)
