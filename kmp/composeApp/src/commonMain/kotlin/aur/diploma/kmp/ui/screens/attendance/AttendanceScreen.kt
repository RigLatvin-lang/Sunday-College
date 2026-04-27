package aur.diploma.kmp.ui.screens.attendance

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import aur.diploma.kmp.ui.components.IconArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import aur.diploma.kmp.ui.components.AppTopBar
import aur.diploma.kmp.ui.components.ErrorMessage
import aur.diploma.kmp.ui.components.LoadingIndicator
import aur.diploma.kmp.ui.components.StudentListItem
import aur.diploma.kmp.ui.theme.Background
import aur.diploma.kmp.ui.theme.Border
import aur.diploma.kmp.ui.theme.Primary
import aur.diploma.kmp.ui.theme.Success
import aur.diploma.kmp.ui.theme.Surface
import aur.diploma.kmp.ui.theme.TextMuted
import aur.diploma.kmp.ui.theme.TextPrimary
import aur.diploma.kmp.ui.theme.TextSecondary
import aur.diploma.kmp.ui.theme.Warning
import aur.diploma.kmp.util.formatDateTime

@Composable
fun AttendanceScreen(
    viewModel: AttendanceViewModel,
    onBack: () -> Unit,
    onStudentClick: (Long) -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        AppTopBar(
            title = "Посещаемость",
            onBack = onBack,
            showBackButton = true
        )

        when {
            state.isLoading -> LoadingIndicator()
            state.error != null -> Box(Modifier.padding(20.dp)) { ErrorMessage(state.error!!) }
            state.summary != null -> {
                val summary = state.summary!!

                LazyColumn(
                    contentPadding = PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(4.dp, RoundedCornerShape(16.dp))
                                .clip(RoundedCornerShape(16.dp))
                                .background(Surface)
                                .padding(20.dp)
                        ) {
                            Text(
                                text = summary.lesson.subjectName,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = formatDateTime(summary.lesson.dateTime),
                                fontSize = 13.sp,
                                color = TextSecondary
                            )
                            Text(
                                text = "${summary.lesson.groupName} • Ауд. ${summary.lesson.classroom}",
                                fontSize = 13.sp,
                                color = TextMuted
                            )
                            Spacer(Modifier.height(12.dp))
                            HorizontalDivider(color = Border)
                            Spacer(Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                StatItem("Всего", "${summary.totalStudents}", TextPrimary)
                                StatItem("Присут.", "${summary.presentCount}", Success)
                                StatItem("Процент", "${summary.attendancePercentage.toInt()}%", Warning)
                            }
                        }
                    }

                    item {
                        Text(
                            text = "СТУДЕНТЫ",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextSecondary,
                            letterSpacing = 0.5.sp,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    items(summary.records) { record ->
                        StudentListItem(
                            name = record.studentName,
                            isPresent = state.attendanceMap[record.studentId] ?: record.present,
                            onPresentChanged = { viewModel.toggleAttendance(record.studentId, it) },
                            onProfileClick = { onStudentClick(record.studentId) }
                        )
                    }

                    item {
                        Spacer(Modifier.height(8.dp))

                        if (state.saveSuccess) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Success.copy(alpha = 0.08f))
                                    .padding(12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Сохранено!",
                                    color = Success,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 14.sp
                                )
                            }
                            Spacer(Modifier.height(8.dp))
                        }

                        Button(
                            onClick = viewModel::saveAttendance,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Primary),
                            enabled = !state.isSaving
                        ) {
                            if (state.isSaving) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Surface,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    text = "Сохранить посещаемость",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 15.sp
                                )
                            }
                        }
                        Spacer(Modifier.height(20.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String, color: androidx.compose.ui.graphics.Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = TextMuted
        )
    }
}
