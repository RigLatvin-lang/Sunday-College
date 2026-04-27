package aur.diploma.kmp.data.remote

import aur.diploma.kmp.data.model.LessonResponse
import aur.diploma.kmp.data.model.StudentProfileResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class ParentApi(private val client: HttpClient) {

    suspend fun getChildSchedule(): List<LessonResponse> {
        return client.get("/api/parent/child/schedule").body()
    }

    suspend fun getChildAttendance(): StudentProfileResponse {
        return client.get("/api/parent/child/attendance").body()
    }
}
