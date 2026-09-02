package com.steptrack.pro.data.repository

import com.steptrack.pro.data.local.dao.DailyStepDao
import com.steptrack.pro.data.local.entity.DailyStepEntity
import com.steptrack.pro.data.sensor.StepSensorManager
import com.steptrack.pro.domain.model.DailyStep
import com.steptrack.pro.domain.repository.StepRepository
import com.steptrack.pro.domain.repository.UserProfileRepository
import com.steptrack.pro.util.Constants
import com.steptrack.pro.util.DateUtils
import com.steptrack.pro.util.StepCalculationUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StepRepositoryImpl @Inject constructor(
    private val dao: DailyStepDao,
    private val sensorManager: StepSensorManager,
    private val userProfileRepository: UserProfileRepository
) : StepRepository {

    override fun observeLiveStepCount(): Flow<Int> = sensorManager.todaySteps

    override fun observeToday(): Flow<DailyStep> {
        val today = DateUtils.today()
        return combine(
            sensorManager.todaySteps.distinctUntilChanged(),
            dao.observeByDate(today)
        ) { liveSteps, entity ->
            val goal = entity?.goal ?: Constants.DEFAULT_DAILY_GOAL
            val profile = userProfileRepository.getProfile()
            DailyStep(
                date = today,
                steps = liveSteps,
                goal = goal,
                distanceMeters = StepCalculationUtils.distanceMeters(liveSteps, profile.estimatedStrideMeters()),
                caloriesBurned = StepCalculationUtils.caloriesBurned(liveSteps, profile.weightKg),
                activeMinutes = StepCalculationUtils.activeMinutes(liveSteps),
                floorsClimbed = StepCalculationUtils.floorsClimbed(liveSteps)
            )
        }
    }

    override fun observeHistory(startDate: String, endDate: String): Flow<List<DailyStep>> =
        dao.observeRange(startDate, endDate).map { list -> list.map { it.toDomain() } }

    override fun observeLifetimeSteps(): Flow<Long> = dao.observeLifetimeTotalSteps()

    override suspend fun getHistory(startDate: String, endDate: String): List<DailyStep> =
        dao.getRange(startDate, endDate).map { it.toDomain() }

    override suspend fun saveTodaySnapshot(steps: Int) {
        val today = DateUtils.today()
        val existing = dao.getByDate(today)
        val profile = userProfileRepository.getProfile()
        val goal = existing?.goal ?: Constants.DEFAULT_DAILY_GOAL
        dao.upsert(
            DailyStepEntity(
                date = today,
                steps = steps,
                goal = goal,
                distance = StepCalculationUtils.distanceMeters(steps, profile.estimatedStrideMeters()),
                calories = StepCalculationUtils.caloriesBurned(steps, profile.weightKg),
                activeMinutes = StepCalculationUtils.activeMinutes(steps),
                floors = StepCalculationUtils.floorsClimbed(steps),
                lastUpdatedEpochMillis = System.currentTimeMillis()
            )
        )
        dao.pruneOlderThan(DateUtils.daysAgo(Constants.HISTORY_RETENTION_DAYS.toLong()))
    }

    override suspend fun startTracking() {
        sensorManager.start()
    }

    override suspend fun stopTracking() {
        sensorManager.stop()
    }

    private fun DailyStepEntity.toDomain() = DailyStep(
        date = date,
        steps = steps,
        goal = goal,
        distanceMeters = distance,
        caloriesBurned = calories,
        activeMinutes = activeMinutes,
        floorsClimbed = floors
    )
}
