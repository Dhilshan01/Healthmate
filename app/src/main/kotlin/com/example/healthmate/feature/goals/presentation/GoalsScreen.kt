package com.example.healthmate.feature.goals.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.healthmate.core.design.HealthColors
import com.example.healthmate.feature.common.presentation.BackBar
import com.example.healthmate.feature.common.presentation.IconBadge
import com.example.healthmate.feature.common.presentation.Input
import com.example.healthmate.feature.common.presentation.SaveButton
import com.example.healthmate.feature.common.presentation.SoftCard
import com.example.healthmate.feature.records.data.DailyGoals

@Composable
fun Goals(current: DailyGoals, save: (DailyGoals) -> Unit, back: () -> Unit) {
    var steps by remember { mutableStateOf(current.steps.toString()) }
    var calories by remember { mutableStateOf(current.calories.toString()) }
    var water by remember { mutableStateOf(current.water.toString()) }
    var error by remember { mutableStateOf<String?>(null) }
    Scaffold(containerColor = MaterialTheme.colorScheme.background, topBar = { BackBar("Daily goals", back) }) { padding ->
        Column(
            Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState()).imePadding().padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text("Choose targets you can keep.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            GoalField("Steps", "A full day of walking", steps, { steps = it }, HealthColors.Steps, Icons.AutoMirrored.Filled.DirectionsWalk)
            GoalField("Calories", "Energy you want to burn", calories, { calories = it }, HealthColors.Calories, Icons.Filled.LocalFireDepartment)
            GoalField("Water", "How much you want to drink", water, { water = it }, HealthColors.Water, Icons.Filled.WaterDrop)
            error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            SaveButton("Save goals") {
                val parsedSteps = steps.toIntOrNull()
                val parsedCalories = calories.toIntOrNull()
                val parsedWater = water.toIntOrNull()
                if (parsedSteps == null || parsedCalories == null || parsedWater == null || parsedSteps !in 1..100_000 || parsedCalories !in 1..20_000 || parsedWater !in 1..20_000) {
                    error = "Goals must be whole numbers greater than zero within the allowed limits."
                } else {
                    save(DailyGoals(steps = parsedSteps, calories = parsedCalories, water = parsedWater))
                }
            }
        }
    }
}

@Composable
private fun GoalField(title: String, subtitle: String, value: String, onChange: (String) -> Unit, color: Color, icon: ImageVector) {
    SoftCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconBadge(icon, color)
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(title, style = MaterialTheme.typography.titleMedium)
                    Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Input(value, onChange, "$title goal", KeyboardType.Number)
        }
    }
}
