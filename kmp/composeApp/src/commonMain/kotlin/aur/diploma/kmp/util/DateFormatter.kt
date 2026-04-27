package aur.diploma.kmp.util

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun formatDateTime(isoString: String): String {
    return try {
        val parts = isoString.split("T")
        if (parts.size == 2) {
            val dateParts = parts[0].split("-")
            val timeParts = parts[1].split(":")
            if (dateParts.size == 3 && timeParts.size >= 2) {
                "${dateParts[2]}.${dateParts[1]}.${dateParts[0]} ${timeParts[0]}:${timeParts[1]}"
            } else isoString
        } else isoString
    } catch (_: Exception) {
        isoString
    }
}

fun formatDate(isoString: String): String {
    return try {
        val parts = isoString.split("T")[0].split("-")
        if (parts.size == 3) "${parts[2]}.${parts[1]}.${parts[0]}" else isoString
    } catch (_: Exception) {
        isoString
    }
}

fun formatTime(isoString: String): String {
    return try {
        val timePart = isoString.split("T").getOrNull(1) ?: return isoString
        val parts = timePart.split(":")
        if (parts.size >= 2) "${parts[0]}:${parts[1]}" else isoString
    } catch (_: Exception) {
        isoString
    }
}

fun formatEndTime(isoString: String, durationMinutes: Int?): String {
    if (durationMinutes == null) return ""
    return try {
        val timePart = isoString.split("T").getOrNull(1) ?: return ""
        val parts = timePart.split(":")
        if (parts.size >= 2) {
            val hours = parts[0].toInt()
            val minutes = parts[1].toInt()
            val totalMinutes = hours * 60 + minutes + durationMinutes
            val endH = (totalMinutes / 60) % 24
            val endM = totalMinutes % 60
            "${endH.toString().padStart(2, '0')}:${endM.toString().padStart(2, '0')}"
        } else ""
    } catch (_: Exception) {
        ""
    }
}

fun extractDateKey(isoString: String): String {
    return try {
        isoString.split("T")[0]
    } catch (_: Exception) {
        isoString
    }
}

fun todayIsoDate(): String {
    return Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .date
        .toString()
}
