package com.steptrack.pro.di

import android.content.Context
import androidx.room.Room
import com.steptrack.pro.data.local.StepTrackDatabase
import com.steptrack.pro.data.local.dao.AchievementDao
import com.steptrack.pro.data.local.dao.DailyStepDao
import com.steptrack.pro.data.local.dao.UserProfileDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): StepTrackDatabase =
        Room.databaseBuilder(context, StepTrackDatabase::class.java, StepTrackDatabase.DATABASE_NAME)
            .fallbackToDestructiveMigration() // acceptable for v1; replace with real migrations post-launch
            .build()

    @Provides
    fun provideDailyStepDao(db: StepTrackDatabase): DailyStepDao = db.dailyStepDao()

    @Provides
    fun provideAchievementDao(db: StepTrackDatabase): AchievementDao = db.achievementDao()

    @Provides
    fun provideUserProfileDao(db: StepTrackDatabase): UserProfileDao = db.userProfileDao()
}
