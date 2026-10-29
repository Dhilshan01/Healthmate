package com.example.healthmate.feature.records.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import com.example.healthmate.core.design.HealthColors
import com.example.healthmate.feature.common.presentation.*
import com.example.healthmate.feature.records.data.*

@Composable fun History(records: List<HealthRecord>, edit: (Long) -> Unit, delete: (HealthRecord) -> Unit) {
    var filter by remember { mutableStateOf("") }
    val shown = records.filter { filter.isBlank() || it.date.contains(filter) }
    var pending by remember { mutableStateOf<HealthRecord?>(null) }
    Scaffold(topBar = { TopAppBar(title = { Text("History") }, windowInsets = WindowInsets(0, 0, 0, 0)) }) { p ->
        LazyColumn(Modifier.padding(p).fillMaxSize(), contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            item { Text("Your activity timeline", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = HealthColors.Ink); Text("Review, update, or remove your daily health records.", color = Color(0xFF59658C)) }
            item { OutlinedTextField(filter, { filter = it }, Modifier.fillMaxWidth(), label = { Text("Find by date") }, placeholder = { Text("YYYY-MM-DD") }, leadingIcon = { Icon(Icons.Outlined.Search, "Search records") }, trailingIcon = if (filter.isNotEmpty()) {{ IconButton({ filter = "" }) { Icon(Icons.Filled.Close, "Clear search") } }} else null, singleLine = true) }
            item { Text(if (filter.isBlank()) "${shown.size} record${if (shown.size == 1) "" else "s"}" else "${shown.size} matching record${if (shown.size == 1) "" else "s"}", style = MaterialTheme.typography.labelLarge, color = Color(0xFF59658C)) }
            if (shown.isEmpty()) item { Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = HealthColors.Steps.copy(alpha = .08f))) { Column(Modifier.fillMaxWidth().padding(28.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) { Icon(Icons.Outlined.History, null, tint = HealthColors.Steps, modifier = Modifier.size(42.dp)); Text(if (filter.isBlank()) "No records yet" else "No matching records", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold); Text(if (filter.isBlank()) "Add your first activity from the Add tab." else "Try a different date or clear the filter.", color = Color(0xFF59658C)) } } }
            else items(shown, key = { it.id }) { r -> Card(Modifier.fillMaxWidth().clickable { edit(r.id) }) { Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) { Row(verticalAlignment = Alignment.CenterVertically) { Column(Modifier.weight(1f)) { Text(r.date, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = HealthColors.Ink); Text("Tap to edit this record", style = MaterialTheme.typography.bodySmall, color = Color(0xFF59658C)) }; IconButton({ pending = r }) { Icon(Icons.Outlined.Delete, "Delete record", tint = MaterialTheme.colorScheme.error) } }; HorizontalDivider(color = Color(0xFFE5E8F2)); Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { HistoryMetric("Steps", r.steps.toString(), Icons.Filled.DirectionsWalk, HealthColors.Steps); HistoryMetric("Calories", "${r.calories} kcal", Icons.Filled.LocalFireDepartment, HealthColors.Calories); HistoryMetric("Water", "${r.water} ml", Icons.Filled.WaterDrop, HealthColors.Water) } } } }
        }
    }
    pending?.let { r -> AlertDialog(onDismissRequest = { pending = null }, title = { Text("Delete record?") }, text = { Text("The record for ${r.date} will be permanently removed.") }, confirmButton = { TextButton({ delete(r); pending = null }) { Text("Delete") } }, dismissButton = { TextButton({ pending = null }) { Text("Cancel") } }) }
}

@Composable fun RecordForm(record: HealthRecord?, onBack: () -> Unit, onSave: (HealthRecord) -> Unit) { var date by remember { mutableStateOf(record?.date ?: LocalDate.now().toString()) }; var steps by remember { mutableStateOf(record?.steps?.toString() ?: "") }; var calories by remember { mutableStateOf(record?.calories?.toString() ?: "") }; var water by remember { mutableStateOf(record?.water?.toString() ?: "") }; var error by remember { mutableStateOf<String?>(null) }
    Scaffold(topBar = { TopAppBar(title = { Text(if (record == null) "Add record" else "Edit record") }, navigationIcon = { IconButton(onBack) { Icon(Icons.Filled.ArrowBack, "Back") } }, windowInsets = WindowInsets(0, 0, 0, 0)) }) { p -> Column(Modifier.padding(p).padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) { Input(date, { date = it }, "Date (yyyy-mm-dd)"); Input(steps, { steps = it }, "Steps", KeyboardType.Number); Input(calories, { calories = it }, "Calories burned (kcal)", KeyboardType.Number); Input(water, { water = it }, "Water intake (ml)", KeyboardType.Number); error?.let { Text(it, color = MaterialTheme.colorScheme.error) }; Button({ val s = steps.toIntOrNull(); val c = calories.toIntOrNull(); val w = water.toIntOrNull(); if (runCatching { LocalDate.parse(date) }.isFailure || s == null || c == null || w == null || s !in 0..100000 || c !in 0..20000 || w !in 0..20000) error = "Enter a valid date and non-negative values within the allowed limits." else onSave(HealthRecord(record?.id ?: 0, date, s, c, w)) }, Modifier.fillMaxWidth()) { Text(if (record == null) "Save" else "Update") } } }
}

@Composable private fun HistoryMetric(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color) = Row(verticalAlignment = Alignment.CenterVertically) { Icon(icon, null, tint = color, modifier = Modifier.size(20.dp)); Spacer(Modifier.width(6.dp)); Column { Text(value, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold); Text(label, style = MaterialTheme.typography.labelSmall, color = Color(0xFF59658C)) } }
