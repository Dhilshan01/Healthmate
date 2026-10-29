package com.example.healthmate.feature.settings.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.healthmate.app.navigation.Routes
import com.example.healthmate.core.design.HealthColors
import com.example.healthmate.feature.common.presentation.SettingItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable fun Settings(open: (String) -> Unit, back: () -> Unit) = Scaffold(topBar = { TopAppBar(title = { Text("Settings") }, navigationIcon = { IconButton(back) { Icon(Icons.Filled.ArrowBack, "Back") } }, windowInsets = WindowInsets(0, 0, 0, 0)) }) { p -> LazyColumn(Modifier.padding(p).padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) { item { Text("Make HealthMate fit your routine.", style = MaterialTheme.typography.titleMedium) }; item { SettingItem("Your profile", "Age, height, weight, and preferences", Icons.Outlined.Person, HealthColors.Seed) { open(Routes.PROFILE) } }; item { SettingItem("Daily goals", "Steps, calories, and water targets", Icons.Outlined.Flag, HealthColors.Steps) { open(Routes.GOALS) } }; item { SettingItem("Achievements", "Badges, streaks, and fitness milestones", Icons.Outlined.EmojiEvents, Color(0xFFE18A00)) { open(Routes.ACHIEVEMENTS) } }; item { SettingItem("BMI calculator", "Calculate BMI from height and weight", Icons.Outlined.Calculate, HealthColors.Seed) { open(Routes.BMI) } }; item { SettingItem("Reminders", "Water, walking, and activity prompts", Icons.Outlined.Notifications, HealthColors.Seed) { open(Routes.REMINDERS) } } } }
