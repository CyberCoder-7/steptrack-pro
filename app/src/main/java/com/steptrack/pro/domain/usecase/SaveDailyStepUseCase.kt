package com.steptrack.pro.domain.usecase

import com.steptrack.pro.domain.repository.AchievementRepository
import com.steptrack.pro.domain.repository.StepRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Persists the current live step count as today's row and evaluates
 * achievement/streak unlocks. Called periodically by the foreground service
 * and whenever the app is backgrounded, so data survives process death.
 */
class SaveDailyStepUseCase @Inject constructor(
    private val stepRepository: StepRepository,
    private val achievementRepository: AchievementRepository,
    private val getStreakUseCase: GetStreakUseCase
) {
    suspend operator fun invoke(currentSteps: Int) {
        stepRepository.saveTodaySnapshot(currentSteps)
        val lifetime = stepRepository.observeLifetimeSteps().first()
        val streak = getStreakUseCase.currentStreak()
        achievementRepository.checkAndUnlockAchievements(
            lifetimeSteps = lifetime,
            currentStreak = streak,
            todaySteps = currentSteps
        )
    }
}
