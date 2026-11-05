package com.example.healthmate.feature.records.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.healthmate.core.design.HealthColors
import com.example.healthmate.feature.common.presentation.IconBadge
import com.example.healthmate.feature.common.presentation.Input
import com.example.healthmate.feature.common.presentation.SaveButton
import com.example.healthmate.feature.common.presentation.SoftCard
import com.example.healthmate.feature.common.presentation.grouped
import com.example.healthmate.feature.records.data.HealthRecord
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun History(records: List<HealthRecord>, edit: (Long) -> Unit, delete: (HealthRecord) -> Unit) {
    var filter by remember { mutableStateOf("") }
    val shown = records.filter { filter.isBlank() || it.date.contains(filter) }
    var pending by remember { mutableStateOf<HealthRecord?>(null) }
    val barColors = TopAppBarDefaults.topAppBarColors(
        containerColor = MaterialTheme.colorScheme.background,
        titleContentColor = MaterialTheme.colorScheme.onBackground,
    )
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { TopAppBar(title = { Text("History", fontWeight = FontWeight.SemiBold) }, colors = barColors, windowInsets = androidx.compose.foundation.layout.WindowInsets(0, 0, 0, 0)) },
    ) { padding ->
        LazyColumn(
            Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                Text("Review, update, or remove a day.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            item {
                OutlinedTextField(
                    value = filter,
                    onValueChange = { filter = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Find by date") },
                    placeholder = { Text("YYYY-MM-DD") },
                    leadingIcon = { Icon(Icons.Outlined.Search, "Search records") },
                    trailingIcon = if (filter.isNotEmpty()) {
                        { IconButton({ filter = "" }) { Icon(Icons.Filled.Close, "Clear search") } }
                    } else {
                        null
                    },
                    singleLine = true,
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                )
            }
            item {
                Text(
                    if (filter.isBlank()) "${shown.size} record${if (shown.size == 1) "" else "s"}" else "${shown.size} matching record${if (shown.size == 1) "" else "s"}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            if (shown.isEmpty()) {
                item { EmptyHistory(filter.isBlank()) }
            } else {
                items(shown, key = { it.id }) { record ->
                    SoftCard(Modifier.fillMaxWidth(), onClick = { edit(record.id) }) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Column(Modifier.weight(1f)) {
                                    Text(record.date, style = MaterialTheme.typography.titleMedium)
                                    Text("Tap to edit", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                IconButton({ pending = record }) {
                                    Icon(Icons.Outlined.Delete, "Delete record", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.7f))
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                HistoryMetric("Steps", record.steps.grouped(), Icons.AutoMirrored.Filled.DirectionsWalk, HealthColors.Steps)
                                HistoryMetric("Calories", "${record.calories.grouped()} kcal", Icons.Filled.LocalFireDepartment, HealthColors.Calories)
                                HistoryMetric("Water", "${record.water.grouped()} ml", Icons.Filled.WaterDrop, HealthColors.Water)
                            }
                        }
                    }
                }
            }
        }
    }
    pending?.let { record ->
        AlertDialog(
            onDismissRequest = { pending = null },
            title = { Text("Delete record?") },
            text = { Text("The record for ${record.date} will be permanently removed.") },
            confirmButton = { TextButton({ delete(record); pending = null }) { Text("Delete") } },
            dismissButton = { TextButton({ pending = null }) { Text("Cancel") } },
        )
    }
}

@Composable
private fun EmptyHistory(noRecords: Boolean) {
    SoftCard(Modifier.fillMaxWidth(), container = MaterialTheme.colorScheme.primaryContainer) {
        Column(
            Modifier.fillMaxWidth().padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            IconBadge(Icons.Outlined.History, HealthColors.Steps)
            Text(
                if (noRecords) "No records yet" else "No matching records",
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                if (noRecords) "Add your first activity from the Add tab, or use the quick buttons on Home." else "Try a different date or clear the filter.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun HistoryMetric(label: String, value: String, icon: ImageVector, color: androidx.compose.ui.graphics.Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = color, modifier = Modifier.height(18.dp))
        Spacer(Modifier.width(6.dp))
        Column {
            Text(value, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordForm(record: HealthRecord?, onBack: () -> Unit, onSave: (HealthRecord) -> Unit) {
    var date by remember { mutableStateOf(record?.date ?: LocalDate.now().toString()) }
    var steps by remember { mutableStateOf(record?.steps?.toString() ?: "") }
    var calories by remember { mutableStateOf(record?.calories?.toString() ?: "") }
    var water by remember { mutableStateOf(record?.water?.toString() ?: "") }
    var error by remember { mutableStateOf<String?>(null) }
    val barColors = TopAppBarDefaults.topAppBarColors(
        containerColor = MaterialTheme.colorScheme.background,
        titleContentColor = MaterialTheme.colorScheme.onBackground,
    )
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(if (record == null) "Add record" else "Edit record", fontWeight = FontWeight.SemiBold) },
                navigationIcon = { IconButton(onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } },
                colors = barColors,
                windowInsets = androidx.compose.foundation.layout.WindowInsets(0, 0, 0, 0),
            )
        },
    ) { padding ->
        Column(
            Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState()).imePadding().padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text(
                "Enter the full day, or use the quick buttons on Home for a small top-up.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            SoftCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Input(date, { date = it }, "Date (yyyy-mm-dd)")
                    Input(steps, { steps = it }, "Steps", KeyboardType.Number)
                    Input(calories, { calories = it }, "Calories burned (kcal)", KeyboardType.Number)
                    Input(water, { water = it }, "Water intake (ml)", KeyboardType.Number)
                }
            }
            error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            SaveButton(if (record == null) "Save record" else "Update record") {
                val parsedSteps = steps.toIntOrNull()
                val parsedCalories = calories.toIntOrNull()
                val parsedWater = water.toIntOrNull()
                val invalid = runCatching { LocalDate.parse(date) }.isFailure ||
                    parsedSteps == null || parsedCalories == null || parsedWater == null ||
                    parsedSteps !in 0..100_000 || parsedCalories !in 0..20_000 || parsedWater !in 0..20_000
                if (invalid) {
                    error = "Enter a valid date and non-negative values within the allowed limits."
                } else {
                    onSave(HealthRecord(record?.id ?: 0, date, parsedSteps!!, parsedCalories!!, parsedWater!!))
                }
            }
        }
    }
}
