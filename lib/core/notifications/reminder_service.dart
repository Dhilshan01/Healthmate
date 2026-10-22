import 'package:flutter_local_notifications/flutter_local_notifications.dart';
import 'package:flutter_timezone/flutter_timezone.dart';
import 'package:timezone/data/latest.dart' as tz_data;
import 'package:timezone/timezone.dart' as tz;

import '../../features/records/viewmodel/record_viewmodel.dart';

/// Schedules the three daily wellness reminders on the device.
class ReminderService {
  ReminderService._();
  static final instance = ReminderService._();

  final FlutterLocalNotificationsPlugin _notifications =
      FlutterLocalNotificationsPlugin();

  static const _details = NotificationDetails(
    android: AndroidNotificationDetails(
      'wellness_reminders',
      'Wellness reminders',
      channelDescription: 'Reminders for water, walking, and daily activity',
      importance: Importance.defaultImportance,
      priority: Priority.defaultPriority,
    ),
  );

  Future<void> initialize() async {
    tz_data.initializeTimeZones();
    final deviceTimezone = await FlutterTimezone.getLocalTimezone();
    tz.setLocalLocation(tz.getLocation(deviceTimezone.identifier));
    await _notifications.initialize(
      settings: const InitializationSettings(
        android: AndroidInitializationSettings('@mipmap/ic_launcher'),
      ),
    );
  }

  Future<void> save(ReminderSettings settings) async {
    final android = _notifications
        .resolvePlatformSpecificImplementation<
            AndroidFlutterLocalNotificationsPlugin>();
    await android?.requestNotificationsPermission();
    await _notifications.cancel(id: 101);
    await _notifications.cancel(id: 102);
    await _notifications.cancel(id: 103);

    if (settings.waterEnabled) {
      await _schedule(101, settings.waterMinutes, 'Drink some water',
          'A little hydration goes a long way.');
    }
    if (settings.walkingEnabled) {
      await _schedule(102, settings.walkingMinutes, 'Time for a walk',
          'Take a short walk and add to your step count.');
    }
    if (settings.activityEnabled) {
      await _schedule(103, settings.activityMinutes, 'Move your body',
          'Make time for your daily activity.');
    }
  }

  Future<void> _schedule(int id, int minutes, String title, String body) async {
    final now = DateTime.now();
    var scheduled = DateTime(now.year, now.month, now.day, minutes ~/ 60,
        minutes % 60);
    if (!scheduled.isAfter(now)) scheduled = scheduled.add(const Duration(days: 1));
    await _notifications.zonedSchedule(
      id: id,
      title: title,
      body: body,
      scheduledDate: tz.TZDateTime.from(scheduled, tz.local),
      notificationDetails: _details,
      androidScheduleMode: AndroidScheduleMode.inexactAllowWhileIdle,
      matchDateTimeComponents: DateTimeComponents.time,
    );
  }
}
