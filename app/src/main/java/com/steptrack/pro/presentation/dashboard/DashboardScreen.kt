package com.steptrack.pro.presentation.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.steptrack.pro.domain.model.DistanceUnit
import com.steptrack.pro.presentation.components.AchievementBadge
import com.steptrack.pro.presentation.components.CircularStepProgress
import com.steptrack.pro.presentation.components.StatCard
import com.steptrack.pro.presentation.theme.AccentBlue
import com.steptrack.pro.presentation.theme.AccentOrange
import com.steptrack.pro.util.BatteryOptimizationUtils
import com.steptrack.pro.util.StepCalculationUtils

@Composable
fun DashboardScreen(
    onNavigateToInsights: () -> Unit,
    onNavigateToAchievements: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val currentViewModel = rememberUpdatedState(viewModel)

    // Re-check the exemption status whenever the user returns to this screen
    // (e.g. after visiting the system battery-optimization dialog).
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                currentViewModel.value.refreshBatteryOptimizationStatus()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Scaffold { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Column {
                    Text(
                        text = "${state.greeting} 👋",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = state.dateLabel,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (state.sensorUnavailable) {
                item {
                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                        Text(
                            "This device has no step-counter sensor. Steps can't be tracked automatically here.",
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }

            if (state.needsBatteryOptimizationExemption) {
                item {
                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Keep step tracking running in the background",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Text(
                                "Your device's battery saver may pause tracking overnight. " +
                                    "Exempt StepTrack Pro to keep counting steps reliably.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
                            )
                            TextButton(onClick = { BatteryOptimizationUtils.requestIgnoreBatteryOptimizations(context) }) {
                                Text("Allow background tracking")
                            }
                        }
                    }
                }
            }

            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularStepProgress(
                        steps = state.steps,
                        goal = state.goal,
                        progress = state.progressPercent
                    )
                }
            }

            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val distanceText = when (state.distanceUnit) {
                        DistanceUnit.KM -> "%.2f km".format(StepCalculationUtils.distanceKm(state.distanceMeters))
                        DistanceUnit.MILES -> "%.2f mi".format(StepCalculationUtils.distanceMiles(state.distanceMeters))
                    }
                    StatCard(
                        icon = Icons.Filled.Straighten,
                        iconTint = AccentBlue,
                        label = "Distance",
                        value = distanceText,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        icon = Icons.Filled.LocalFireDepartment,
                        iconTint = AccentOrange,
                        label = "Calories",
                        value = "${state.calories.toInt()} kcal",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    StatCard(
                        icon = Icons.Filled.Timer,
                        iconTint = MaterialTheme.colorScheme.primary,
                        label = "Active Time",
                        value = "${state.activeMinutes} min",
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        icon = Icons.Filled.Whatshot,
                        iconTint = AccentOrange,
                        label = "Streak",
                        value = "${state.currentStreak} days",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                TextButton(onClick = onNavigateToInsights) {
                    Text("View health insights →")
                }
            }

            if (state.recentAchievements.isNotEmpty()) {
                item {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Recent Achievements", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                            TextButton(onClick = onNavigateToAchievements) { Text("See all") }
                        }
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(state.recentAchievements) { achievement ->
                                AchievementBadge(achievement = achievement)
                            }
                        }
                    }
                }
            }
        }
    }
}
