package aur.diploma.kmp.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import aur.diploma.kmp.data.model.SubjectAttendanceResponse
import aur.diploma.kmp.ui.theme.ChartPink
import aur.diploma.kmp.ui.theme.ChartPurple
import aur.diploma.kmp.ui.theme.Info
import aur.diploma.kmp.ui.theme.Primary
import aur.diploma.kmp.ui.theme.Success
import aur.diploma.kmp.ui.theme.Surface
import aur.diploma.kmp.ui.theme.TextMuted
import aur.diploma.kmp.ui.theme.TextPrimary
import aur.diploma.kmp.ui.theme.TextSecondary
import aur.diploma.kmp.ui.theme.Warning

private val barColors = listOf(Primary, Info, Success, Warning, ChartPurple, ChartPink)

@Composable
fun SubjectBarChart(
    subjects: List<SubjectAttendanceResponse>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(Surface)
            .padding(20.dp)
    ) {
        Text(
            text = "Посещаемость по предметам",
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )
        Spacer(Modifier.height(16.dp))

        if (subjects.isEmpty()) {
            Text("Нет данных", fontSize = 13.sp, color = TextMuted)
        } else {
            subjects.forEachIndexed { index, subject ->
                val color = barColors[index % barColors.size]
                val percentage = subject.attendancePercentage.toFloat() / 100f

                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = subject.subjectName,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextSecondary,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "${subject.attendancePercentage.toInt()}%",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = color
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                    ) {
                        drawRect(
                            color = color.copy(alpha = 0.12f),
                            size = size
                        )
                        drawRect(
                            color = color,
                            size = Size(size.width * percentage, size.height)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AttendanceDoughnutChart(
    presentCount: Long,
    absentCount: Long,
    modifier: Modifier = Modifier
) {
    val total = presentCount + absentCount
    val presentAngle = if (total > 0) (presentCount.toFloat() / total) * 360f else 0f
    val absentAngle = if (total > 0) (absentCount.toFloat() / total) * 360f else 0f
    val percentage = if (total > 0) (presentCount * 100 / total).toInt() else 0

    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(Surface)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Общая посещаемость",
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))

        Box(contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.size(160.dp)) {
                val strokeWidth = 24.dp.toPx()
                val diameter = size.minDimension - strokeWidth
                val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)
                val arcSize = Size(diameter, diameter)

                drawArc(
                    color = Success,
                    startAngle = -90f,
                    sweepAngle = presentAngle,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
                drawArc(
                    color = Color(0xFFEF4444),
                    startAngle = -90f + presentAngle,
                    sweepAngle = absentAngle,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$percentage%",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
        }

        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            LegendItem(color = Success, label = "Присутствовал", value = "$presentCount")
            LegendItem(color = Color(0xFFEF4444), label = "Отсутствовал", value = "$absentCount")
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(Modifier.width(6.dp))
        Column {
            Text(text = label, fontSize = 11.sp, color = TextMuted)
            Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
        }
    }
}
