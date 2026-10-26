package com.example.healthmate.core.design

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object HealthColors { val Ink = Color(0xFF1D274B); val Canvas = Color(0xFFF1F4FF); val Steps = Color(0xFF167348); val Calories = Color(0xFFB3262C); val Water = Color(0xFF1855A9); val Seed = Color(0xFF7080F5) }

private val Light = lightColorScheme(primary = HealthColors.Seed, secondary = HealthColors.Steps, background = HealthColors.Canvas, surface = Color.White)
private val Dark = darkColorScheme(primary = Color(0xFFAEB8FF), secondary = Color(0xFF90D9AC))

@Composable fun HealthMateTheme(content: @Composable () -> Unit) = MaterialTheme(colorScheme = if (androidx.compose.foundation.isSystemInDarkTheme()) Dark else Light, typography = Typography(), content = content)
