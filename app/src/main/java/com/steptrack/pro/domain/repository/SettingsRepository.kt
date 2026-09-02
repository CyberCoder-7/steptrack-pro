package com.steptrack.pro.domain.repository

import com.steptrack.pro.domain.model.AppSettings
import com.steptrack.pro.domain.model.AppTheme
import com.steptrack.pro.domain.model.DistanceUnit
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val settings: Flow<AppSettings>
    suspend fun setDailyGoal(goal: Int)
    suspend fun setDistanceUnit(unit: DistanceUnit)
    suspend fun setTheme(theme: AppTheme)
    suspend fun setDailyReminder(enabled: Boolean, hour: Int, minute: Int)
    suspend fun setGoalReminderEnabled(enabled: Boolean)
    suspend fun setGoalAchievedNotifEnabled(enabled: Boolean)
    suspend fun setStrideLength(meters: Double)
    suspend fun setOnboardingCompleted(completed: Boolean)
}
