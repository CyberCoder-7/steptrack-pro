package com.steptrack.pro.domain.repository

import com.steptrack.pro.domain.model.DailyStep
import kotlinx.coroutines.flow.Flow

interface StepRepository {
    fun observeToday(): Flow<DailyStep>
    fun observeLiveStepCount(): Flow<Int>
    fun observeHistory(startDate: String, endDate: String): Flow<List<DailyStep>>
    fun observeLifetimeSteps(): Flow<Long>
    suspend fun getHistory(startDate: String, endDate: String): List<DailyStep>
    suspend fun saveTodaySnapshot(steps: Int)
    suspend fun startTracking()
    suspend fun stopTracking()
}
