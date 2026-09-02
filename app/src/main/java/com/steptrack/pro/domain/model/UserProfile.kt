package com.steptrack.pro.domain.model

enum class Gender { MALE, FEMALE, OTHER, UNSPECIFIED }

data class UserProfile(
    val id: Int = 1,
    val name: String = "",
    val age: Int = 25,
    val heightCm: Float = 170f,
    val weightKg: Float = 70f,
    val gender: Gender = Gender.UNSPECIFIED
) {
    /** Personalized stride length improves distance accuracy over the flat 0.75m average. */
    fun estimatedStrideMeters(): Double {
        val baseFactor = if (gender == Gender.FEMALE) 0.413 else 0.415
        return (heightCm / 100.0) * baseFactor
    }
}
