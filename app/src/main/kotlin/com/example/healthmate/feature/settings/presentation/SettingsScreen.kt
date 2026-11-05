package com.example.healthmate.feature.settings.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.healthmate.app.navigation.Routes
import com.example.healthmate.core.design.HealthColors
import com.example.healthmate.feature.common.presentation.SettingItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Settings(open: (String) -> Unit, back: () -> Unit) {
    val barColors = TopAppBarDefaults.topAppBarColors(
        containerColor = MaterialTheme.colorScheme.background,
        titleContentColor = MaterialTheme.colorScheme.onBackground,
    )
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.SemiBold) },
                navigationIcon = { IconButton(back) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } },
                colors = barColors,
                windowInsets = androidx.compose.foundation.layout.WindowInsets(0, 0, 0, 0),
            )
        },
    ) { padding ->
        LazyColumn(
            Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Text("Shape HealthMate around your routine.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            item { SettingItem("Your profile", "Age, height, weight, and preferences", Icons.Outlined.Person, HealthColors.Steps) { open(Routes.PROFILE) } }
            item { SettingItem("Daily goals", "Steps, calories, and water targets", Icons.Outlined.Flag, HealthColors.Steps) { open(Routes.GOALS) } }
            item { SettingItem("Achievements", "Badges, streaks, and milestones", Icons.Outlined.EmojiEvents, HealthColors.Award) { open(Routes.ACHIEVEMENTS) } }
            item { SettingItem("BMI calculator", "A quick check from height and weight", Icons.Outlined.Calculate, HealthColors.Water) { open(Routes.BMI) } }
            item { SettingItem("Reminders", "Water, walking, and activity prompts", Icons.Outlined.Notifications, HealthColors.Calories) { open(Routes.REMINDERS) } }
        }
    }
}
