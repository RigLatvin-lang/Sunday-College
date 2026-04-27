package aur.diploma.kmp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class LessonResponse(
    val id: Long,
    val subjectName: String,
    val dateTime: String,
    val groupName: String,
    val groupId: Long,
    val teacherId: Long? = null,
    val teacherName: String? = null,
    val classroom: String,
    val durationMinutes: Int? = null
)
