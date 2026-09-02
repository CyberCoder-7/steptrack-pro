package com.steptrack.pro.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DailyStepTest {

    private fun sample(steps: Int, goal: Int = 10000) = DailyStep(
        date = "2026-01-01",
        steps = steps,
        goal = goal,
        distanceMeters = 0.0,
        caloriesBurned = 0.0,
        activeMinutes = 0,
        floorsClimbed = 0
    )

    @Test
    fun `progressPercent is clamped between 0 and 1`() {
        assertEquals(0.5f, sample(5000).progressPercent, 0.0001f)
        assertEquals(1f, sample(15000).progressPercent, 0.0001f) // over-achieved, clamped to 100%
        assertEquals(0f, sample(0).progressPercent, 0.0001f)
    }

    @Test
    fun `progressPercent handles zero goal without dividing by zero`() {
        assertEquals(0f, sample(steps = 500, goal = 0).progressPercent, 0.0001f)
    }

    @Test
    fun `isGoalAchieved is true only at or above goal`() {
        assertFalse(sample(steps = 9999, goal = 10000).isGoalAchieved)
        assertTrue(sample(steps = 10000, goal = 10000).isGoalAchieved)
        assertTrue(sample(steps = 12000, goal = 10000).isGoalAchieved)
    }
}
