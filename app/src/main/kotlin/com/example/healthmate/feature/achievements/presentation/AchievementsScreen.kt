package com.example.healthmate.feature.achievements.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.healthmate.core.design.HealthColors
import com.example.healthmate.feature.common.presentation.BackBar
import com.example.healthmate.feature.common.presentation.GoalProgress
import com.example.healthmate.feature.common.presentation.SoftCard
import com.example.healthmate.feature.common.presentation.grouped
import com.example.healthmate.feature.records.data.DailyGoals
import com.example.healthmate.feature.records.data.HealthRecord
import java.time.LocalDate

@Composable
fun Achievements(records: List<HealthRecord>, goals: DailyGoals, back: () -> Unit) {
    val totalSteps = records.sumOf { it.steps }
    val totalWater = records.sumOf { it.water }
    val days = records.groupBy { it.date }.mapValues { (_, dayRecords) ->
        Triple(dayRecords.sumOf { it.steps }, dayRecords.sumOf { it.calories }, dayRecords.sumOf { it.water })
    }
    val goalDays = days.values.count { it.first >= goals.steps || it.second >= goals.calories || it.third >= goals.water }
    var streak = 0
    var day = LocalDate.now()
    while (days[day.toString()]?.let { it.first >= goals.steps || it.second >= goals.calories || it.third >= goals.water } == true) {
        streak++
        day = day.minusDays(1)
    }
    Scaffold(containerColor = MaterialTheme.colorScheme.background, topBar = { BackBar("Achievements", back) }) { padding ->
        LazyColumn(
            Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                Text("Every recorded day builds the routine.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            item {
                SoftCard(Modifier.fillMaxWidth(), container = MaterialTheme.colorScheme.primary) {
                    Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.LocalFireDepartment, null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(42.dp))
                        Spacer(Modifier.width(16.dp))
                        Column {
                            Text(
                                "$streak day${if (streak == 1) "" else "s"}",
                                color = MaterialTheme.colorScheme.onPrimary,
                                style = MaterialTheme.typography.headlineSmall,
                            )
                            Text(
                                if (streak == 0) "Record an activity to begin your streak." else "Keep showing up. Your streak is growing.",
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f),
                            )
                        }
                    }
                }
            }
            item { Text("Badges", style = MaterialTheme.typography.titleLarge) }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Badge("First step", records.isNotEmpty(), Modifier.weight(1f))
                        Badge("Goal getter", goalDays >= 3, Modifier.weight(1f))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Badge("On a roll", streak >= 3, Modifier.weight(1f))
                        Badge("Century club", totalSteps >= 100_000, Modifier.weight(1f))
                    }
                }
            }
            item { Text("Milestones", style = MaterialTheme.typography.titleLarge) }
            item { Milestone("Steps explorer", totalSteps, 100_000, "steps", HealthColors.Steps) }
            item { Milestone("Hydration hero", totalWater, 25_000, "ml", HealthColors.Water) }
            item { Milestone("Goal getter", goalDays, 10, "days", HealthColors.Calories) }
        }
    }
}

@Composable
private fun Badge(title: String, earned: Boolean, modifier: Modifier = Modifier) {
    SoftCard(modifier) {
        Column(Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                Modifier.size(44.dp).clip(CircleShape).background(if (earned) Color(0xFFF6E7C8) else MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    if (earned) Icons.Filled.EmojiEvents else Icons.Outlined.Lock,
                    null,
                    tint = if (earned) HealthColors.Award else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(title, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
            Text(
                if (earned) "Earned" else "Locked",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun Milestone(title: String, value: Int, target: Int, unit: String, color: Color) {
    SoftCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Row {
                Text(title, Modifier.weight(1f), fontWeight = FontWeight.SemiBold)
                Text("${value.grouped()} / ${target.grouped()} $unit", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.height(12.dp))
            GoalProgress(value.toFloat() / target, color)
        }
    }
}
