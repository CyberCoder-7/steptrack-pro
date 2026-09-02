package com.steptrack.pro.data.repository

import com.steptrack.pro.data.datastore.PreferencesManager
import com.steptrack.pro.domain.model.AppSettings
import com.steptrack.pro.domain.model.AppTheme
import com.steptrack.pro.domain.model.DistanceUnit
import com.steptrack.pro.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val preferencesManager: PreferencesManager
) : SettingsRepository {

    override val settings: Flow<AppSettings> = preferencesManager.settingsFlow

    override suspend fun setDailyGoal(goal: Int) = preferencesManager.updateDailyGoal(goal)

    override suspend fun setDistanceUnit(unit: DistanceUnit) = preferencesManager.updateDistanceUnit(unit)

    override suspend fun setTheme(theme: AppTheme) = preferencesManager.updateTheme(theme)

    override suspend fun setDailyReminder(enabled: Boolean, hour: Int, minute: Int) =
        preferencesManager.updateDailyReminder(enabled, hour, minute)

    override suspend fun setGoalReminderEnabled(enabled: Boolean) =
        preferencesManager.updateGoalReminderEnabled(enabled)

    override suspend fun setGoalAchievedNotifEnabled(enabled: Boolean) =
        preferencesManager.updateGoalAchievedNotifEnabled(enabled)

    override suspend fun setStrideLength(meters: Double) = preferencesManager.updateStrideLength(meters)

    override suspend fun setOnboardingCompleted(completed: Boolean) =
        preferencesManager.setOnboardingCompleted(completed)
}
