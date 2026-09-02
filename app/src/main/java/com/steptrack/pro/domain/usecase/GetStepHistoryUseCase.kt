package com.steptrack.pro.domain.usecase

import com.steptrack.pro.domain.model.DailyStep
import com.steptrack.pro.domain.repository.StepRepository
import com.steptrack.pro.util.DateUtils
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetStepHistoryUseCase @Inject constructor(
    private val stepRepository: StepRepository
) {
    /** Observes a rolling window of history, e.g. last 7/30/365 days. */
    operator fun invoke(daysBack: Long): Flow<List<DailyStep>> {
        val start = DateUtils.daysAgo(daysBack)
        val end = DateUtils.today()
        return stepRepository.observeHistory(start, end)
    }

    suspend fun range(startDate: String, endDate: String): List<DailyStep> =
        stepRepository.getHistory(startDate, endDate)
}
