package aur.diploma.kmp.di

import aur.diploma.kmp.data.remote.AuthApi
import aur.diploma.kmp.data.remote.ParentApi
import aur.diploma.kmp.data.remote.ScheduleApi
import aur.diploma.kmp.data.remote.StudentApi
import aur.diploma.kmp.data.remote.TeacherApi
import aur.diploma.kmp.data.remote.TokenStorage
import aur.diploma.kmp.data.remote.createHttpClient
import aur.diploma.kmp.data.repository.AuthRepository
import aur.diploma.kmp.data.repository.ParentRepository
import aur.diploma.kmp.data.repository.ScheduleRepository
import aur.diploma.kmp.data.repository.StudentRepository
import aur.diploma.kmp.data.repository.TeacherRepository
import aur.diploma.kmp.notifications.AttendanceNotifier
import aur.diploma.kmp.notifications.ParentAttendanceMonitor
import aur.diploma.kmp.ui.screens.attendance.AttendanceViewModel
import aur.diploma.kmp.ui.screens.lessons.LessonsViewModel
import aur.diploma.kmp.ui.screens.login.LoginViewModel
import aur.diploma.kmp.ui.screens.parent.ParentLessonsViewModel
import aur.diploma.kmp.ui.screens.parent.ParentStudentProfileViewModel
import aur.diploma.kmp.ui.screens.schedule.ScheduleViewModel
import aur.diploma.kmp.ui.screens.student.StudentLessonsViewModel
import aur.diploma.kmp.ui.screens.student.StudentStatsViewModel
import aur.diploma.kmp.ui.screens.studentprofile.StudentProfileViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { TokenStorage() }
    single { createHttpClient(get()) }
    single { AuthApi(get()) }
    single { ParentApi(get()) }
    single { ScheduleApi(get()) }
    single { StudentApi(get()) }
    single { TeacherApi(get()) }
    single { AuthRepository(get(), get()) }
    single { ParentRepository(get()) }
    single { ScheduleRepository(get()) }
    single { StudentRepository(get()) }
    single { TeacherRepository(get()) }
    single { AttendanceNotifier() }
    single { ParentAttendanceMonitor(get(), get()) }

    viewModel { LoginViewModel(get()) }
    viewModel { ParentLessonsViewModel(get()) }
    viewModel { ParentStudentProfileViewModel(get()) }
    viewModel { ScheduleViewModel(get()) }
    viewModel { StudentLessonsViewModel(get()) }
    viewModel { StudentStatsViewModel(get()) }
    viewModel { LessonsViewModel(get()) }
    viewModel { params -> AttendanceViewModel(params.get(), get()) }
    viewModel { params -> StudentProfileViewModel(params.get(), get()) }
}
