package aur.diploma.kmp.ui.screens.studentprofile

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
import aur.diploma.kmp.ui.components.IconCheck
import aur.diploma.kmp.ui.components.IconClose
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import aur.diploma.kmp.ui.components.AppTopBar
import aur.diploma.kmp.ui.components.AttendanceDoughnutChart
import aur.diploma.kmp.ui.components.ErrorMessage
import aur.diploma.kmp.ui.components.LoadingIndicator
import aur.diploma.kmp.ui.components.StatCard
import aur.diploma.kmp.ui.components.SubjectBarChart
import aur.diploma.kmp.ui.theme.Background
import aur.diploma.kmp.ui.theme.Border
import aur.diploma.kmp.ui.theme.Danger
import aur.diploma.kmp.ui.theme.Info
import aur.diploma.kmp.ui.theme.Primary
import aur.diploma.kmp.ui.theme.PrimaryDark
import aur.diploma.kmp.ui.theme.Success
import aur.diploma.kmp.ui.theme.Surface
import aur.diploma.kmp.ui.theme.TextMuted
import aur.diploma.kmp.ui.theme.TextPrimary
import aur.diploma.kmp.ui.theme.TextSecondary
import aur.diploma.kmp.ui.theme.Warning
import aur.diploma.kmp.util.formatDate
import aur.diploma.kmp.util.formatDateTime

@Composable
fun StudentProfileScreen(
    viewModel: StudentProfileViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        AppTopBar(
            title = "Профиль студента",
            onBack = onBack,
            showBackButton = true
        )

        when {
            state.isLoading -> LoadingIndicator()
            state.error != null -> Box(Modifier.padding(20.dp)) { ErrorMessage(state.error!!) }
            state.profile != null -> {
                val profile = state.profile!!
                val student = profile.student
                val stats = profile.stats

                LazyColumn(
                    contentPadding = PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(4.dp, RoundedCornerShape(16.dp))
                                .clip(RoundedCornerShape(16.dp))
                                .background(Surface)
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(
                                        Brush.linearGradient(listOf(Primary, PrimaryDark))
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = student.lastName.take(1).uppercase() +
                                            student.firstName.take(1).uppercase(),
                                    color = Surface,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 22.sp
                                )
                            }
                            Spacer(Modifier.height(12.dp))
                            Text(
                                text = student.fullName,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(Modifier.height(8.dp))
                            HorizontalDivider(color = Border)
                            Spacer(Modifier.height(8.dp))
                            if (student.birthDate != null) {
                                InfoRow("Дата рождения", formatDate(student.birthDate))
                            }
                            if (student.phone != null) {
                                InfoRow("Телефон", student.phone)
                            }
                            if (student.inn != null) {
                                InfoRow("ИНН", student.inn)
                            }
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            StatCard(
                                label = "Всего занятий",
                                value = "${stats.totalLessons}",
                                color = Info,
                                modifier = Modifier.weight(1f)
                            )
                            StatCard(
                                label = "Посещено",
                                value = "${stats.presentCount}",
                                color = Success,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            StatCard(
                                label = "Пропущено",
                                value = "${stats.absentCount}",
                                color = Danger,
                                modifier = Modifier.weight(1f)
                            )
                            StatCard(
                                label = "Серия посещений",
                                value = "${stats.currentStreak}",
                                color = Warning,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    item {
                        AttendanceDoughnutChart(
                            presentCount = stats.presentCount,
                            absentCount = stats.absentCount
                        )
                    }

                    item {
                        SubjectBarChart(subjects = profile.subjectStats)
                    }

                    if (profile.recentAttendance.isNotEmpty()) {
                        item {
                            Text(
                                text = "ПОСЛЕДНИЕ ПОСЕЩЕНИЯ",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextSecondary,
                                letterSpacing = 0.5.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }

                        items(profile.recentAttendance) { record ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .shadow(2.dp, RoundedCornerShape(12.dp))
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Surface)
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (record.present) {
                                    IconCheck(
                                        modifier = Modifier.size(18.dp),
                                        tint = Success
                                    )
                                } else {
                                    IconClose(
                                        modifier = Modifier.size(18.dp),
                                        tint = Danger
                                    )
                                }
                                Spacer(Modifier.width(10.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(
                                        text = record.subjectName,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = formatDateTime(record.dateTime),
                                        fontSize = 12.sp,
                                        color = TextMuted
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(
                                            if (record.present) Success.copy(alpha = 0.1f)
                                            else Danger.copy(alpha = 0.1f)
                                        )
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = if (record.present) "Присутствовал" else "Отсутствовал",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = if (record.present) Success else Danger
                                    )
                                }
                            }
                        }
                    }

                    item { Spacer(Modifier.height(20.dp)) }
                }
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 13.sp, color = TextMuted)
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
    }
}
