package com.example.healthmate.feature.dashboard.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import com.example.healthmate.core.design.HealthColors
import com.example.healthmate.feature.dashboard.presentation.HealthUiState
import com.example.healthmate.feature.records.data.*

@Composable fun Dashboard(state: HealthUiState, reminders: () -> Unit, goals: () -> Unit) {
    val today = LocalDate.now().toString(); val records = state.records.filter { it.date == today }; val steps = records.sumOf { it.steps }; val calories = records.sumOf { it.calories }; val water = records.sumOf { it.water }
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item { Row(verticalAlignment = Alignment.CenterVertically) { Column(Modifier.weight(1f)) { Text("Good morning!", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = HealthColors.Ink); Text(LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, d MMMM")), color = Color(0xFF59658C)) }; IconButton(reminders) { Icon(Icons.Outlined.Notifications, "Reminders") }; IconButton(goals) { Icon(Icons.Outlined.Tune, "Goals") } } }
        item { MetricCard("Steps", "$steps", "${state.goals.steps} goal", steps.toFloat() / state.goals.steps, HealthColors.Steps, Icons.Filled.DirectionsWalk) }
        item { MetricCard("Calories burned", "$calories kcal", "${state.goals.calories} kcal goal", calories.toFloat() / state.goals.calories, HealthColors.Calories, Icons.Filled.LocalFireDepartment) }
        item { MetricCard("Water intake", "$water ml", "${state.goals.water} ml goal", water.toFloat() / state.goals.water, HealthColors.Water, Icons.Filled.WaterDrop) }
        item { Text("Last 7 days Ã¢â‚¬â€œ steps", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = HealthColors.Ink); Spacer(Modifier.height(8.dp)); WeeklyChart(state.records, state.goals.steps) }
    }
}

@Composable private fun MetricCard(label: String, value: String, goal: String, progress: Float, color: Color, icon: androidx.compose.ui.graphics.vector.ImageVector) = Card { Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) { Icon(icon, null, tint = color, modifier = Modifier.size(34.dp)); Spacer(Modifier.width(16.dp)); Column(Modifier.weight(1f)) { Text(label); Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold); Text(goal, style = MaterialTheme.typography.bodySmall); Spacer(Modifier.height(8.dp)); LinearProgressIndicator(progress = { progress.coerceIn(0f, 1f) }, color = color, modifier = Modifier.fillMaxWidth()) } } }

@Composable private fun WeeklyChart(records: List<HealthRecord>, goal: Int) { val days = (6 downTo 0).map { LocalDate.now().minusDays(it.toLong()) }; val values = days.map { d -> records.filter { it.date == d.toString() }.sumOf { it.steps } }; val max = maxOf(goal, values.maxOrNull() ?: 0)
    Card { Row(Modifier.fillMaxWidth().height(170.dp).padding(14.dp), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.Bottom) { days.zip(values).forEach { (day, value) -> Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Bottom, modifier = Modifier.weight(1f).fillMaxHeight()) { Spacer(Modifier.weight(1f)); Box(Modifier.fillMaxWidth(.58f).height((110f * value / max).dp.coerceAtLeast(2.dp)).background(HealthColors.Steps)); Text(day.dayOfWeek.name.take(3), style = MaterialTheme.typography.labelSmall) } } } }
}
