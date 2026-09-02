package com.steptrack.pro.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.steptrack.pro.data.local.dao.AchievementDao
import com.steptrack.pro.data.local.dao.DailyStepDao
import com.steptrack.pro.data.local.dao.UserProfileDao
import com.steptrack.pro.data.local.entity.AchievementEntity
import com.steptrack.pro.data.local.entity.DailyStepEntity
import com.steptrack.pro.data.local.entity.UserProfileEntity

@Database(
    entities = [DailyStepEntity::class, AchievementEntity::class, UserProfileEntity::class],
    version = 1,
    exportSchema = true
)
abstract class StepTrackDatabase : RoomDatabase() {
    abstract fun dailyStepDao(): DailyStepDao
    abstract fun achievementDao(): AchievementDao
    abstract fun userProfileDao(): UserProfileDao

    companion object {
        const val DATABASE_NAME = "steptrack.db"
    }
}
