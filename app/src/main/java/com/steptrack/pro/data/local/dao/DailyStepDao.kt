package com.steptrack.pro.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import com.steptrack.pro.data.local.entity.DailyStepEntity

@Dao
interface DailyStepDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: DailyStepEntity)

    @Query("SELECT * FROM daily_steps WHERE date = :date LIMIT 1")
    fun observeByDate(date: String): Flow<DailyStepEntity?>

    @Query("SELECT * FROM daily_steps WHERE date = :date LIMIT 1")
    suspend fun getByDate(date: String): DailyStepEntity?

    @Query("SELECT * FROM daily_steps ORDER BY date DESC")
    fun observeAll(): Flow<List<DailyStepEntity>>

    @Query("SELECT * FROM daily_steps WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    fun observeRange(startDate: String, endDate: String): Flow<List<DailyStepEntity>>

    @Query("SELECT * FROM daily_steps WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    suspend fun getRange(startDate: String, endDate: String): List<DailyStepEntity>

    @Query("SELECT COALESCE(SUM(steps), 0) FROM daily_steps")
    suspend fun getLifetimeTotalSteps(): Long

    @Query("SELECT COALESCE(SUM(steps), 0) FROM daily_steps")
    fun observeLifetimeTotalSteps(): Flow<Long>

    @Query("DELETE FROM daily_steps WHERE date < :beforeDate")
    suspend fun pruneOlderThan(beforeDate: String)
}
