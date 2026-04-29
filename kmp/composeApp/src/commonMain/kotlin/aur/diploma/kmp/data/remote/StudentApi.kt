package aur.diploma.kmp.data.remote

import aur.diploma.kmp.data.model.LessonResponse
import aur.diploma.kmp.data.model.StudentProfileResponse
import aur.diploma.kmp.data.model.StudentResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class StudentApi(private val client: HttpClient) {

    suspend fun getMyProfile(): StudentResponse {
        return client.get("/api/student/me").body()
    }

    suspend fun getMySchedule(date: String? = null): List<LessonResponse> {
        return client.get("/api/student/schedule") {
            date?.let { parameter("date", it) }
        }.body()
    }

    suspend fun getMyAttendance(): StudentProfileResponse {
        return client.get("/api/student/attendance").body()
    }
}
