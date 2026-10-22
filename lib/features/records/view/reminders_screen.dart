import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

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

  Future<void> _pickTime(String type, int initialMinutes) async {
    final time = await showTimePicker(
      context: context,
      initialTime: TimeOfDay(hour: initialMinutes ~/ 60, minute: initialMinutes % 60),
    );
    if (time == null) return;
    final minutes = time.hour * 60 + time.minute;
    setState(() {
      _settings = switch (type) {
        'water' => ReminderSettings(waterEnabled: _settings.waterEnabled, waterMinutes: minutes, walkingEnabled: _settings.walkingEnabled, walkingMinutes: _settings.walkingMinutes, activityEnabled: _settings.activityEnabled, activityMinutes: _settings.activityMinutes),
        'walking' => ReminderSettings(waterEnabled: _settings.waterEnabled, waterMinutes: _settings.waterMinutes, walkingEnabled: _settings.walkingEnabled, walkingMinutes: minutes, activityEnabled: _settings.activityEnabled, activityMinutes: _settings.activityMinutes),
        _ => ReminderSettings(waterEnabled: _settings.waterEnabled, waterMinutes: _settings.waterMinutes, walkingEnabled: _settings.walkingEnabled, walkingMinutes: _settings.walkingMinutes, activityEnabled: _settings.activityEnabled, activityMinutes: minutes),
      };
    });
  }

  ReminderSettings _enabled(String type, bool enabled) => switch (type) {
        'water' => ReminderSettings(waterEnabled: enabled, waterMinutes: _settings.waterMinutes, walkingEnabled: _settings.walkingEnabled, walkingMinutes: _settings.walkingMinutes, activityEnabled: _settings.activityEnabled, activityMinutes: _settings.activityMinutes),
        'walking' => ReminderSettings(waterEnabled: _settings.waterEnabled, waterMinutes: _settings.waterMinutes, walkingEnabled: enabled, walkingMinutes: _settings.walkingMinutes, activityEnabled: _settings.activityEnabled, activityMinutes: _settings.activityMinutes),
        _ => ReminderSettings(waterEnabled: _settings.waterEnabled, waterMinutes: _settings.waterMinutes, walkingEnabled: _settings.walkingEnabled, walkingMinutes: _settings.walkingMinutes, activityEnabled: enabled, activityMinutes: _settings.activityMinutes),
      };

  Future<void> _save() async {
    await ReminderService.instance.save(_settings);
    await context.read<RecordViewModel>().saveReminders(_settings);
    if (!mounted) return;
    Navigator.pop(context);
    ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('Reminders saved')));
  }

  @override
  Widget build(BuildContext context) => Scaffold(
        appBar: AppBar(title: const Text('Daily reminders')),
        body: ListView(
          padding: const EdgeInsets.all(16),
          children: [
            Text('Choose which wellness prompts you want to receive each day.', style: Theme.of(context).textTheme.bodyLarge),
            const SizedBox(height: 16),
            _reminder('water', 'Drink water', Icons.water_drop, _settings.waterEnabled, _settings.waterMinutes),
            _reminder('walking', 'Go for a walk', Icons.directions_walk, _settings.walkingEnabled, _settings.walkingMinutes),
            _reminder('activity', 'Daily activity', Icons.fitness_center, _settings.activityEnabled, _settings.activityMinutes),
            const SizedBox(height: 24),
            FilledButton.icon(onPressed: context.watch<RecordViewModel>().loading ? null : _save, icon: const Icon(Icons.notifications_active), label: const Text('Save reminders')),
          ],
        ),
      );

  Widget _reminder(String type, String title, IconData icon, bool enabled, int minutes) => Card(
        child: Column(children: [
          SwitchListTile(value: enabled, onChanged: (value) => setState(() => _settings = _enabled(type, value)), title: Text(title), secondary: Icon(icon)),
          ListTile(enabled: enabled, onTap: enabled ? () => _pickTime(type, minutes) : null, title: const Text('Reminder time'), trailing: Text(TimeOfDay(hour: minutes ~/ 60, minute: minutes % 60).format(context))),
        ]),
      );
}
