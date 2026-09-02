package com.steptrack.pro.di

import com.steptrack.pro.data.repository.AchievementRepositoryImpl
import com.steptrack.pro.data.repository.SettingsRepositoryImpl
import com.steptrack.pro.data.repository.StepRepositoryImpl
import com.steptrack.pro.data.repository.UserProfileRepositoryImpl
import com.steptrack.pro.domain.repository.AchievementRepository
import com.steptrack.pro.domain.repository.SettingsRepository
import com.steptrack.pro.domain.repository.StepRepository
import com.steptrack.pro.domain.repository.UserProfileRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindStepRepository(impl: StepRepositoryImpl): StepRepository

    @Binds
    @Singleton
    abstract fun bindAchievementRepository(impl: AchievementRepositoryImpl): AchievementRepository

    @Binds
    @Singleton
    abstract fun bindUserProfileRepository(impl: UserProfileRepositoryImpl): UserProfileRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository
}
