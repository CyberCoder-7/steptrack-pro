package com.steptrack.pro.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.steptrack.pro.domain.model.Gender
import com.steptrack.pro.domain.model.UserProfile
import com.steptrack.pro.domain.repository.StepRepository
import com.steptrack.pro.domain.repository.UserProfileRepository
import com.steptrack.pro.domain.usecase.UpdateUserProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val profile: UserProfile = UserProfile(),
    val lifetimeSteps: Long = 0
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userProfileRepository: UserProfileRepository,
    private val updateUserProfileUseCase: UpdateUserProfileUseCase,
    private val stepRepository: StepRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                userProfileRepository.observeProfile(),
                stepRepository.observeLifetimeSteps()
            ) { profile, lifetime -> ProfileUiState(profile, lifetime) }
                .collect { _uiState.value = it }
        }
    }

    fun updateProfile(name: String, age: Int, heightCm: Float, weightKg: Float, gender: Gender) {
        viewModelScope.launch {
            updateUserProfileUseCase(UserProfile(name = name, age = age, heightCm = heightCm, weightKg = weightKg, gender = gender))
        }
    }
}
