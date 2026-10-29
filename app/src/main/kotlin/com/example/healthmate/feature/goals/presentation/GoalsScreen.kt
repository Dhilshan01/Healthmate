package com.example.healthmate.feature.goals.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.healthmate.feature.common.presentation.BackBar
import com.example.healthmate.feature.common.presentation.Input
import com.example.healthmate.feature.records.data.DailyGoals

@Composable fun Goals(current: DailyGoals, save: (DailyGoals) -> Unit, back: () -> Unit) { var steps by remember { mutableStateOf(current.steps.toString()) }; var calories by remember { mutableStateOf(current.calories.toString()) }; var water by remember { mutableStateOf(current.water.toString()) }; var error by remember { mutableStateOf<String?>(null) }
    Scaffold(topBar = { BackBar("Daily goals", back) }) { p -> Column(Modifier.padding(p).padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) { Text("Set targets that fit your routine."); Input(steps, { steps = it }, "Steps goal", KeyboardType.Number); Input(calories, { calories = it }, "Calories goal", KeyboardType.Number); Input(water, { water = it }, "Water goal", KeyboardType.Number); error?.let { Text(it, color = MaterialTheme.colorScheme.error) }; Button({ val s = steps.toIntOrNull(); val c = calories.toIntOrNull(); val w = water.toIntOrNull(); if (s == null || c == null || w == null || s !in 1..100000 || c !in 1..20000 || w !in 1..20000) error = "Goals must be whole numbers greater than zero within the allowed limits." else save(DailyGoals(steps = s, calories = c, water = w)) }, Modifier.fillMaxWidth()) { Text("Save goals") } } }
}
