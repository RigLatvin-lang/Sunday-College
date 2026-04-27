package aur.diploma.kmp.data.repository

import aur.diploma.kmp.data.model.LessonResponse
import aur.diploma.kmp.data.model.StudentProfileResponse
import aur.diploma.kmp.data.remote.ParentApi

class ParentRepository(private val parentApi: ParentApi) {

    suspend fun getChildSchedule(): List<LessonResponse> {
        return parentApi.getChildSchedule()
    }

    suspend fun getChildAttendance(): StudentProfileResponse {
        return parentApi.getChildAttendance()
    }
}
