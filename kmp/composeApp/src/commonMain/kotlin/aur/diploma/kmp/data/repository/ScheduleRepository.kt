package aur.diploma.kmp.data.repository

import aur.diploma.kmp.data.model.GroupResponse
import aur.diploma.kmp.data.model.LessonResponse
import aur.diploma.kmp.data.remote.ScheduleApi

class ScheduleRepository(private val scheduleApi: ScheduleApi) {

    suspend fun getSchedule(date: String? = null, groupId: Long? = null, teacherId: Long? = null): List<LessonResponse> {
        println("📅 ScheduleRepository.getSchedule() вызван: date=$date, groupId=$groupId, teacherId=$teacherId")
        return try {
            val result = scheduleApi.getSchedule(date, groupId, teacherId)
            println("✅ ScheduleRepository.getSchedule() успешно: получено ${result.size} занятий")
            result
        } catch (e: Exception) {
            println("❌ ScheduleRepository.getSchedule() ошибка: ${e.message}")
            println("📋 Stack trace: ${e.stackTraceToString()}")
            throw e
        }
    }

    suspend fun getGroups(): List<GroupResponse> {
        println("👥 ScheduleRepository.getGroups() вызван")
        return try {
            val result = scheduleApi.getGroups()
            println("✅ ScheduleRepository.getGroups() успешно: получено ${result.size} групп")
            result
        } catch (e: Exception) {
            println("❌ ScheduleRepository.getGroups() ошибка: ${e.message}")
            throw e
        }
    }
}
