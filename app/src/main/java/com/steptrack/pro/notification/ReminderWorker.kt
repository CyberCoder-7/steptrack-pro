package com.steptrack.pro.notification

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * Fires the daily "time for a walk" reminder. Scheduled via WorkManager
 * (survives reboot/doze better than a raw AlarmManager for a non-exact daily nudge).
 */
@HiltWorker
class ReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val notificationHelper: NotificationHelper
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        notificationHelper.showDailyReminder()
        return Result.success()
    }

    companion object {
        const val WORK_NAME = "daily_reminder_work"
    }
}
