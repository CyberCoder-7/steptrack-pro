package com.steptrack.pro.presentation.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.steptrack.pro.data.sensor.StepSensorManager
import com.steptrack.pro.domain.repository.AchievementRepository
import com.steptrack.pro.domain.repository.SettingsRepository
import com.steptrack.pro.domain.usecase.GetStreakUseCase
import com.steptrack.pro.domain.usecase.GetTodayStepsUseCase
import com.steptrack.pro.util.BatteryOptimizationUtils
import com.steptrack.pro.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    application: Application,
    private val getTodayStepsUseCase: GetTodayStepsUseCase,
    private val settingsRepository: SettingsRepository,
    private val achievementRepository: AchievementRepository,
    private val getStreakUseCase: GetStreakUseCase,
    private val sensorManager: StepSensorManager
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        observeDashboardData()
        observeSensorAvailability()
        refreshStreak()
        refreshBatteryOptimizationStatus()
    }

    /** Re-checked whenever the dashboard resumes, since the user may have just
     *  returned from the system battery-optimization dialog. */
    fun refreshBatteryOptimizationStatus() {
        val exempted = BatteryOptimizationUtils.isIgnoringBatteryOptimizations(getApplication())
        _uiState.value = _uiState.value.copy(needsBatteryOptimizationExemption = !exempted)
    }

    private fun observeDashboardData() {
        viewModelScope.launch {
            combine(
                getTodayStepsUseCase(),
                settingsRepository.settings,
                achievementRepository.observeAll()
            ) { todaySteps, settings, achievements ->
                val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
                DashboardUiState(
                    isLoading = false,
                    steps = todaySteps.steps,
                    goal = settings.dailyGoal,
                    progressPercent = if (settings.dailyGoal > 0) (todaySteps.steps.toFloat() / settings.dailyGoal) else 0f,
                    distanceMeters = todaySteps.distanceMeters,
                    calories = todaySteps.caloriesBurned,
                    activeMinutes = todaySteps.activeMinutes,
                    floors = todaySteps.floorsClimbed,
                    currentStreak = _uiState.value.currentStreak,
                    distanceUnit = settings.distanceUnit,
                    recentAchievements = achievements.filter { it.isUnlocked }.take(3),
                    greeting = DateUtils.friendlyGreeting(hour),
                    dateLabel = DateUtils.humanReadableDate(),
                    sensorUnavailable = _uiState.value.sensorUnavailable,
                    needsBatteryOptimizationExemption = _uiState.value.needsBatteryOptimizationExemption
                )
            }.collect { _uiState.value = it }
        }
    }

    private fun observeSensorAvailability() {
        viewModelScope.launch {
            sensorManager.availability.collect { availability ->
                _uiState.value = _uiState.value.copy(
                    sensorUnavailable = availability is com.steptrack.pro.data.sensor.SensorAvailability.StepCounterUnavailable
                )
            }
        }
    }

    private fun refreshStreak() {
        viewModelScope.launch {
            val streak = getStreakUseCase.invoke()
            _uiState.value = _uiState.value.copy(currentStreak = streak.currentStreak)
        }
    }
}
