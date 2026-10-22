import 'package:flutter/material.dart';

/// Semantic colours used across the app (water = blue, calories = red ...).
class AppColors {
  static const ink = Color(0xFF1D274B);
  static const canvas = Color(0xFFF1F4FF);
  static const steps = Color(0xFF167348);
  static const calories = Color(0xFFB3262C);
  static const water = Color(0xFF1855A9);
  static const seed = Color(0xFF7080F5);
  static const waterTile = Color(0xFFD5E7FF);
  static const stepsTile = Color(0xFFD8F4E2);
  static const caloriesTile = Color(0xFFFFDDE0);
}

class AppTheme {
  static ThemeData _build(Brightness b) => ThemeData(
        useMaterial3: true,
        colorScheme: ColorScheme.fromSeed(seedColor: AppColors.seed, brightness: b),
        scaffoldBackgroundColor: b == Brightness.light ? AppColors.canvas : null,
        appBarTheme: const AppBarTheme(backgroundColor: Colors.transparent, foregroundColor: AppColors.ink, elevation: 0),
        inputDecorationTheme: InputDecorationTheme(
          filled: true, fillColor: Colors.white,
          border: OutlineInputBorder(borderRadius: BorderRadius.circular(18), borderSide: BorderSide.none),
          enabledBorder: OutlineInputBorder(borderRadius: BorderRadius.circular(18), borderSide: BorderSide.none),
        ),
        cardTheme: CardThemeData(margin: EdgeInsets.zero, color: Colors.white, elevation: 0, shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(26))),
      );

  static ThemeData get light => _build(Brightness.light);
  static ThemeData get dark => _build(Brightness.dark);
}
