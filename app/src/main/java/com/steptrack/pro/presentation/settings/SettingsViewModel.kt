package com.steptrack.pro.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.steptrack.pro.domain.model.AppSettings
import com.steptrack.pro.domain.model.AppTheme
import com.steptrack.pro.domain.model.DistanceUnit
import com.steptrack.pro.domain.repository.SettingsRepository
import com.steptrack.pro.domain.usecase.UpdateGoalUseCase
import com.steptrack.pro.notification.ReminderScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val updateGoalUseCase: UpdateGoalUseCase,
    private val reminderScheduler: ReminderScheduler
) : ViewModel() {

    val settings: StateFlow<AppSettings> = settingsRepository.settings.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), AppSettings()
    )

    fun setDailyGoal(goal: Int) = viewModelScope.launch { updateGoalUseCase(goal) }
    fun setDistanceUnit(unit: DistanceUnit) = viewModelScope.launch { settingsRepository.setDistanceUnit(unit) }
    fun setTheme(theme: AppTheme) = viewModelScope.launch { settingsRepository.setTheme(theme) }

    fun setDailyReminder(enabled: Boolean, hour: Int, minute: Int) {
        viewModelScope.launch {
            settingsRepository.setDailyReminder(enabled, hour, minute)
            if (enabled) {
                reminderScheduler.scheduleDailyReminder(hour, minute)
            } else {
                reminderScheduler.cancelDailyReminder()
            }
        }
    }

    fun setGoalReminderEnabled(enabled: Boolean) =
        viewModelScope.launch { settingsRepository.setGoalReminderEnabled(enabled) }
    fun setGoalAchievedNotifEnabled(enabled: Boolean) =
        viewModelScope.launch { settingsRepository.setGoalAchievedNotifEnabled(enabled) }
    fun setStrideLength(meters: Double) = viewModelScope.launch { settingsRepository.setStrideLength(meters) }
}
