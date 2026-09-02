package com.steptrack.pro.data.service

import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.ServiceCompat
import com.steptrack.pro.data.sensor.StepSensorManager
import com.steptrack.pro.domain.repository.SettingsRepository
import com.steptrack.pro.domain.usecase.SaveDailyStepUseCase
import com.steptrack.pro.notification.NotificationHelper
import com.steptrack.pro.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Foreground service that owns the sensor listener for the entire lifetime of
 * tracking. Running as a foreground service (type "health") lets the OS know
 * this work is user-visible and important, so it survives:
 *  - the screen locking,
 *  - the app being swiped away / minimized,
 *  - most OEM battery-optimization kill lists (once whitelisted by the user).
 *
 * It periodically persists the running total to Room so progress is never
 * lost to process death, and pushes live updates into the sticky notification.
 */
@AndroidEntryPoint
class StepCounterService : Service() {

    @Inject lateinit var sensorManager: StepSensorManager
    @Inject lateinit var notificationHelper: NotificationHelper
    @Inject lateinit var saveDailyStepUseCase: SaveDailyStepUseCase
    @Inject lateinit var settingsRepository: SettingsRepository

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(serviceJob)

    private var goalNotifiedToday = false
    private var progressNotifiedToday = false

    override fun onCreate() {
        super.onCreate()
        startForegroundWithType()
        sensorManager.start()
        observeSteps()
        schedulePeriodicPersist()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == Constants.ACTION_STOP_SERVICE) {
            stopSelf()
            return START_NOT_STICKY
        }
        // START_STICKY: if the OS kills the process under memory pressure,
        // it will recreate the service (without the original intent) so
        // tracking resumes automatically.
        return START_STICKY
    }

    private fun startForegroundWithType() {
        val notification = notificationHelper.buildTrackingNotification(0, Constants.DEFAULT_DAILY_GOAL)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ServiceCompat.startForeground(
                this,
                Constants.NOTIF_ID_TRACKING,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_HEALTH
            )
        } else {
            startForeground(Constants.NOTIF_ID_TRACKING, notification)
        }
    }

    private fun observeSteps() {
        serviceScope.launch {
            combine(
                sensorManager.todaySteps.distinctUntilChanged(),
                settingsRepository.settings
            ) { steps, settings -> steps to settings }
                .collect { (steps, settings) ->
                    notificationHelper.updateTrackingNotification(steps, settings.dailyGoal)

                    val remaining = settings.dailyGoal - steps
                    if (settings.goalReminderEnabled && remaining in 1..2000 && !progressNotifiedToday) {
                        notificationHelper.showGoalProgressReminder(remaining)
                        progressNotifiedToday = true
                    }
                    if (settings.goalAchievedNotifEnabled && steps >= settings.dailyGoal && !goalNotifiedToday) {
                        notificationHelper.showGoalAchievedNotification()
                        goalNotifiedToday = true
                    }
                }
        }
    }

    /** Persists the running count every 30s so a process kill never loses more than that window. */
    private fun schedulePeriodicPersist() {
        serviceScope.launch {
            while (true) {
                delay(30_000)
                val steps = sensorManager.todaySteps.first()
                saveDailyStepUseCase(steps)
            }
        }
    }

    override fun onDestroy() {
        sensorManager.stop()
        serviceJob.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
