package aur.diploma.kmp.data.remote

import aur.diploma.kmp.data.model.AttendanceResponse
import aur.diploma.kmp.data.model.BulkAttendanceRequest
import aur.diploma.kmp.data.model.LessonAttendanceSummary
import aur.diploma.kmp.data.model.LessonResponse
import aur.diploma.kmp.data.model.StudentProfileResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class TeacherApi(private val client: HttpClient) {

    suspend fun getUpcomingLessons(): List<LessonResponse> {
        return client.get("/api/teacher/lessons").body()
    }

    suspend fun getLessonAttendance(lessonId: Long): LessonAttendanceSummary {
        return client.get("/api/teacher/lessons/$lessonId/attendance").body()
    }

    suspend fun markAttendance(lessonId: Long, request: BulkAttendanceRequest): List<AttendanceResponse> {
        return client.post("/api/teacher/lessons/$lessonId/attendance") {
            setBody(request)
        }.body()
    }

    suspend fun getStudentProfile(studentId: Long): StudentProfileResponse {
        return client.get("/api/teacher/students/$studentId/profile").body()
    }
}
