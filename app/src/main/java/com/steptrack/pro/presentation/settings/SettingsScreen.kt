package com.steptrack.pro.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.steptrack.pro.domain.model.AppTheme
import com.steptrack.pro.domain.model.DistanceUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    var goalSlider by remember(settings.dailyGoal) { mutableFloatStateOf(settings.dailyGoal.toFloat()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Back") }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                SettingsSection(title = "Daily Goal") {
                    Text("${goalSlider.toInt()} steps")
                    Slider(
                        value = goalSlider,
                        onValueChange = { goalSlider = it },
                        onValueChangeFinished = { viewModel.setDailyGoal((goalSlider / 500).toInt() * 500) },
                        valueRange = 2000f..30000f
                    )
                }
            }

            item {
                SettingsSection(title = "Units") {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        DistanceUnit.values().forEach { unit ->
                            FilterChip(
                                selected = settings.distanceUnit == unit,
                                onClick = { viewModel.setDistanceUnit(unit) },
                                label = { Text(unit.name) }
                            )
                        }
                    }
                }
            }

            item {
                SettingsSection(title = "Theme") {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        AppTheme.values().forEach { theme ->
                            FilterChip(
                                selected = settings.theme == theme,
                                onClick = { viewModel.setTheme(theme) },
                                label = { Text(theme.name.lowercase().replaceFirstChar { it.uppercase() }) }
                            )
                        }
                    }
                }
            }

            item {
                SettingsSection(title = "Notifications") {
                    ToggleRow(
                        label = "Daily walk reminder",
                        checked = settings.dailyReminderEnabled,
                        onCheckedChange = {
                            viewModel.setDailyReminder(it, settings.dailyReminderHour, settings.dailyReminderMinute)
                        }
                    )
                    ToggleRow(
                        label = "Goal progress reminder",
                        checked = settings.goalReminderEnabled,
                        onCheckedChange = { viewModel.setGoalReminderEnabled(it) }
                    )
                    ToggleRow(
                        label = "Goal achieved celebration",
                        checked = settings.goalAchievedNotifEnabled,
                        onCheckedChange = { viewModel.setGoalAchievedNotifEnabled(it) }
                    )
                }
            }

            item {
                SettingsSection(title = "Sensor Calibration") {
                    var stride by remember(settings.strideLengthMeters) {
                        mutableFloatStateOf(settings.strideLengthMeters.toFloat())
                    }
                    Text("Stride length: %.2f m".format(stride))
                    Slider(
                        value = stride,
                        onValueChange = { stride = it },
                        onValueChangeFinished = { viewModel.setStrideLength(stride.toDouble()) },
                        valueRange = 0.4f..1.2f
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsSection(title: String, content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            content()
        }
    }
}

@Composable
private fun ToggleRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
