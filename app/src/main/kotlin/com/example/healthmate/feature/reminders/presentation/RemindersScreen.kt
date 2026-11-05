package com.example.healthmate.feature.reminders.presentation

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.healthmate.core.design.HealthColors
import com.example.healthmate.core.notifications.ReminderScheduler
import com.example.healthmate.feature.common.presentation.BackBar
import com.example.healthmate.feature.common.presentation.IconBadge
import com.example.healthmate.feature.common.presentation.Input
import com.example.healthmate.feature.common.presentation.SaveButton
import com.example.healthmate.feature.common.presentation.SoftCard
import com.example.healthmate.feature.records.data.ReminderSettings

@Composable
fun Reminders(current: ReminderSettings, save: (ReminderSettings) -> Unit, back: () -> Unit) {
    var settings by remember { mutableStateOf(current) }
    val context = androidx.compose.ui.platform.LocalContext.current
    val requestPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { }
    Scaffold(containerColor = MaterialTheme.colorScheme.background, topBar = { BackBar("Daily reminders", back) }) { padding ->
        Column(
            Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState()).imePadding().padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text("Gentle prompts, once a day.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            ReminderRow(
                "Drink water",
                "Stay hydrated through the day",
                settings.waterEnabled,
                settings.waterMinutes,
                HealthColors.Water,
                Icons.Filled.WaterDrop,
                { settings = settings.copy(waterEnabled = it) },
                { settings = settings.copy(waterMinutes = it) },
            )
            ReminderRow(
                "Go for a walk",
                "Take a short movement break",
                settings.walkingEnabled,
                settings.walkingMinutes,
                HealthColors.Steps,
                Icons.AutoMirrored.Filled.DirectionsWalk,
                { settings = settings.copy(walkingEnabled = it) },
                { settings = settings.copy(walkingMinutes = it) },
            )
            ReminderRow(
                "Daily activity",
                "Make time to move",
                settings.activityEnabled,
                settings.activityMinutes,
                HealthColors.Calories,
                Icons.Filled.LocalFireDepartment,
                { settings = settings.copy(activityEnabled = it) },
                { settings = settings.copy(activityMinutes = it) },
            )
            SaveButton("Save reminders") {
                if (Build.VERSION.SDK_INT >= 33) requestPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
                listOf(
                    Triple(101, settings.waterEnabled, settings.waterMinutes),
                    Triple(102, settings.walkingEnabled, settings.walkingMinutes),
                    Triple(103, settings.activityEnabled, settings.activityMinutes),
                ).forEachIndexed { index, (id, enabled, minutes) ->
                    if (enabled) {
                        ReminderScheduler.schedule(
                            context,
                            id,
                            minutes,
                            listOf("Drink some water", "Time for a walk", "Move your body")[index],
                            listOf(
                                "A little hydration goes a long way.",
                                "Take a short walk and add to your step count.",
                                "Make time for your daily activity.",
                            )[index],
                        )
                    } else {
                        ReminderScheduler.cancel(context, id)
                    }
                }
                save(settings)
            }
        }
    }
}

@Composable
private fun ReminderRow(
    title: String,
    subtitle: String,
    enabled: Boolean,
    minutes: Int,
    color: Color,
    icon: ImageVector,
    toggle: (Boolean) -> Unit,
    updateMinutes: (Int) -> Unit,
) {
    var text by remember(minutes) { mutableStateOf("%02d:%02d".format(minutes / 60, minutes % 60)) }
    SoftCard(Modifier.fillMaxWidth(), container = if (enabled) color.copy(alpha = 0.10f) else MaterialTheme.colorScheme.surface) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconBadge(icon, color)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(title, fontWeight = FontWeight.SemiBold)
                    Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(enabled, toggle)
            }
            if (enabled) {
                Input(
                    text,
                    {
                        text = it
                        Regex("^(\\d{1,2}):(\\d{2})$").matchEntire(it)?.let { match ->
                            val hour = match.groupValues[1].toInt()
                            val minute = match.groupValues[2].toInt()
                            if (hour in 0..23 && minute in 0..59) updateMinutes(hour * 60 + minute)
                        }
                    },
                    "Daily at (HH:mm)",
                )
            }
        }
    }
}
