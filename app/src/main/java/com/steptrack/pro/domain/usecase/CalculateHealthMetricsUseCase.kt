package com.steptrack.pro.domain.usecase

import com.steptrack.pro.domain.model.UserProfile
import com.steptrack.pro.util.StepCalculationUtils
import javax.inject.Inject

data class HealthMetrics(
    val distanceMeters: Double,
    val calories: Double,
    val activeMinutes: Int,
    val floors: Int
)

/** Centralizes the step -> health-metric formulas so both the live sensor
 *  path and the historical/backfill path use identical math. */
class CalculateHealthMetricsUseCase @Inject constructor() {
    operator fun invoke(steps: Int, profile: UserProfile): HealthMetrics = HealthMetrics(
        distanceMeters = StepCalculationUtils.distanceMeters(steps, profile.estimatedStrideMeters()),
        calories = StepCalculationUtils.caloriesBurned(steps, profile.weightKg),
        activeMinutes = StepCalculationUtils.activeMinutes(steps),
        floors = StepCalculationUtils.floorsClimbed(steps)
    )
}
