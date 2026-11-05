package com.example.healthmate.feature.dashboard.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.healthmate.core.design.HealthColors
import com.example.healthmate.feature.common.presentation.GoalProgress
import com.example.healthmate.feature.common.presentation.IconBadge
import com.example.healthmate.feature.common.presentation.SoftCard
import com.example.healthmate.feature.common.presentation.grouped
import com.example.healthmate.feature.records.data.HealthRecord
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun Dashboard(
    state: HealthUiState,
    reminders: () -> Unit,
    goals: () -> Unit,
    quickAdd: (steps: Int, calories: Int, water: Int) -> Unit,
) {
    val today = LocalDate.now().toString()
    val records = state.records.filter { it.date == today }
    val steps = records.sumOf { it.steps }
    val calories = records.sumOf { it.calories }
    val water = records.sumOf { it.water }
    val met = listOf(
        steps to state.goals.steps,
        calories to state.goals.calories,
        water to state.goals.water,
    ).count { (value, goal) -> goal > 0 && value >= goal }
    val hour = LocalTime.now().hour
    val greeting = when {
        hour < 12 -> "Good morning"
        hour < 17 -> "Good afternoon"
        else -> "Good evening"
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(greeting, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onBackground)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, d MMMM")),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                FilledTonalIconButton(onClick = reminders) { Icon(Icons.Outlined.Notifications, "Reminders") }
                Spacer(Modifier.width(8.dp))
                FilledTonalIconButton(onClick = goals) { Icon(Icons.Outlined.Tune, "Goals") }
            }
        }
        item {
            SoftCard(Modifier.fillMaxWidth(), container = MaterialTheme.colorScheme.primaryContainer) {
                Column(Modifier.padding(18.dp)) {
                    Text(
                        when (met) {
                            0 -> "A quiet start to the day"
                            3 -> "All three goals are done"
                            else -> "$met of 3 goals reached"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Log a little at a time, or add a full entry from the Add tab.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                    )
                }
            }
        }
        item {
            MetricCard(
                label = "Steps",
                value = steps.grouped(),
                goal = "${state.goals.steps.grouped()} goal",
                progress = fraction(steps, state.goals.steps),
                color = HealthColors.Steps,
                icon = Icons.AutoMirrored.Filled.DirectionsWalk,
                quick = listOf(500 to "+500", 1_000 to "+1,000"),
                onQuick = { quickAdd(it, 0, 0) },
            )
        }
        item {
            MetricCard(
                label = "Calories burned",
                value = "${calories.grouped()} kcal",
                goal = "${state.goals.calories.grouped()} kcal goal",
                progress = fraction(calories, state.goals.calories),
                color = HealthColors.Calories,
                icon = Icons.Filled.LocalFireDepartment,
                quick = listOf(50 to "+50", 100 to "+100"),
                onQuick = { quickAdd(0, it, 0) },
            )
        }
        item {
            MetricCard(
                label = "Water intake",
                value = "${water.grouped()} ml",
                goal = "${state.goals.water.grouped()} ml goal",
                progress = fraction(water, state.goals.water),
                color = HealthColors.Water,
                icon = Icons.Filled.WaterDrop,
                quick = listOf(250 to "+250 ml", 500 to "+500 ml"),
                onQuick = { quickAdd(0, 0, it) },
            )
        }
        item { WeeklyChart(state.records, state.goals.steps) }
    }
}

private fun fraction(value: Int, goal: Int) = if (goal <= 0) 0f else value.toFloat() / goal

@Composable
private fun MetricCard(
    label: String,
    value: String,
    goal: String,
    progress: Float,
    color: Color,
    icon: ImageVector,
    quick: List<Pair<Int, String>>,
    onQuick: (Int) -> Unit,
) {
    val percent = (progress * 100).toInt()
    SoftCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconBadge(icon, color)
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f)) {
                    Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(value, style = MaterialTheme.typography.headlineSmall)
                    Text(goal, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Text("$percent%", color = color, fontWeight = FontWeight.SemiBold)
            }
            GoalProgress(progress, color)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                quick.forEach { (amount, caption) ->
                    FilledTonalButton(onClick = { onQuick(amount) }) {
                        Icon(Icons.Filled.Add, null, modifier = Modifier.height(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(caption)
                    }
                }
            }
        }
    }
}

@Composable
private fun WeeklyChart(records: List<HealthRecord>, goal: Int) {
    val today = LocalDate.now()
    val days = (6 downTo 0).map { today.minusDays(it.toLong()) }
    val values = days.map { day -> records.filter { it.date == day.toString() }.sumOf { it.steps } }
    val max = maxOf(goal, values.maxOrNull() ?: 0, 1)
    SoftCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(18.dp)) {
            Text("Last 7 days", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))
            Text("Steps across the week", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(16.dp))
            Row(
                Modifier.fillMaxWidth().height(150.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom,
            ) {
                days.zip(values).forEach { (day, value) ->
                    val isToday = day == today
                    Column(
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                    ) {
                        Text(
                            if (value == 0) "" else compact(value),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Spacer(Modifier.height(6.dp))
                        Box(
                            Modifier
                                .width(16.dp)
                                .height((96f * value / max).dp.coerceAtLeast(if (value == 0) 4.dp else 8.dp))
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isToday) HealthColors.Steps else HealthColors.Steps.copy(alpha = 0.35f)),
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            day.dayOfWeek.name.take(2).lowercase().replaceFirstChar { it.titlecase() },
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                            color = if (isToday) HealthColors.Steps else MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}

private fun compact(value: Int) = when {
    value >= 1000 -> "${value / 1000}.${(value % 1000) / 100}k"
    else -> value.toString()
}
