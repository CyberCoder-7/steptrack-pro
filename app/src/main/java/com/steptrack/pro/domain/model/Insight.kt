package com.steptrack.pro.domain.model

data class Insight(
    val id: String,
    val message: String,
    val icon: String = "insights"
)

data class StreakInfo(
    val currentStreak: Int,
    val longestStreak: Int,
    val weeklySuccessRatePercent: Int
)
