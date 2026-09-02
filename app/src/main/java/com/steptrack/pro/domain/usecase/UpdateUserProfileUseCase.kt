package com.steptrack.pro.domain.usecase

import com.steptrack.pro.domain.model.UserProfile
import com.steptrack.pro.domain.repository.UserProfileRepository
import javax.inject.Inject

class UpdateUserProfileUseCase @Inject constructor(
    private val userProfileRepository: UserProfileRepository
) {
    suspend operator fun invoke(profile: UserProfile) = userProfileRepository.saveProfile(profile)
}
