package com.example.healthmate.feature.bmi.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.healthmate.core.design.HealthColors
import com.example.healthmate.feature.common.presentation.BackBar
import com.example.healthmate.feature.common.presentation.Input
import com.example.healthmate.feature.records.data.UserProfile

@OptIn(ExperimentalMaterial3Api::class)
@Composable fun BmiCalculator(profile: UserProfile, back: () -> Unit) {
    var height by remember(profile.heightCm) { mutableStateOf(profile.heightCm?.let { "%.1f".format(it) }.orEmpty()) }
    var weight by remember(profile.weightKg) { mutableStateOf(profile.weightKg?.let { "%.1f".format(it) }.orEmpty()) }
    var error by remember { mutableStateOf<String?>(null) }
    val h = height.toDoubleOrNull(); val w = weight.toDoubleOrNull()
    val bmi = if (h != null && w != null && h in 50.0..300.0 && w in 10.0..500.0) w / ((h / 100) * (h / 100)) else null
    val result = bmi?.let { value -> when { value < 18.5 -> Triple("Underweight", "A little below the usual range", HealthColors.Water); value < 25 -> Triple("Healthy range", "Within the usual BMI range", HealthColors.Steps); value < 30 -> Triple("Overweight", "Above the usual BMI range", Color(0xFFE18A00)); else -> Triple("Obesity range", "Well above the usual BMI range", HealthColors.Calories) } }
    Scaffold(topBar = { BackBar("BMI calculator", back) }) { padding -> LazyColumn(Modifier.padding(padding).padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item { Text("Body mass index", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold); Text("A quick screening calculation based on height and weight.", color = Color(0xFF59658C)) }
        if (profile.heightCm != null && profile.weightKg != null) item { Card(colors = CardDefaults.cardColors(containerColor = HealthColors.Seed.copy(alpha = .10f))) { Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Outlined.Person, null, tint = HealthColors.Seed); Spacer(Modifier.width(12.dp)); Text("Values prefilled from your profile") } } }
        item { Input(height, { height = it; error = null }, "Height (cm)", KeyboardType.Decimal) }
        item { Input(weight, { weight = it; error = null }, "Weight (kg)", KeyboardType.Decimal) }
        item { Button({ if (bmi == null) error = "Enter height from 50 to 300 cm and weight from 10 to 500 kg." else error = null }, Modifier.fillMaxWidth()) { Text("Calculate BMI") } }
        error?.let { message -> item { Text(message, color = MaterialTheme.colorScheme.error) } }
        result?.let { (label, description, color) -> item { Card(colors = CardDefaults.cardColors(containerColor = color.copy(alpha = .12f))) { Column(Modifier.padding(22.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) { Text(label, color = color, fontWeight = FontWeight.Bold); Text("%.1f".format(bmi), style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Bold); Text(description); Text("BMI", style = MaterialTheme.typography.labelLarge, color = Color(0xFF59658C)) } } } }
    } }
}

