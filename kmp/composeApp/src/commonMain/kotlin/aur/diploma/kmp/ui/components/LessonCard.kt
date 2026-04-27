package aur.diploma.kmp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import aur.diploma.kmp.data.model.LessonResponse
import aur.diploma.kmp.ui.theme.Primary
import aur.diploma.kmp.ui.theme.PrimaryDark
import aur.diploma.kmp.ui.theme.Surface
import aur.diploma.kmp.ui.theme.TextMuted
import aur.diploma.kmp.ui.theme.TextPrimary
import aur.diploma.kmp.ui.theme.TextSecondary
import aur.diploma.kmp.util.formatEndTime
import aur.diploma.kmp.util.formatTime

@Composable
fun LessonCard(
    lesson: LessonResponse,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(Surface)
            .clickable(onClick = onClick)
            .padding(18.dp)
            .height(IntrinsicSize.Min)
    ) {
        Column(
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = formatTime(lesson.dateTime),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            val endTime = formatEndTime(lesson.dateTime, lesson.durationMinutes)
            if (endTime.isNotEmpty()) {
                Text(
                    text = endTime,
                    fontSize = 13.sp,
                    color = TextMuted
                )
            }
        }
        Spacer(Modifier.width(16.dp))
        Box(
            modifier = Modifier
                .width(3.dp)
                .fillMaxHeight()
                .clip(RoundedCornerShape(2.dp))
                .background(
                    Brush.verticalGradient(listOf(Primary, PrimaryDark))
                )
        )
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = lesson.subjectName,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = lesson.groupName,
                fontSize = 13.sp,
                color = TextSecondary
            )
            if (!lesson.teacherName.isNullOrBlank()) {
                Text(
                    text = lesson.teacherName,
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
            Row {
                Text(
                    text = "Ауд. ${lesson.classroom}",
                    fontSize = 12.sp,
                    color = TextMuted
                )
                if (lesson.durationMinutes != null) {
                    Text(
                        text = "  •  ${lesson.durationMinutes} мин",
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                }
            }
        }
    }
}
