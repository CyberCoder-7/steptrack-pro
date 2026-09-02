package com.steptrack.pro.domain.repository

import com.steptrack.pro.domain.model.Achievement
import kotlinx.coroutines.flow.Flow

interface AchievementRepository {
    fun observeAll(): Flow<List<Achievement>>
    suspend fun ensureSeeded()
    suspend fun unlock(achievement: Achievement)
    suspend fun checkAndUnlockAchievements(lifetimeSteps: Long, currentStreak: Int, todaySteps: Int)
}
