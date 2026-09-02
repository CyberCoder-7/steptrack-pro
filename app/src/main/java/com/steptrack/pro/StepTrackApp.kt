package com.steptrack.pro

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.steptrack.pro.domain.repository.SettingsRepository
import com.steptrack.pro.notification.ReminderScheduler
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class StepTrackApp : Application(), Configuration.Provider {

    @Inject lateinit var workerFactory: HiltWorkerFactory
    @Inject lateinit var settingsRepository: SettingsRepository
    @Inject lateinit var reminderScheduler: ReminderScheduler

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        // WorkManager's own unique-work persistence survives process death, but we
        // reconcile here too: if the last stored setting was "enabled", make sure the
        // periodic request exists (cheap no-op via ExistingPeriodicWorkPolicy.UPDATE
        // if it's already scheduled).
        CoroutineScope(Dispatchers.Default).launch {
            val settings = settingsRepository.settings.first()
            if (settings.dailyReminderEnabled) {
                reminderScheduler.scheduleDailyReminder(settings.dailyReminderHour, settings.dailyReminderMinute)
            }
        }
    }
}
