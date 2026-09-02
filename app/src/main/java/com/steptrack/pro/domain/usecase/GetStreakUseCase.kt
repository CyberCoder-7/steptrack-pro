package com.steptrack.pro.domain.usecase

import com.steptrack.pro.domain.model.StreakInfo
import com.steptrack.pro.domain.repository.StepRepository
import com.steptrack.pro.util.DateUtils
import javax.inject.Inject

/**
 * Computes streak stats by walking backward day-by-day through history
 * until the goal-achieved chain breaks. Also derives the weekly success rate
 * (how many of the last 7 days met the goal).
 */
class GetStreakUseCase @Inject constructor(
    private val stepRepository: StepRepository
) {
    suspend fun currentStreak(): Int {
        val history = stepRepository.getHistory(DateUtils.daysAgo(400), DateUtils.today())
            .associateBy { it.date }
        var streak = 0
        var cursor = DateUtils.parse(DateUtils.today())
        while (true) {
            val day = history[DateUtils.format(cursor)] ?: break
            if (!day.isGoalAchieved) break
            streak++
            cursor = cursor.minusDays(1)
        }
        return streak
    }

    suspend fun invoke(): StreakInfo {
        val history = stepRepository.getHistory(DateUtils.daysAgo(400), DateUtils.today())
            .associateBy { it.date }

        var current = 0
        var longest = 0
        var running = 0
        var cursor = DateUtils.parse(DateUtils.daysAgo(400))
        val today = DateUtils.parse(DateUtils.today())
        var stillCounting = true

        // Walk forward to find the longest streak across all history.
        while (!cursor.isAfter(today)) {
            val day = history[DateUtils.format(cursor)]
            if (day?.isGoalAchieved == true) {
                running++
                longest = maxOf(longest, running)
            } else {
                running = 0
            }
            cursor = cursor.plusDays(1)
        }

        current = currentStreak()

        val last7 = (0..6).map { history[DateUtils.daysAgo(it.toLong())] }
        val successCount = last7.count { it?.isGoalAchieved == true }
        val weeklyRate = ((successCount / 7.0) * 100).toInt()

        return StreakInfo(
            currentStreak = current,
            longestStreak = longest,
            weeklySuccessRatePercent = weeklyRate
        )
    }
}
