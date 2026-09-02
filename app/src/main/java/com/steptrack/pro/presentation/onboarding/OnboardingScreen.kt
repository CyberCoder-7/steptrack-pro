package com.steptrack.pro.presentation.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.steptrack.pro.domain.model.Gender

/**
 * Guided profile setup shown on first launch. Collects the physical stats
 * that drive personalized distance/calorie math, plus an initial daily goal.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    onFinished: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    var name by remember { mutableStateOf("") }
    var age by remember { mutableIntStateOf(25) }
    var height by remember { mutableFloatStateOf(170f) }
    var weight by remember { mutableFloatStateOf(70f) }
    var gender by remember { mutableStateOf(Gender.UNSPECIFIED) }
    var goal by remember { mutableIntStateOf(10000) }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text("Let's set up your profile", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text("This helps us personalize distance and calorie estimates.", style = MaterialTheme.typography.bodyMedium)

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Column {
                Text("Age: $age")
                Slider(value = age.toFloat(), onValueChange = { age = it.toInt() }, valueRange = 10f..100f)
            }

            Column {
                Text("Height: ${height.toInt()} cm")
                Slider(value = height, onValueChange = { height = it }, valueRange = 120f..220f)
            }

            Column {
                Text("Weight: ${weight.toInt()} kg")
                Slider(value = weight, onValueChange = { weight = it }, valueRange = 30f..180f)
            }

            Text("Gender")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Gender.values().forEach { g ->
                    FilterChip(
                        selected = gender == g,
                        onClick = { gender = g },
                        label = { Text(g.name.lowercase().replaceFirstChar { it.uppercase() }) }
                    )
                }
            }

            Column {
                Text("Daily step goal: ${"%,d".format(goal)}")
                Slider(
                    value = goal.toFloat(),
                    onValueChange = { goal = (it / 500).toInt() * 500 },
                    valueRange = 2000f..30000f
                )
            }

            Button(
                onClick = {
                    viewModel.completeOnboarding(name, age, height, weight, gender, goal, onFinished)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Get Started")
            }
        }
    }
}
