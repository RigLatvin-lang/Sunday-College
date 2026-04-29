package aur.diploma.kmp.data.repository

import aur.diploma.kmp.data.model.LessonResponse
import aur.diploma.kmp.data.model.StudentProfileResponse
import aur.diploma.kmp.data.model.StudentResponse
import aur.diploma.kmp.data.remote.StudentApi

class StudentRepository(private val studentApi: StudentApi) {

    suspend fun getMyProfile(): StudentResponse {
        return studentApi.getMyProfile()
    }

    suspend fun getMySchedule(date: String? = null): List<LessonResponse> {
        return studentApi.getMySchedule(date)
    }

    suspend fun getMyAttendance(): StudentProfileResponse {
        return studentApi.getMyAttendance()
    }
}
