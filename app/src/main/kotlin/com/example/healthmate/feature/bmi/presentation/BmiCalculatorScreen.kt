package com.example.healthmate.feature.bmi.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.healthmate.core.design.HealthColors
import com.example.healthmate.feature.common.presentation.BackBar
import com.example.healthmate.feature.common.presentation.IconBadge
import com.example.healthmate.feature.common.presentation.Input
import com.example.healthmate.feature.common.presentation.SaveButton
import com.example.healthmate.feature.common.presentation.SoftCard
import com.example.healthmate.feature.records.data.UserProfile

@Composable
fun BmiCalculator(profile: UserProfile, back: () -> Unit) {
    var height by remember(profile.heightCm) { mutableStateOf(profile.heightCm?.let { "%.1f".format(it) }.orEmpty()) }
    var weight by remember(profile.weightKg) { mutableStateOf(profile.weightKg?.let { "%.1f".format(it) }.orEmpty()) }
    var error by remember { mutableStateOf<String?>(null) }
    val parsedHeight = height.toDoubleOrNull()
    val parsedWeight = weight.toDoubleOrNull()
    val bmi = if (parsedHeight != null && parsedWeight != null && parsedHeight in 50.0..300.0 && parsedWeight in 10.0..500.0) {
        parsedWeight / ((parsedHeight / 100) * (parsedHeight / 100))
    } else {
        null
    }
    val result = bmi?.let { value ->
        when {
            value < 18.5 -> Triple("Underweight", "A little below the usual range", HealthColors.Water)
            value < 25 -> Triple("Healthy range", "Within the usual BMI range", HealthColors.Steps)
            value < 30 -> Triple("Overweight", "Above the usual BMI range", HealthColors.Award)
            else -> Triple("Obesity range", "Well above the usual BMI range", HealthColors.Calories)
        }
    }
    Scaffold(containerColor = MaterialTheme.colorScheme.background, topBar = { BackBar("BMI calculator", back) }) { padding ->
        LazyColumn(
            Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                Text("A quick screening calculation from height and weight.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (profile.heightCm != null && profile.weightKg != null) {
                item {
                    SoftCard(Modifier.fillMaxWidth(), container = MaterialTheme.colorScheme.primaryContainer) {
                        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            IconBadge(Icons.Outlined.Person, HealthColors.Steps)
                            Spacer(Modifier.width(12.dp))
                            Text("Filled in from your profile", color = MaterialTheme.colorScheme.onPrimaryContainer)
                        }
                    }
                }
            }
            item {
                SoftCard(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Input(height, { height = it; error = null }, "Height (cm)", KeyboardType.Decimal)
                        Input(weight, { weight = it; error = null }, "Weight (kg)", KeyboardType.Decimal)
                    }
                }
            }
            item {
                SaveButton("Calculate BMI") {
                    error = if (bmi == null) "Enter height from 50 to 300 cm and weight from 10 to 500 kg." else null
                }
            }
            error?.let { message -> item { Text(message, color = MaterialTheme.colorScheme.error) } }
            result?.let { (label, description, color) ->
                item {
                    SoftCard(Modifier.fillMaxWidth(), container = color.copy(alpha = 0.12f)) {
                        Column(Modifier.padding(22.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(label, color = color, fontWeight = FontWeight.SemiBold)
                            Text("%.1f".format(bmi), style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.SemiBold)
                            Text(description)
                            Text("BMI", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}
