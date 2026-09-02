package com.steptrack.pro.data.repository

import com.steptrack.pro.data.local.dao.AchievementDao
import com.steptrack.pro.data.local.entity.AchievementEntity
import com.steptrack.pro.domain.model.Achievement
import com.steptrack.pro.domain.model.AchievementType
import com.steptrack.pro.domain.repository.AchievementRepository
import com.steptrack.pro.util.DateUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AchievementRepositoryImpl @Inject constructor(
    private val dao: AchievementDao
) : AchievementRepository {

    // Static catalog of all possible badges; only the unlock state is dynamic.
    private val catalog = listOf(
        AchievementType.FIRST_1000_STEPS to Pair("First 1,000 Steps", "Log 1,000 steps in a single day"),
        AchievementType.FIRST_5000_STEPS to Pair("First 5,000 Steps", "Log 5,000 steps in a single day"),
        AchievementType.FIRST_10000_STEPS to Pair("First 10,000 Steps", "Log 10,000 steps in a single day"),
        AchievementType.STREAK_7_DAYS to Pair("7-Day Streak", "Hit your goal 7 days in a row"),
        AchievementType.STREAK_30_DAYS to Pair("30-Day Streak", "Hit your goal 30 days in a row"),
        AchievementType.LIFETIME_100K_STEPS to Pair("100K Lifetime Steps", "Walk 100,000 steps in total")
    )

    override fun observeAll(): Flow<List<Achievement>> =
        dao.observeAll().map { entities -> entities.map { it.toDomain() } }

    override suspend fun ensureSeeded() {
        val seedEntities = catalog.map { (type, meta) ->
            AchievementEntity(
                type = type.name,
                title = meta.first,
                description = meta.second,
                iconName = iconFor(type),
                unlockedDate = null,
                isUnlocked = false
            )
        }
        dao.insertAll(seedEntities)
    }

    override suspend fun unlock(achievement: Achievement) {
        val entity = dao.getByType(achievement.type.name) ?: return
        if (entity.isUnlocked) return
        dao.update(entity.copy(isUnlocked = true, unlockedDate = DateUtils.today()))
    }

    override suspend fun checkAndUnlockAchievements(
        lifetimeSteps: Long,
        currentStreak: Int,
        todaySteps: Int
    ) {
        if (todaySteps >= 1000) unlockByType(AchievementType.FIRST_1000_STEPS)
        if (todaySteps >= 5000) unlockByType(AchievementType.FIRST_5000_STEPS)
        if (todaySteps >= 10000) unlockByType(AchievementType.FIRST_10000_STEPS)
        if (currentStreak >= 7) unlockByType(AchievementType.STREAK_7_DAYS)
        if (currentStreak >= 30) unlockByType(AchievementType.STREAK_30_DAYS)
        if (lifetimeSteps >= 100_000) unlockByType(AchievementType.LIFETIME_100K_STEPS)
    }

    private suspend fun unlockByType(type: AchievementType) {
        val entity = dao.getByType(type.name) ?: return
        if (!entity.isUnlocked) {
            dao.update(entity.copy(isUnlocked = true, unlockedDate = DateUtils.today()))
        }
    }

    private fun iconFor(type: AchievementType): String = when (type) {
        AchievementType.FIRST_1000_STEPS -> "directions_walk"
        AchievementType.FIRST_5000_STEPS -> "directions_run"
        AchievementType.FIRST_10000_STEPS -> "emoji_events"
        AchievementType.STREAK_7_DAYS -> "local_fire_department"
        AchievementType.STREAK_30_DAYS -> "whatshot"
        AchievementType.LIFETIME_100K_STEPS -> "military_tech"
    }

    private fun AchievementEntity.toDomain() = Achievement(
        id = id,
        type = AchievementType.valueOf(type),
        title = title,
        description = description,
        iconName = iconName,
        unlockedDate = unlockedDate,
        isUnlocked = isUnlocked
    )
}
