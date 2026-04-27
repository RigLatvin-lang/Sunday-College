package aur.diploma.kmp.ui.screens.lessons

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import aur.diploma.kmp.ui.components.IconExitToApp
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import aur.diploma.kmp.ui.components.AppTopBar
import aur.diploma.kmp.ui.components.ErrorMessage
import aur.diploma.kmp.ui.components.LessonCard
import aur.diploma.kmp.ui.components.LoadingIndicator
import aur.diploma.kmp.ui.theme.Background
import aur.diploma.kmp.ui.theme.Danger
import aur.diploma.kmp.ui.theme.TextMuted
import aur.diploma.kmp.ui.theme.TextPrimary
import aur.diploma.kmp.util.extractDateKey
import aur.diploma.kmp.util.formatDate

@Composable
fun LessonsScreen(
    viewModel: LessonsViewModel,
    onLessonClick: (Long) -> Unit,
    onLogout: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val groupedLessons = state.lessons.groupBy { extractDateKey(it.dateTime) }.toSortedMap()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        AppTopBar(
            title = "Мои занятия",
            actions = {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable(onClick = onLogout),
                    contentAlignment = Alignment.Center
                ) {
                    IconExitToApp(
                        modifier = Modifier.size(20.dp),
                        tint = Danger
                    )
                }
            }
        )

        when {
            state.isLoading -> LoadingIndicator()
            state.error != null -> {
                Box(Modifier.padding(20.dp)) {
                    ErrorMessage(state.error!!)
                }
            }
            state.lessons.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Нет предстоящих занятий",
                            color = TextMuted,
                            fontSize = 14.sp
                        )
                    }
                }
            }
            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    groupedLessons.entries.forEach { entry ->
                        item {
                            Text(
                                text = formatDate(entry.key),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        items(entry.value) { lesson ->
                            LessonCard(
                                lesson = lesson,
                                onClick = { onLessonClick(lesson.id) }
                            )
                        }
                        item { Spacer(Modifier.height(8.dp)) }
                    }
                }
            }
        }
    }
}
