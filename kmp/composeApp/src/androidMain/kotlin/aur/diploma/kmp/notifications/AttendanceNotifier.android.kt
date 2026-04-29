package aur.diploma.kmp.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import aur.diploma.kmp.R
import aur.diploma.kmp.data.remote.requireAppContext

actual class AttendanceNotifier actual constructor() {
    private val context = requireAppContext()

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Attendance updates",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications about attendance updates for a child"
            }
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    actual fun show(notification: AttendanceStatusNotification) {
        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(notification.title)
            .setContentText(notification.message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(notification.message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        NotificationManagerCompat.from(context).notify(nextNotificationId(), builder.build())
    }

    private companion object {
        const val CHANNEL_ID = "attendance_updates"
        var notificationCounter = 1000

        fun nextNotificationId(): Int {
            notificationCounter += 1
            return notificationCounter
        }
    }
}
