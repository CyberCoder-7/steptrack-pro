package com.steptrack.pro.presentation.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.steptrack.pro.presentation.components.ChartPoint
import com.steptrack.pro.presentation.components.StepBarChart
import com.steptrack.pro.util.DateUtils
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(viewModel: HistoryViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { TopAppBar(title = { Text("History") }) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    HistoryRange.values().forEach { range ->
                        FilterChip(
                            selected = state.range == range,
                            onClick = { viewModel.selectRange(range) },
                            label = { Text(range.label) }
                        )
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    SummaryStat(label = "Total", value = "%,d".format(state.totalSteps))
                    SummaryStat(label = "Average", value = "%,d".format(state.averageSteps))
                    SummaryStat(label = "Best Day", value = "%,d".format(state.bestDay?.steps ?: 0))
                }
            }

            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    val labelFormatter = DateTimeFormatter.ofPattern(
                        if (state.range == HistoryRange.YEAR) "MMM" else "EEE", Locale.US
                    )
                    val points = state.entries.map { day ->
                        ChartPoint(
                            label = runCatching { DateUtils.parse(day.date).format(labelFormatter) }.getOrDefault(""),
                            value = day.steps,
                            goalMet = day.isGoalAchieved
                        )
                    }
                    StepBarChart(
                        points = points,
                        goalLine = state.dailyGoal,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            item {
                Text("Daily Log", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            }

            items(state.entries.sortedByDescending { it.date }) { day ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(day.date)
                        Text("%,d steps".format(day.steps), fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryStat(label: String, value: String) {
    Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
