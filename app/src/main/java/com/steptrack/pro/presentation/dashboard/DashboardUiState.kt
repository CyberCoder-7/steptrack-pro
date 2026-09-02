package com.steptrack.pro.presentation.dashboard

import com.steptrack.pro.domain.model.Achievement
import com.steptrack.pro.domain.model.DistanceUnit

data class DashboardUiState(
    val isLoading: Boolean = true,
    val steps: Int = 0,
    val goal: Int = 10000,
    val progressPercent: Float = 0f,
    val distanceMeters: Double = 0.0,
    val calories: Double = 0.0,
    val activeMinutes: Int = 0,
    val floors: Int = 0,
    val currentStreak: Int = 0,
    val distanceUnit: DistanceUnit = DistanceUnit.KM,
    val recentAchievements: List<Achievement> = emptyList(),
    val greeting: String = "Hello",
    val dateLabel: String = "",
    val sensorUnavailable: Boolean = false,
    val hasActivityPermission: Boolean = true,
    val needsBatteryOptimizationExemption: Boolean = false
)
