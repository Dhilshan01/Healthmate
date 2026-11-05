package com.example.healthmate.feature.profile.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
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
import com.example.healthmate.feature.common.presentation.BackBar
import com.example.healthmate.feature.common.presentation.Input
import com.example.healthmate.feature.common.presentation.SaveButton
import com.example.healthmate.feature.common.presentation.SoftCard
import com.example.healthmate.feature.records.data.UserProfile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Profile(current: UserProfile, save: (UserProfile) -> Unit, back: () -> Unit) {
    var age by remember(current) { mutableStateOf(current.age?.toString().orEmpty()) }
    var height by remember(current) { mutableStateOf(current.heightCm?.toString().orEmpty()) }
    var weight by remember(current) { mutableStateOf(current.weightKg?.toString().orEmpty()) }
    var activity by remember(current) { mutableStateOf(current.activityLevel) }
    var metric by remember(current) { mutableStateOf(current.useMetricUnits) }
    var expanded by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val activityLevels = listOf("Sedentary", "Lightly active", "Moderately active", "Very active")
    Scaffold(containerColor = MaterialTheme.colorScheme.background, topBar = { BackBar("Your profile", back) }) { padding ->
        Column(
            Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState()).imePadding().padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text("These details stay on this device.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            SoftCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Personal details", style = MaterialTheme.typography.titleMedium)
                    Input(age, { age = it }, "Age", KeyboardType.Number)
                    Input(height, { height = it }, "Height (cm)", KeyboardType.Decimal)
                    Input(weight, { weight = it }, "Weight (kg)", KeyboardType.Decimal)
                }
            }
            SoftCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Preferences", style = MaterialTheme.typography.titleMedium)
                    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                        OutlinedTextField(
                            value = activity,
                            onValueChange = {},
                            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
                            readOnly = true,
                            label = { Text("Activity level") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                            singleLine = true,
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                        )
                        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            activityLevels.forEach { level ->
                                DropdownMenuItem(text = { Text(level) }, onClick = { activity = level; expanded = false })
                            }
                        }
                    }
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text("Use metric units", fontWeight = FontWeight.SemiBold)
                            Text("Centimetres and kilograms", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(metric, { metric = it })
                    }
                }
            }
            error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            SaveButton("Save profile") {
                val parsedAge = age.toIntOrNull()
                val parsedHeight = height.toDoubleOrNull()
                val parsedWeight = weight.toDoubleOrNull()
                if (parsedAge == null || parsedAge !in 1..120 || parsedHeight == null || parsedHeight !in 50.0..300.0 || parsedWeight == null || parsedWeight !in 10.0..500.0) {
                    error = "Enter an age from 1 to 120, height from 50 to 300 cm, and weight from 10 to 500 kg."
                } else {
                    error = null
                    save(UserProfile(age = parsedAge, heightCm = parsedHeight, weightKg = parsedWeight, activityLevel = activity, useMetricUnits = metric))
                }
            }
        }
    }
}
