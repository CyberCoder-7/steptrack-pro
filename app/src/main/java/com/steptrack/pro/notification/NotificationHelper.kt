package com.steptrack.pro.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.steptrack.pro.MainActivity
import com.steptrack.pro.R
import com.steptrack.pro.util.Constants
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Centralizes all notification creation: the persistent tracking notification
 * (required for the foreground service), goal-progress nudges, goal-achieved
 * celebrations, and daily walk reminders.
 */
@Singleton
class NotificationHelper @Inject constructor(
    private val context: Context
) {
    private val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createChannels()
    }

    private fun createChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val tracking = NotificationChannel(
                Constants.NOTIF_CHANNEL_TRACKING_ID,
                context.getString(R.string.notif_channel_tracking),
                NotificationManager.IMPORTANCE_LOW // silent, ongoing
            )
            val reminders = NotificationChannel(
                Constants.NOTIF_CHANNEL_REMINDERS_ID,
                context.getString(R.string.notif_channel_reminders),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            manager.createNotificationChannel(tracking)
            manager.createNotificationChannel(reminders)
        }
    }

    private fun contentIntent(): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        return PendingIntent.getActivity(context, 0, intent, flags)
    }

    /** Persistent, low-priority notification required to keep the foreground service alive. */
    fun buildTrackingNotification(steps: Int, goal: Int): android.app.Notification {
        val percent = if (goal > 0) ((steps.toFloat() / goal) * 100).toInt().coerceIn(0, 100) else 0
        return NotificationCompat.Builder(context, Constants.NOTIF_CHANNEL_TRACKING_ID)
            .setContentTitle(context.getString(R.string.notif_tracking_title))
            .setContentText("$steps / $goal steps ($percent%)")
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setContentIntent(contentIntent())
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    fun updateTrackingNotification(steps: Int, goal: Int) {
        manager.notify(Constants.NOTIF_ID_TRACKING, buildTrackingNotification(steps, goal))
    }

    fun showGoalProgressReminder(remainingSteps: Int) {
        val notification = NotificationCompat.Builder(context, Constants.NOTIF_CHANNEL_REMINDERS_ID)
            .setContentTitle("Almost there!")
            .setContentText("Only ${"%,d".format(remainingSteps)} steps left to reach your goal.")
            .setSmallIcon(android.R.drawable.ic_menu_directions)
            .setAutoCancel(true)
            .setContentIntent(contentIntent())
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        manager.notify(Constants.NOTIF_ID_GOAL_PROGRESS, notification)
    }

    fun showGoalAchievedNotification() {
        val notification = NotificationCompat.Builder(context, Constants.NOTIF_CHANNEL_REMINDERS_ID)
            .setContentTitle("Goal achieved! 🎉")
            .setContentText("Congratulations! You reached today's step goal.")
            .setSmallIcon(android.R.drawable.ic_menu_myplaces)
            .setAutoCancel(true)
            .setContentIntent(contentIntent())
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
        manager.notify(Constants.NOTIF_ID_GOAL_ACHIEVED, notification)
    }

    fun showDailyReminder() {
        val notification = NotificationCompat.Builder(context, Constants.NOTIF_CHANNEL_REMINDERS_ID)
            .setContentTitle("StepTrack Pro")
            .setContentText(context.getString(R.string.daily_reminder_text))
            .setSmallIcon(android.R.drawable.ic_menu_compass)
            .setAutoCancel(true)
            .setContentIntent(contentIntent())
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        manager.notify(Constants.NOTIF_ID_DAILY_REMINDER, notification)
    }
}
