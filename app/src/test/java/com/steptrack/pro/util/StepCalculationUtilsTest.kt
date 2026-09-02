package com.steptrack.pro.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class StepCalculationUtilsTest {

    @Test
    fun `distanceMeters uses provided stride length`() {
        val result = StepCalculationUtils.distanceMeters(steps = 1000, strideMeters = 0.75)
        assertEquals(750.0, result, 0.0001)
    }

    @Test
    fun `distanceMeters with zero steps returns zero`() {
        assertEquals(0.0, StepCalculationUtils.distanceMeters(0), 0.0001)
    }

    @Test
    fun `caloriesBurned scales with weight`() {
        val lightUser = StepCalculationUtils.caloriesBurned(steps = 5000, weightKg = 50f)
        val heavyUser = StepCalculationUtils.caloriesBurned(steps = 5000, weightKg = 100f)
        assertTrue("heavier user should burn more calories for same steps", heavyUser > lightUser)
    }

    @Test
    fun `caloriesBurned base formula matches spec at reference weight`() {
        // Spec: calories = steps * 0.04, calibrated at 70kg reference weight.
        val result = StepCalculationUtils.caloriesBurned(steps = 10000, weightKg = 70f)
        assertEquals(400.0, result, 0.5)
    }

    @Test
    fun `activeMinutes derives from step cadence`() {
        assertEquals(50, StepCalculationUtils.activeMinutes(5000))
        assertEquals(0, StepCalculationUtils.activeMinutes(50))
    }

    @Test
    fun `distanceKm and distanceMiles convert correctly`() {
        assertEquals(1.0, StepCalculationUtils.distanceKm(1000.0), 0.0001)
        assertEquals(1.0, StepCalculationUtils.distanceMiles(1609.344), 0.0001)
    }
}
