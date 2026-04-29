package aur.diploma.kmp.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
object LoginRoute

@Serializable
object ScheduleRoute

@Serializable
object LessonsRoute

@Serializable
object ParentLessonsRoute

@Serializable
object StudentLessonsRoute

@Serializable
object StudentStatsRoute

@Serializable
data class AttendanceRoute(val lessonId: Long)

@Serializable
data class StudentProfileRoute(val studentId: Long)

@Serializable
object ParentStudentProfileRoute
