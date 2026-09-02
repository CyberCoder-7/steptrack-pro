package com.steptrack.pro.domain.usecase

import com.steptrack.pro.domain.model.Insight
import com.steptrack.pro.domain.repository.StepRepository
import com.steptrack.pro.util.DateUtils
import javax.inject.Inject
import kotlin.math.roundToInt

/**
 * Derives friendly, human-readable insights purely from stored history —
 * no ML needed, just comparative arithmetic over recent days.
 */
class GenerateInsightsUseCase @Inject constructor(
    private val stepRepository: StepRepository,
    private val getStreakUseCase: GetStreakUseCase
) {
    suspend operator fun invoke(): List<Insight> {
        val insights = mutableListOf<Insight>()
        val history = stepRepository.getHistory(DateUtils.daysAgo(30), DateUtils.today())
            .associateBy { it.date }

        val today = history[DateUtils.today()]
        val yesterday = history[DateUtils.daysAgo(1)]

        if (today != null && yesterday != null && yesterday.steps > 0) {
            val diffPercent = (((today.steps - yesterday.steps).toDouble() / yesterday.steps) * 100).roundToInt()
            if (diffPercent > 0) {
                insights.add(Insight("vs_yesterday", "You walked $diffPercent% more than yesterday.", "trending_up"))
            } else if (diffPercent < 0) {
                insights.add(Insight("vs_yesterday", "You walked ${-diffPercent}% less than yesterday.", "trending_down"))
            }
        }

        val streak = getStreakUseCase.invoke()
        if (streak.currentStreak >= 2) {
            insights.add(
                Insight(
                    "streak",
                    "You achieved your goal for ${streak.currentStreak} consecutive days.",
                    "local_fire_department"
                )
            )
        }

        val last7 = (0..6).mapNotNull { history[DateUtils.daysAgo(it.toLong())] }
        if (last7.isNotEmpty()) {
            val avg = last7.sumOf { it.steps } / last7.size
            insights.add(Insight("weekly_avg", "Your average daily steps this week are ${"%,d".format(avg)}.", "bar_chart"))
        }

        if (streak.weeklySuccessRatePercent >= 70) {
            insights.add(
                Insight(
                    "weekly_success",
                    "Great consistency — you hit your goal ${streak.weeklySuccessRatePercent}% of days this week.",
                    "emoji_events"
                )
            )
        }

        if (insights.isEmpty()) {
            insights.add(Insight("welcome", "Keep walking today to start building insights!", "insights"))
        }

        return insights
    }
}
