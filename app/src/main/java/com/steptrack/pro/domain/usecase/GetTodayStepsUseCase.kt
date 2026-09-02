package com.steptrack.pro.domain.usecase

import com.steptrack.pro.domain.model.DailyStep
import com.steptrack.pro.domain.repository.StepRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/** Streams today's step snapshot (steps, goal, distance, calories, etc.), live. */
class GetTodayStepsUseCase @Inject constructor(
    private val stepRepository: StepRepository
) {
    operator fun invoke(): Flow<DailyStep> = stepRepository.observeToday()
}
