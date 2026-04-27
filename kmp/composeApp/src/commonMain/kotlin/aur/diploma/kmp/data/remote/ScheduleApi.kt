package aur.diploma.kmp.data.remote

import aur.diploma.kmp.data.model.GroupResponse
import aur.diploma.kmp.data.model.LessonResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class ScheduleApi(private val client: HttpClient) {

    suspend fun getSchedule(date: String? = null, groupId: Long? = null, teacherId: Long? = null): List<LessonResponse> {
        return client.get("/api/schedule") {
            date?.let { parameter("date", it) }
            groupId?.let { parameter("groupId", it) }
            teacherId?.let { parameter("teacherId", it) }
        }.body()
    }

    suspend fun getGroups(): List<GroupResponse> {
        return client.get("/api/groups").body()
    }
}
