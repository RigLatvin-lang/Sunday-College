package aur.diploma.kmp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import aur.diploma.kmp.ui.navigation.AppRole
import aur.diploma.kmp.ui.navigation.AttendanceRoute
import aur.diploma.kmp.ui.navigation.LessonsRoute
import aur.diploma.kmp.ui.navigation.LoginRoute
import aur.diploma.kmp.ui.navigation.ParentLessonsRoute
import aur.diploma.kmp.ui.navigation.ParentStudentProfileRoute
import aur.diploma.kmp.ui.navigation.ScheduleRoute
import aur.diploma.kmp.ui.navigation.StudentProfileRoute
import aur.diploma.kmp.ui.screens.attendance.AttendanceScreen
import aur.diploma.kmp.ui.screens.attendance.AttendanceViewModel
import aur.diploma.kmp.ui.screens.lessons.LessonsScreen
import aur.diploma.kmp.ui.screens.lessons.LessonsViewModel
import aur.diploma.kmp.ui.screens.login.LoginScreen
import aur.diploma.kmp.ui.screens.login.LoginViewModel
import aur.diploma.kmp.ui.screens.parent.ParentLessonsScreen
import aur.diploma.kmp.ui.screens.parent.ParentLessonsViewModel
import aur.diploma.kmp.ui.screens.parent.ParentStudentProfileScreen
import aur.diploma.kmp.ui.screens.parent.ParentStudentProfileViewModel
import aur.diploma.kmp.ui.screens.schedule.ScheduleScreen
import aur.diploma.kmp.ui.screens.schedule.ScheduleViewModel
import aur.diploma.kmp.ui.screens.studentprofile.StudentProfileScreen
import aur.diploma.kmp.ui.screens.studentprofile.StudentProfileViewModel
import aur.diploma.kmp.ui.theme.AppTheme
import org.koin.compose.KoinContext
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun App() {
    KoinContext {
        AppTheme {
            val navController = rememberNavController()
            val loginViewModel: LoginViewModel = koinViewModel()
            var isStartResolved by remember { mutableStateOf(false) }

            LaunchedEffect(Unit) {
                if (loginViewModel.isLoggedIn) {
                    when (loginViewModel.restoreRole()) {
                        AppRole.TEACHER -> {
                            navController.navigate(LessonsRoute) {
                                popUpTo(LoginRoute) { inclusive = true }
                            }
                        }
                        AppRole.PARENT -> {
                            navController.navigate(ParentLessonsRoute) {
                                popUpTo(LoginRoute) { inclusive = true }
                            }
                        }
                        null -> Unit
                    }
                }
                isStartResolved = true
            }

            if (!isStartResolved) {
                return@AppTheme
            }

            NavHost(
                navController = navController,
                startDestination = LoginRoute
            ) {
                composable<LoginRoute> {
                    LoginScreen(
                        viewModel = loginViewModel,
                        onLoginSuccess = { appRole ->
                            when (appRole) {
                                AppRole.TEACHER -> {
                                    navController.navigate(LessonsRoute) {
                                        popUpTo(LoginRoute) { inclusive = true }
                                    }
                                }
                                AppRole.PARENT -> {
                                    navController.navigate(ParentLessonsRoute) {
                                        popUpTo(LoginRoute) { inclusive = true }
                                    }
                                }
                            }
                        },
                        onScheduleClick = {
                            navController.navigate(ScheduleRoute)
                        }
                    )
                }

                composable<ScheduleRoute> {
                    val viewModel: ScheduleViewModel = koinViewModel()
                    ScheduleScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() },
                        onLessonClick = null
                    )
                }

                composable<LessonsRoute> {
                    val viewModel: LessonsViewModel = koinViewModel()
                    LessonsScreen(
                        viewModel = viewModel,
                        onLessonClick = { lessonId ->
                            navController.navigate(AttendanceRoute(lessonId))
                        },
                        onLogout = {
                            loginViewModel.logout()
                            navController.navigate(LoginRoute) {
                                popUpTo(LessonsRoute) { inclusive = true }
                            }
                        }
                    )
                }

                composable<ParentLessonsRoute> {
                    val viewModel: ParentLessonsViewModel = koinViewModel()
                    ParentLessonsScreen(
                        viewModel = viewModel,
                        onProfileClick = {
                            navController.navigate(ParentStudentProfileRoute)
                        },
                        onLogout = {
                            loginViewModel.logout()
                            navController.navigate(LoginRoute) {
                                popUpTo(ParentLessonsRoute) { inclusive = true }
                            }
                        }
                    )
                }

                composable<AttendanceRoute> { backStackEntry ->
                    val route: AttendanceRoute = backStackEntry.toRoute()
                    val viewModel: AttendanceViewModel = koinViewModel(
                        parameters = { parametersOf(route.lessonId) }
                    )
                    AttendanceScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() },
                        onStudentClick = { studentId ->
                            navController.navigate(StudentProfileRoute(studentId))
                        }
                    )
                }

                composable<StudentProfileRoute> { backStackEntry ->
                    val route: StudentProfileRoute = backStackEntry.toRoute()
                    val viewModel: StudentProfileViewModel = koinViewModel(
                        parameters = { parametersOf(route.studentId) }
                    )
                    StudentProfileScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() }
                    )
                }

                composable<ParentStudentProfileRoute> {
                    val viewModel: ParentStudentProfileViewModel = koinViewModel()
                    ParentStudentProfileScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
