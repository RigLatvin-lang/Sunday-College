package aur.diploma.kmp.data.repository

import aur.diploma.kmp.data.model.AttendanceResponse
import aur.diploma.kmp.data.model.BulkAttendanceRequest
import aur.diploma.kmp.data.model.LessonAttendanceSummary
import aur.diploma.kmp.data.model.LessonResponse
import aur.diploma.kmp.data.model.MarkAttendanceRequest
import aur.diploma.kmp.data.model.StudentProfileResponse
import aur.diploma.kmp.data.remote.TeacherApi

class TeacherRepository(private val teacherApi: TeacherApi) {

    suspend fun getUpcomingLessons(): List<LessonResponse> {
        println("📚 TeacherRepository.getUpcomingLessons() вызван")
        return try {
            val result = teacherApi.getUpcomingLessons()
            println("✅ TeacherRepository.getUpcomingLessons() успешно: получено ${result.size} занятий")
            result
        } catch (e: Exception) {
            println("❌ TeacherRepository.getUpcomingLessons() ошибка: ${e.message}")
            println("📋 Stack trace: ${e.stackTraceToString()}")
            throw e
        }
    }

    suspend fun getLessonAttendance(lessonId: Long): LessonAttendanceSummary {
        println("📊 TeacherRepository.getLessonAttendance() вызван: lessonId=$lessonId")
        return try {
            val result = teacherApi.getLessonAttendance(lessonId)
            println("✅ TeacherRepository.getLessonAttendance() успешно: студентов=${result.totalStudents}")
            result
        } catch (e: Exception) {
            println("❌ TeacherRepository.getLessonAttendance() ошибка: ${e.message}")
            throw e
        }
    }

    suspend fun markAttendance(
        lessonId: Long,
        records: List<MarkAttendanceRequest>
    ): List<AttendanceResponse> {
        println("✏️ TeacherRepository.markAttendance() вызван: lessonId=$lessonId, records=${records.size}")
        return try {
            val result = teacherApi.markAttendance(lessonId, BulkAttendanceRequest(records))
            println("✅ TeacherRepository.markAttendance() успешно")
            result
        } catch (e: Exception) {
            println("❌ TeacherRepository.markAttendance() ошибка: ${e.message}")
            throw e
        }
    }

    suspend fun getStudentProfile(studentId: Long): StudentProfileResponse {
        println("👨‍🎓 TeacherRepository.getStudentProfile() вызван: studentId=$studentId")
        return try {
            val result = teacherApi.getStudentProfile(studentId)
            println("✅ TeacherRepository.getStudentProfile() успешно: ${result.student.fullName}")
            result
        } catch (e: Exception) {
            println("❌ TeacherRepository.getStudentProfile() ошибка: ${e.message}")
            throw e
        }
    }
}
