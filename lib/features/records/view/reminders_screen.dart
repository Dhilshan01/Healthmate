import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../../../core/app_theme.dart';
import '../../../core/notifications/reminder_service.dart';
import '../viewmodel/record_viewmodel.dart';

class RemindersScreen extends StatefulWidget {
  const RemindersScreen({super.key});

  @override
  State<RemindersScreen> createState() => _RemindersScreenState();
}

class _RemindersScreenState extends State<RemindersScreen> {
  late ReminderSettings _settings;

  @override
  void initState() {
    super.initState();
    _settings = context.read<RecordViewModel>().reminders;
  }

  Future<void> _pickTime(String type, int minutes) async {
    final time = await showTimePicker(context: context, initialTime: TimeOfDay(hour: minutes ~/ 60, minute: minutes % 60));
    if (time == null) return;
    _update(type, minutes: time.hour * 60 + time.minute);
  }

  void _update(String type, {bool? enabled, int? minutes}) => setState(() {
        final isWater = type == 'water';
        final isWalking = type == 'walking';
        _settings = ReminderSettings(
          waterEnabled: isWater ? enabled ?? _settings.waterEnabled : _settings.waterEnabled,
          waterMinutes: isWater ? minutes ?? _settings.waterMinutes : _settings.waterMinutes,
          walkingEnabled: isWalking ? enabled ?? _settings.walkingEnabled : _settings.walkingEnabled,
          walkingMinutes: isWalking ? minutes ?? _settings.walkingMinutes : _settings.walkingMinutes,
          activityEnabled: !isWater && !isWalking ? enabled ?? _settings.activityEnabled : _settings.activityEnabled,
          activityMinutes: !isWater && !isWalking ? minutes ?? _settings.activityMinutes : _settings.activityMinutes,
        );
      });

  Future<void> _save() async {
    await ReminderService.instance.save(_settings);
    await context.read<RecordViewModel>().saveReminders(_settings);
    if (!mounted) return;
    ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('Daily reminders saved')));
    Navigator.pop(context);
  }

  @override
  Widget build(BuildContext context) {
    final loading = context.watch<RecordViewModel>().loading;
    return Scaffold(
      appBar: AppBar(title: const Text('Daily reminders')),
      body: ListView(
        padding: const EdgeInsets.fromLTRB(20, 8, 20, 32),
        children: [
          Text('Gentle prompts for your healthy habits.', style: Theme.of(context).textTheme.titleMedium?.copyWith(color: const Color(0xFF59658C), fontWeight: FontWeight.w600)),
          const SizedBox(height: 22),
          _ReminderCard(type: 'water', title: 'Drink water', subtitle: 'Stay hydrated throughout the day', icon: Icons.water_drop_outlined, background: AppColors.waterTile, color: AppColors.water, enabled: _settings.waterEnabled, minutes: _settings.waterMinutes, onEnabled: (value) => _update('water', enabled: value), onTime: () => _pickTime('water', _settings.waterMinutes)),
          const SizedBox(height: 12),
          _ReminderCard(type: 'walking', title: 'Go for a walk', subtitle: 'Take a short movement break', icon: Icons.directions_walk_rounded, background: AppColors.stepsTile, color: AppColors.steps, enabled: _settings.walkingEnabled, minutes: _settings.walkingMinutes, onEnabled: (value) => _update('walking', enabled: value), onTime: () => _pickTime('walking', _settings.walkingMinutes)),
          const SizedBox(height: 12),
          _ReminderCard(type: 'activity', title: 'Daily activity', subtitle: 'Make time to move your body', icon: Icons.fitness_center_rounded, background: AppColors.caloriesTile, color: AppColors.calories, enabled: _settings.activityEnabled, minutes: _settings.activityMinutes, onEnabled: (value) => _update('activity', enabled: value), onTime: () => _pickTime('activity', _settings.activityMinutes)),
          const SizedBox(height: 26),
          FilledButton.icon(
            style: FilledButton.styleFrom(backgroundColor: AppColors.ink, foregroundColor: Colors.white, minimumSize: const Size.fromHeight(56), shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(18))),
            onPressed: loading ? null : _save,
            icon: const Icon(Icons.notifications_active_outlined),
            label: const Text('Save reminders', style: TextStyle(fontWeight: FontWeight.w800)),
          ),
        ],
      ),
    );
  }
}

class _ReminderCard extends StatelessWidget {
  final String type, title, subtitle;
  final IconData icon;
  final Color background, color;
  final bool enabled;
  final int minutes;
  final ValueChanged<bool> onEnabled;
  final VoidCallback onTime;
  const _ReminderCard({required this.type, required this.title, required this.subtitle, required this.icon, required this.background, required this.color, required this.enabled, required this.minutes, required this.onEnabled, required this.onTime});

  @override
  Widget build(BuildContext context) {
    final time = TimeOfDay(hour: minutes ~/ 60, minute: minutes % 60).format(context);
    return AnimatedOpacity(
      duration: const Duration(milliseconds: 220),
      opacity: enabled ? 1 : .62,
      child: Container(
        padding: const EdgeInsets.all(18),
        decoration: BoxDecoration(color: background, borderRadius: BorderRadius.circular(28)),
        child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
          Row(children: [
            CircleAvatar(backgroundColor: Colors.white.withValues(alpha: .72), child: Icon(icon, color: color)),
            const SizedBox(width: 12),
            Expanded(child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [Text(title, style: TextStyle(color: color, fontSize: 19, fontWeight: FontWeight.w800)), Text(subtitle, style: TextStyle(color: AppColors.ink.withValues(alpha: .68), fontSize: 13))])),
            Switch(value: enabled, onChanged: onEnabled, activeThumbColor: color),
          ]),
          const SizedBox(height: 15),
          InkWell(
            onTap: enabled ? onTime : null,
            borderRadius: BorderRadius.circular(16),
            child: Ink(
              padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 12),
              decoration: BoxDecoration(color: Colors.white.withValues(alpha: .72), borderRadius: BorderRadius.circular(16)),
              child: Row(children: [Icon(Icons.schedule_rounded, color: color, size: 20), const SizedBox(width: 8), const Text('Daily at', style: TextStyle(fontWeight: FontWeight.w700, color: AppColors.ink)), const Spacer(), Text(time, style: TextStyle(color: color, fontWeight: FontWeight.w900)), const SizedBox(width: 4), Icon(Icons.chevron_right_rounded, color: color)]),
            ),
          ),
        ]),
      ),
    );
  }
}
