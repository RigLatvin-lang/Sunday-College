package aur.diploma.kmp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class StudentResponse(
    val id: Long,
    val lastName: String,
    val firstName: String,
    val middleName: String? = null,
    val birthDate: String? = null,
    val inn: String? = null,
    val phone: String? = null
) {
    val fullName: String get() = buildString {
        append(lastName)
        append(" ")
        append(firstName)
        if (!middleName.isNullOrBlank()) {
            append(" ")
            append(middleName)
        }
    }
}

@Serializable
data class StudentProfileResponse(
    val student: StudentResponse,
    val stats: AttendanceStatsResponse,
    val subjectStats: List<SubjectAttendanceResponse>,
    val recentAttendance: List<AttendanceRecordResponse>
)

@Serializable
data class AttendanceStatsResponse(
    val totalLessons: Long,
    val presentCount: Long,
    val absentCount: Long,
    val attendancePercentage: Double,
    val currentStreak: Int
)

@Serializable
data class SubjectAttendanceResponse(
    val subjectId: Long,
    val subjectName: String,
    val totalLessons: Long,
    val presentCount: Long,
    val attendancePercentage: Double
)

@Serializable
data class AttendanceRecordResponse(
    val subjectName: String,
    val dateTime: String,
    val present: Boolean
)
