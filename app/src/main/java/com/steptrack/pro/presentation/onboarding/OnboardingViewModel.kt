package com.steptrack.pro.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.steptrack.pro.domain.model.Gender
import com.steptrack.pro.domain.model.UserProfile
import com.steptrack.pro.domain.repository.SettingsRepository
import com.steptrack.pro.domain.usecase.UpdateUserProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val updateUserProfileUseCase: UpdateUserProfileUseCase,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    fun completeOnboarding(
        name: String,
        age: Int,
        heightCm: Float,
        weightKg: Float,
        gender: Gender,
        dailyGoal: Int,
        onDone: () -> Unit
    ) {
        viewModelScope.launch {
            updateUserProfileUseCase(
                UserProfile(name = name, age = age, heightCm = heightCm, weightKg = weightKg, gender = gender)
            )
            settingsRepository.setDailyGoal(dailyGoal)
            settingsRepository.setOnboardingCompleted(true)
            onDone()
        }
    }
}
