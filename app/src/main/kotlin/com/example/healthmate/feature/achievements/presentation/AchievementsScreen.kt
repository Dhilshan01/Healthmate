package com.example.healthmate.feature.achievements.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import com.example.healthmate.core.design.HealthColors
import com.example.healthmate.feature.common.presentation.BackBar
import com.example.healthmate.feature.records.data.HealthRecord
import com.example.healthmate.feature.records.data.DailyGoals

@Composable fun Achievements(records: List<HealthRecord>, goals: DailyGoals, back: () -> Unit) { val totalSteps = records.sumOf { it.steps }; val totalWater = records.sumOf { it.water }; val days = records.groupBy { it.date }.mapValues { (_, r) -> Triple(r.sumOf { it.steps }, r.sumOf { it.calories }, r.sumOf { it.water }) }; val goalDays = days.values.count { it.first >= goals.steps || it.second >= goals.calories || it.third >= goals.water }; var streak = 0; var day = LocalDate.now(); while (days[day.toString()]?.let { it.first >= goals.steps || it.second >= goals.calories || it.third >= goals.water } == true) { streak++; day = day.minusDays(1) }
    Scaffold(topBar = { BackBar("Achievements", back) }) { p -> LazyColumn(Modifier.padding(p).padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) { item { Text("Your journey", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold); Text("Every recorded day helps build a healthier routine.") }; item { Card(colors = CardDefaults.cardColors(containerColor = HealthColors.Ink)) { Row(Modifier.padding(22.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Filled.LocalFireDepartment, null, tint = Color(0xFFFFC45A), modifier = Modifier.size(46.dp)); Spacer(Modifier.width(18.dp)); Column { Text("$streak day${if (streak == 1) "" else "s"}", color = Color.White, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold); Text(if (streak == 0) "Record an activity to begin your streak." else "Keep showing upÃ¢â‚¬â€your streak is growing!", color = Color(0xFFC8D0FF)) } } } }; item { Text("Badges", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) }; item { Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) { Badge("First step", records.isNotEmpty()); Badge("Goal getter", goalDays >= 3); Badge("On a roll", streak >= 3); Badge("Century club", totalSteps >= 100000) } }; item { Text("Fitness milestones", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) }; item { Milestone("Steps explorer", totalSteps, 100000, "steps", HealthColors.Steps) }; item { Milestone("Hydration hero", totalWater, 25000, "ml", HealthColors.Water) }; item { Milestone("Goal getter", goalDays, 10, "days", HealthColors.Seed) } } }
}

@Composable private fun Badge(title: String, earned: Boolean) = Card(Modifier.width(82.dp)) { Column(Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) { Icon(if (earned) Icons.Filled.EmojiEvents else Icons.Outlined.Lock, null, tint = if (earned) Color(0xFFE18A00) else Color.Gray); Text(title, style = MaterialTheme.typography.labelSmall, maxLines = 2) } }

@Composable private fun Milestone(title: String, value: Int, target: Int, unit: String, color: Color) = Card { Column(Modifier.padding(16.dp)) { Row { Text(title, Modifier.weight(1f), fontWeight = FontWeight.Bold); Text("$value / $target $unit", style = MaterialTheme.typography.labelSmall) }; Spacer(Modifier.height(10.dp)); LinearProgressIndicator(progress = { (value.toFloat() / target).coerceIn(0f, 1f) }, color = color, modifier = Modifier.fillMaxWidth()) } }
