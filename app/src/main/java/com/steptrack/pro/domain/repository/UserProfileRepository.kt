package com.steptrack.pro.domain.repository

import com.steptrack.pro.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface UserProfileRepository {
    fun observeProfile(): Flow<UserProfile>
    suspend fun getProfile(): UserProfile
    suspend fun saveProfile(profile: UserProfile)
}
