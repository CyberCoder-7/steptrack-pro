package com.steptrack.pro.data.repository

import com.steptrack.pro.data.local.dao.UserProfileDao
import com.steptrack.pro.data.local.entity.UserProfileEntity
import com.steptrack.pro.domain.model.Gender
import com.steptrack.pro.domain.model.UserProfile
import com.steptrack.pro.domain.repository.UserProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserProfileRepositoryImpl @Inject constructor(
    private val dao: UserProfileDao
) : UserProfileRepository {

    override fun observeProfile(): Flow<UserProfile> =
        dao.observeProfile().map { it?.toDomain() ?: UserProfile() }

    override suspend fun getProfile(): UserProfile =
        dao.getProfile()?.toDomain() ?: UserProfile()

    override suspend fun saveProfile(profile: UserProfile) {
        dao.upsert(
            UserProfileEntity(
                id = 1,
                name = profile.name,
                age = profile.age,
                heightCm = profile.heightCm,
                weightKg = profile.weightKg,
                gender = profile.gender.name
            )
        )
    }

    private fun UserProfileEntity.toDomain() = UserProfile(
        id = id,
        name = name,
        age = age,
        heightCm = heightCm,
        weightKg = weightKg,
        gender = runCatching { Gender.valueOf(gender) }.getOrDefault(Gender.UNSPECIFIED)
    )
}
