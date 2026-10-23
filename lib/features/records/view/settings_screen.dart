import 'package:flutter/material.dart';

import '../../../core/app_theme.dart';
import 'achievements_screen.dart';
import 'bmi_calculator_screen.dart';
import 'goals_screen.dart';
import 'reminders_screen.dart';

class SettingsScreen extends StatelessWidget {
  const SettingsScreen({super.key});

  @override
  Widget build(BuildContext context) => Scaffold(
        appBar: AppBar(title: const Text('Settings')),
        body: ListView(
          padding: const EdgeInsets.all(20),
          children: [
            Text('Make HealthMate fit your routine.',
                style: Theme.of(context).textTheme.titleMedium?.copyWith(
                    color: const Color(0xFF59658C), fontWeight: FontWeight.w600)),
            const SizedBox(height: 20),
            _SettingTile(
              icon: Icons.flag_outlined,
              color: AppColors.steps,
              title: 'Daily goals',
              subtitle: 'Steps, calories, and water targets',
              onTap: () => Navigator.push(context,
                  MaterialPageRoute(builder: (_) => const GoalsScreen())),
            ),
            const SizedBox(height: 12),
            _SettingTile(
              icon: Icons.emoji_events_outlined,
              color: const Color(0xFFE18A00),
              title: 'Achievements',
              subtitle: 'Badges, streaks, and fitness milestones',
              onTap: () => Navigator.push(context,
                  MaterialPageRoute(builder: (_) => const AchievementsScreen())),
            ),
            const SizedBox(height: 12),
            _SettingTile(
              icon: Icons.calculate_outlined,
              color: AppColors.seed,
              title: 'BMI calculator',
              subtitle: 'Calculate BMI from height and weight',
              onTap: () => Navigator.push(context,
                  MaterialPageRoute(builder: (_) => const BmiCalculatorScreen())),
            ),
            const SizedBox(height: 12),
            _SettingTile(
              icon: Icons.notifications_outlined,
              color: AppColors.seed,
              title: 'Reminders',
              subtitle: 'Water, walking, and activity prompts',
              onTap: () => Navigator.push(context,
                  MaterialPageRoute(builder: (_) => const RemindersScreen())),
            ),
          ],
        ),
      );
}

class _SettingTile extends StatelessWidget {
  final IconData icon;
  final Color color;
  final String title;
  final String subtitle;
  final VoidCallback onTap;
  const _SettingTile({required this.icon, required this.color, required this.title, required this.subtitle, required this.onTap});

  @override
  Widget build(BuildContext context) => Card(
        child: ListTile(
          contentPadding: const EdgeInsets.symmetric(horizontal: 18, vertical: 10),
          leading: CircleAvatar(backgroundColor: color.withValues(alpha: .14), child: Icon(icon, color: color)),
          title: Text(title, style: const TextStyle(color: AppColors.ink, fontWeight: FontWeight.w800)),
          subtitle: Text(subtitle),
          trailing: const Icon(Icons.chevron_right_rounded),
          onTap: onTap,
        ),
      );
}
