package com.steptrack.pro.domain.model

/**
 * Domain model representing a single day's step activity.
 * Kept separate from the Room entity so the UI/domain layers never
 * depend on persistence details.
 */
data class DailyStep(
    val date: String,          // yyyy-MM-dd
    val steps: Int,
    val goal: Int,
    val distanceMeters: Double,
    val caloriesBurned: Double,
    val activeMinutes: Int,
    val floorsClimbed: Int
) {
    val progressPercent: Float
        get() = if (goal <= 0) 0f else (steps.toFloat() / goal.toFloat()).coerceIn(0f, 1f)

    val isGoalAchieved: Boolean
        get() = steps >= goal
}
