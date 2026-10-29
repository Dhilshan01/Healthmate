package com.example.healthmate.feature.profile.presentation

import androidx.compose.foundation.layout.*
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
@Composable fun Profile(current: UserProfile, save: (UserProfile) -> Unit, back: () -> Unit) {
    var age by remember(current) { mutableStateOf(current.age?.toString().orEmpty()) }
    var height by remember(current) { mutableStateOf(current.heightCm?.toString().orEmpty()) }
    var weight by remember(current) { mutableStateOf(current.weightKg?.toString().orEmpty()) }
    var activity by remember(current) { mutableStateOf(current.activityLevel) }
    var metric by remember(current) { mutableStateOf(current.useMetricUnits) }
    var expanded by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val activityLevels = listOf("Sedentary", "Lightly active", "Moderately active", "Very active")
    Scaffold(topBar = { BackBar("Your profile", back) }) { p ->
        Column(Modifier.padding(p).padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text("Personal details", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("These details stay on this device and help personalize HealthMate.", color = Color(0xFF59658C))
            Input(age, { age = it }, "Age", KeyboardType.Number)
            Input(height, { height = it }, "Height (cm)", KeyboardType.Decimal)
            Input(weight, { weight = it }, "Weight (kg)", KeyboardType.Decimal)
            Text("Preferences", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                OutlinedTextField(activity, {}, Modifier.menuAnchor().fillMaxWidth(), readOnly = true, label = { Text("Activity level") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) }, singleLine = true)
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) { activityLevels.forEach { level -> DropdownMenuItem(text = { Text(level) }, onClick = { activity = level; expanded = false }) } }
            }
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) { Column(Modifier.weight(1f)) { Text("Use metric units", fontWeight = FontWeight.Bold); Text("Centimetres and kilograms", style = MaterialTheme.typography.bodySmall, color = Color(0xFF59658C)) }; Switch(metric, { metric = it }) }
            error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            Button({
                val parsedAge = age.toIntOrNull(); val parsedHeight = height.toDoubleOrNull(); val parsedWeight = weight.toDoubleOrNull()
                if (parsedAge == null || parsedAge !in 1..120 || parsedHeight == null || parsedHeight !in 50.0..300.0 || parsedWeight == null || parsedWeight !in 10.0..500.0) error = "Enter an age from 1Ã¢â‚¬â€œ120, height from 50Ã¢â‚¬â€œ300 cm, and weight from 10Ã¢â‚¬â€œ500 kg."
                else { error = null; save(UserProfile(age = parsedAge, heightCm = parsedHeight, weightKg = parsedWeight, activityLevel = activity, useMetricUnits = metric)) }
            }, Modifier.fillMaxWidth()) { Text("Save profile") }
        }
    }
}

