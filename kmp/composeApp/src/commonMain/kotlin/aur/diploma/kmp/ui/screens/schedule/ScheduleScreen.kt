package aur.diploma.kmp.ui.screens.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import aur.diploma.kmp.ui.components.IconArrowBack
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
import aur.diploma.kmp.ui.theme.Primary
import aur.diploma.kmp.ui.theme.Primary50
import aur.diploma.kmp.ui.theme.PrimaryDark
import aur.diploma.kmp.ui.theme.Surface
import aur.diploma.kmp.ui.theme.TextMuted
import aur.diploma.kmp.ui.theme.TextPrimary
import aur.diploma.kmp.util.extractDateKey
import aur.diploma.kmp.util.formatDate

@Composable
fun ScheduleScreen(
    viewModel: ScheduleViewModel,
    onBack: () -> Unit,
    onLessonClick: ((Long) -> Unit)? = null
) {
    val state by viewModel.uiState.collectAsState()
    val groupedLessons = state.lessons.groupBy { extractDateKey(it.dateTime) }.toSortedMap()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        AppTopBar(
            title = "Расписание",
            onBack = onBack,
            showBackButton = true
        )

        if (state.groups.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Surface)
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    label = "Все группы",
                    selected = state.selectedGroupId == null,
                    onClick = { viewModel.onGroupSelected(null) }
                )
                state.groups.forEach { group ->
                    FilterChip(
                        label = group.name,
                        selected = state.selectedGroupId == group.id,
                        onClick = { viewModel.onGroupSelected(group.id) }
                    )
                }
            }
        }

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
                    Text(
                        text = "Нет занятий",
                        color = TextMuted,
                        fontSize = 14.sp
                    )
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
                                onClick = { onLessonClick?.invoke(lesson.id) }
                            )
                        }
                        item { Spacer(Modifier.height(8.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (selected) Primary else Primary50)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = if (selected) Surface else PrimaryDark
        )
    }
}
