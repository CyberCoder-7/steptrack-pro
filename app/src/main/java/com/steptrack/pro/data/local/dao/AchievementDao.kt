package com.steptrack.pro.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import com.steptrack.pro.data.local.entity.AchievementEntity

@Dao
interface AchievementDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(achievements: List<AchievementEntity>)

    @Update
    suspend fun update(achievement: AchievementEntity)

    @Query("SELECT * FROM achievements ORDER BY isUnlocked DESC, id ASC")
    fun observeAll(): Flow<List<AchievementEntity>>

    @Query("SELECT * FROM achievements WHERE type = :type LIMIT 1")
    suspend fun getByType(type: String): AchievementEntity?

    @Query("SELECT COUNT(*) FROM achievements WHERE isUnlocked = 1")
    fun observeUnlockedCount(): Flow<Int>
}
