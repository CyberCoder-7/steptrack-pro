package com.steptrack.pro.domain.model

enum class AchievementType {
    FIRST_1000_STEPS,
    FIRST_5000_STEPS,
    FIRST_10000_STEPS,
    STREAK_7_DAYS,
    STREAK_30_DAYS,
    LIFETIME_100K_STEPS
}

data class Achievement(
    val id: Int = 0,
    val type: AchievementType,
    val title: String,
    val description: String,
    val iconName: String,
    val unlockedDate: String?,   // null if not yet unlocked
    val isUnlocked: Boolean
)
