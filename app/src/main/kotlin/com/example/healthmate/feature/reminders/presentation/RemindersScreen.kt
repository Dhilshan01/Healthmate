package com.example.healthmate.feature.reminders.presentation

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import com.example.healthmate.core.design.HealthColors
import com.example.healthmate.core.notifications.ReminderScheduler
import com.example.healthmate.feature.common.presentation.BackBar
import com.example.healthmate.feature.common.presentation.Input
import com.example.healthmate.feature.records.data.ReminderSettings

@OptIn(ExperimentalMaterial3Api::class)
@Composable fun Reminders(current: ReminderSettings, save: (ReminderSettings) -> Unit, back: () -> Unit) { var settings by remember { mutableStateOf(current) }; val context = androidx.compose.ui.platform.LocalContext.current; val requestPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { }
    Scaffold(topBar = { BackBar("Daily reminders", back) }) { p -> Column(Modifier.padding(p).padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) { Text("Gentle prompts for your healthy habits."); ReminderRow("Drink water", "Stay hydrated throughout the day", settings.waterEnabled, settings.waterMinutes, HealthColors.Water, { settings = settings.copy(waterEnabled = it) }, { settings = settings.copy(waterMinutes = it) }); ReminderRow("Go for a walk", "Take a short movement break", settings.walkingEnabled, settings.walkingMinutes, HealthColors.Steps, { settings = settings.copy(walkingEnabled = it) }, { settings = settings.copy(walkingMinutes = it) }); ReminderRow("Daily activity", "Make time to move your body", settings.activityEnabled, settings.activityMinutes, HealthColors.Calories, { settings = settings.copy(activityEnabled = it) }, { settings = settings.copy(activityMinutes = it) }); Button({ if (Build.VERSION.SDK_INT >= 33) requestPermission.launch(Manifest.permission.POST_NOTIFICATIONS); listOf(Triple(101, settings.waterEnabled, settings.waterMinutes), Triple(102, settings.walkingEnabled, settings.walkingMinutes), Triple(103, settings.activityEnabled, settings.activityMinutes)).forEachIndexed { i, (id, enabled, minutes) -> if (enabled) ReminderScheduler.schedule(context, id, minutes, listOf("Drink some water", "Time for a walk", "Move your body")[i], listOf("A little hydration goes a long way.", "Take a short walk and add to your step count.", "Make time for your daily activity.")[i]) else ReminderScheduler.cancel(context, id) }; save(settings) }, Modifier.fillMaxWidth()) { Text("Save reminders") } } }
}

@Composable private fun ReminderRow(title: String, subtitle: String, enabled: Boolean, minutes: Int, color: Color, toggle: (Boolean) -> Unit, updateMinutes: (Int) -> Unit) {
    var text by remember(minutes) { mutableStateOf("%02d:%02d".format(minutes / 60, minutes % 60)) }
    Card(colors = CardDefaults.cardColors(containerColor = color.copy(alpha = if (enabled) .13f else .06f))) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) { Text(title, fontWeight = FontWeight.Bold); Text(subtitle, style = MaterialTheme.typography.bodySmall) }
                Switch(enabled, toggle)
            }
            if (enabled) Input(text, {
                text = it
                Regex("^(\\d{1,2}):(\\d{2})$").matchEntire(it)?.let { match ->
                    val hour = match.groupValues[1].toInt(); val minute = match.groupValues[2].toInt()
                    if (hour in 0..23 && minute in 0..59) updateMinutes(hour * 60 + minute)
                }
            }, "Daily at (HH:mm)")
        }
    }
}
