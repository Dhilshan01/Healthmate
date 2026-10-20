import 'package:flutter/material.dart';

/// Semantic colours used across the app (water = blue, calories = red ...).
class AppColors {
  static const steps = Color(0xFF2E9E5B);
  static const calories = Color(0xFFE5484D);
  static const water = Color(0xFF1E88E5);
  static const seed = Color(0xFF00897B);
}

class AppTheme {
  static ThemeData _build(Brightness b) => ThemeData(
        useMaterial3: true,
        colorScheme:
            ColorScheme.fromSeed(seedColor: AppColors.seed, brightness: b),
        inputDecorationTheme: const InputDecorationTheme(
          border: OutlineInputBorder(),
        ),
        cardTheme: const CardThemeData(margin: EdgeInsets.zero),
      );

  static ThemeData get light => _build(Brightness.light);
  static ThemeData get dark => _build(Brightness.dark);
}
