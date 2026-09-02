package com.steptrack.pro.presentation.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateToSettings: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    var name by remember { mutableStateOf(state.profile.name) }
    var age by remember { mutableStateOf(state.profile.age.toString()) }
    var height by remember { mutableStateOf(state.profile.heightCm.toString()) }
    var weight by remember { mutableStateOf(state.profile.weightKg.toString()) }

    LaunchedEffect(state.profile) {
        name = state.profile.name
        age = state.profile.age.toString()
        height = state.profile.heightCm.toString()
        weight = state.profile.weightKg.toString()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Filled.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Lifetime Steps", style = MaterialTheme.typography.bodyMedium)
                            Text(
                                "%,d".format(state.lifetimeSteps),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            item {
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        persist(viewModel, name, age, height, weight, state)
                    },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                OutlinedTextField(
                    value = age,
                    onValueChange = {
                        age = it
                        persist(viewModel, name, age, height, weight, state)
                    },
                    label = { Text("Age") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                OutlinedTextField(
                    value = height,
                    onValueChange = {
                        height = it
                        persist(viewModel, name, age, height, weight, state)
                    },
                    label = { Text("Height (cm)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                OutlinedTextField(
                    value = weight,
                    onValueChange = {
                        weight = it
                        persist(viewModel, name, age, height, weight, state)
                    },
                    label = { Text("Weight (kg)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

private fun persist(
    viewModel: ProfileViewModel,
    name: String,
    age: String,
    height: String,
    weight: String,
    state: ProfileUiState
) {
    viewModel.updateProfile(
        name = name,
        age = age.toIntOrNull() ?: state.profile.age,
        heightCm = height.toFloatOrNull() ?: state.profile.heightCm,
        weightKg = weight.toFloatOrNull() ?: state.profile.weightKg,
        gender = state.profile.gender
    )
}
