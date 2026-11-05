package com.example.healthmate.core.design

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat

object HealthColors {
    val Steps = Color(0xFF2F8F68)
    val Water = Color(0xFF3D8EBF)
    val Calories = Color(0xFFC4845A)
    val Award = Color(0xFFC4922A)
}

private val Light = lightColorScheme(
    primary = Color(0xFF2F8F68),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDCEFE6),
    onPrimaryContainer = Color(0xFF143528),
    secondary = Color(0xFF3D8EBF),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFDDEFF8),
    onSecondaryContainer = Color(0xFF143246),
    tertiary = Color(0xFFC4845A),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFF8EDE4),
    onTertiaryContainer = Color(0xFF3D2918),
    background = Color(0xFFF3F6F2),
    onBackground = Color(0xFF1E332C),
    surface = Color.White,
    onSurface = Color(0xFF1E332C),
    onSurfaceVariant = Color(0xFF5C6F66),
    surfaceVariant = Color(0xFFE6EEE8),
    outline = Color(0xFFD3E0D8),
    error = Color(0xFFB94A48),
)

private val Dark = darkColorScheme(
    primary = Color(0xFF8FCBB0),
    onPrimary = Color(0xFF0E241C),
    primaryContainer = Color(0xFF1E3D32),
    onPrimaryContainer = Color(0xFFD7F3E6),
    secondary = Color(0xFF8EC4E4),
    onSecondary = Color(0xFF082433),
    secondaryContainer = Color(0xFF1A3344),
    onSecondaryContainer = Color(0xFFD7EEF8),
    tertiary = Color(0xFFE7B892),
    onTertiary = Color(0xFF2C1A0E),
    tertiaryContainer = Color(0xFF3D2A1C),
    onTertiaryContainer = Color(0xFFF8EDE4),
    background = Color(0xFF101614),
    onBackground = Color(0xFFE6F0EA),
    surface = Color(0xFF1A2420),
    onSurface = Color(0xFFE6F0EA),
    onSurfaceVariant = Color(0xFFB7C7BE),
    surfaceVariant = Color(0xFF2A3832),
    outline = Color(0xFF3E5248),
    error = Color(0xFFFFB4AB),
)

private val HealthShapes = Shapes(
    extraSmall = RoundedCornerShape(12.dp),
    small = RoundedCornerShape(16.dp),
    medium = RoundedCornerShape(24.dp),
    large = RoundedCornerShape(28.dp),
    extraLarge = RoundedCornerShape(32.dp),
)

private val HealthTypography: Typography
    get() {
        val base = Typography()
        return base.copy(
            headlineMedium = base.headlineMedium.copy(fontWeight = FontWeight.SemiBold, letterSpacing = (-0.4).sp),
            headlineSmall = base.headlineSmall.copy(fontWeight = FontWeight.SemiBold),
            titleLarge = base.titleLarge.copy(fontWeight = FontWeight.SemiBold),
            titleMedium = base.titleMedium.copy(fontWeight = FontWeight.SemiBold),
        )
    }

@Composable
fun HealthMateTheme(content: @Composable () -> Unit) {
    val dark = isSystemInDarkTheme()
    val scheme = if (dark) Dark else Light
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = !dark
            controller.isAppearanceLightNavigationBars = !dark
        }
    }
    MaterialTheme(colorScheme = scheme, shapes = HealthShapes, typography = HealthTypography, content = content)
}
