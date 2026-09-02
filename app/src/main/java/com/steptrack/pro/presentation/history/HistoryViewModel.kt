package com.steptrack.pro.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.steptrack.pro.domain.model.DailyStep
import com.steptrack.pro.domain.repository.SettingsRepository
import com.steptrack.pro.domain.usecase.GetStepHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class HistoryRange(val days: Long, val label: String) {
    WEEK(7, "Week"),
    MONTH(30, "Month"),
    YEAR(365, "Year")
}

data class HistoryUiState(
    val range: HistoryRange = HistoryRange.WEEK,
    val entries: List<DailyStep> = emptyList(),
    val averageSteps: Int = 0,
    val totalSteps: Int = 0,
    val bestDay: DailyStep? = null,
    val dailyGoal: Int = 10000
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val getStepHistoryUseCase: GetStepHistoryUseCase,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _range = MutableStateFlow(HistoryRange.WEEK)
    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _range.flatMapLatest { range ->
                combine(
                    getStepHistoryUseCase(range.days),
                    settingsRepository.settings
                ) { entries, settings ->
                    val sorted = entries.sortedBy { it.date }
                    HistoryUiState(
                        range = range,
                        entries = sorted,
                        averageSteps = if (sorted.isNotEmpty()) sorted.sumOf { it.steps } / sorted.size else 0,
                        totalSteps = sorted.sumOf { it.steps },
                        bestDay = sorted.maxByOrNull { it.steps },
                        dailyGoal = settings.dailyGoal
                    )
                }
            }.collect { _uiState.value = it }
        }
    }

    fun selectRange(range: HistoryRange) {
        _range.value = range
    }
}
