package com.steptrack.pro.util

/**
 * Pure functions for converting raw step counts into the derived health
 * metrics shown across the app. Kept side-effect free and unit-testable.
 */
object StepCalculationUtils {

    fun distanceMeters(steps: Int, strideMeters: Double = Constants.DEFAULT_STRIDE_METERS): Double =
        steps * strideMeters

    fun caloriesBurned(steps: Int, weightKg: Float = 70f): Double {
        // Base formula from the spec (steps * 0.04) scaled gently by body weight
        // so heavier/lighter users get more realistic estimates.
        val weightFactor = (weightKg / 70f).coerceIn(0.6f, 1.6f)
        return steps * Constants.CALORIES_PER_STEP * weightFactor
    }

    fun activeMinutes(steps: Int): Int = steps / Constants.STEPS_PER_ACTIVE_MINUTE

    fun floorsClimbed(steps: Int): Int = steps / (Constants.STEPS_PER_FLOOR * 10) // heuristic estimate

    fun distanceKm(meters: Double): Double = meters / 1000.0

    fun distanceMiles(meters: Double): Double = meters / 1609.344
}
