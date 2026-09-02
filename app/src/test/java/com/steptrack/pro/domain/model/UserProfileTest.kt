package com.steptrack.pro.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class UserProfileTest {

    @Test
    fun `estimatedStrideMeters scales with height`() {
        val shorter = UserProfile(heightCm = 150f, gender = Gender.MALE)
        val taller = UserProfile(heightCm = 190f, gender = Gender.MALE)
        assert(taller.estimatedStrideMeters() > shorter.estimatedStrideMeters())
    }

    @Test
    fun `estimatedStrideMeters uses distinct factor for female gender`() {
        val male = UserProfile(heightCm = 170f, gender = Gender.MALE)
        val female = UserProfile(heightCm = 170f, gender = Gender.FEMALE)
        assertEquals(170.0 / 100.0 * 0.415, male.estimatedStrideMeters(), 0.0001)
        assertEquals(170.0 / 100.0 * 0.413, female.estimatedStrideMeters(), 0.0001)
    }
}
