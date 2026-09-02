package com.steptrack.pro.domain.usecase

import com.steptrack.pro.domain.repository.SettingsRepository
import javax.inject.Inject

class UpdateGoalUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(newGoal: Int) {
        require(newGoal > 0) { "Daily goal must be positive" }
        settingsRepository.setDailyGoal(newGoal)
    }
}
