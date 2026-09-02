package com.steptrack.pro.domain.model

enum class DistanceUnit { KM, MILES }
enum class AppTheme { LIGHT, DARK, SYSTEM }

data class AppSettings(
    val dailyGoal: Int = 10000,
    val distanceUnit: DistanceUnit = DistanceUnit.KM,
    val theme: AppTheme = AppTheme.SYSTEM,
    val dailyReminderEnabled: Boolean = true,
    val dailyReminderHour: Int = 18,
    val dailyReminderMinute: Int = 0,
    val goalReminderEnabled: Boolean = true,
    val goalAchievedNotifEnabled: Boolean = true,
    val strideLengthMeters: Double = 0.75,
    val onboardingCompleted: Boolean = false
)
