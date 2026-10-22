import 'package:flutter/foundation.dart';
import 'package:intl/intl.dart';
import '../../../core/database/database_helper.dart';
import '../model/health_record.dart';
import '../repository/health_record_repository.dart';

class DayTotals {
  final int steps;
  final int calories;
  final int water;
  const DayTotals(this.steps, this.calories, this.water);
}

class DailyGoals {
  final int steps;
  final int calories;
  final int water;

  const DailyGoals({
    this.steps = 10000,
    this.calories = 500,
    this.water = 2500,
  });
}

class ReminderSettings {
  final bool waterEnabled;
  final int waterMinutes;
  final bool walkingEnabled;
  final int walkingMinutes;
  final bool activityEnabled;
  final int activityMinutes;

  const ReminderSettings({
    this.waterEnabled = false,
    this.waterMinutes = 600,
    this.walkingEnabled = false,
    this.walkingMinutes = 1020,
    this.activityEnabled = false,
    this.activityMinutes = 1200,
  });

  Map<String, int> toMap() => {
        'waterEnabled': waterEnabled ? 1 : 0,
        'waterMinutes': waterMinutes,
        'walkingEnabled': walkingEnabled ? 1 : 0,
        'walkingMinutes': walkingMinutes,
        'activityEnabled': activityEnabled ? 1 : 0,
        'activityMinutes': activityMinutes,
      };
}

/// Holds UI state and talks to the repository (MVVM + Provider).
class RecordViewModel extends ChangeNotifier {
  final HealthRecordRepository _repo;
  RecordViewModel(this._repo);

  List<HealthRecord> _records = [];
  DateTime? _filterDate;
  bool _loading = false;
  String? _error;
  DailyGoals _goals = const DailyGoals();
  ReminderSettings _reminders = const ReminderSettings();

  bool get loading => _loading;
  String? get error => _error;
  DateTime? get filterDate => _filterDate;
  DailyGoals get goals => _goals;
  ReminderSettings get reminders => _reminders;

  static String format(DateTime d) => DateFormat('yyyy-MM-dd').format(d);

  /// Records after the optional date filter is applied.
  List<HealthRecord> get records => _filterDate == null
      ? List.unmodifiable(_records)
      : _records.where((r) => r.date == format(_filterDate!)).toList();

  List<HealthRecord> get _todayRecords =>
      _records.where((r) => r.date == format(DateTime.now())).toList();

  DayTotals get today => _sum(_todayRecords);

  /// Totals for the last 7 days, oldest first (used by the weekly chart).
  List<MapEntry<DateTime, DayTotals>> get lastSevenDays {
    final now = DateTime.now();
    return List.generate(7, (i) {
      final d = DateTime(now.year, now.month, now.day)
          .subtract(Duration(days: 6 - i));
      final day = _records.where((r) => r.date == format(d)).toList();
      return MapEntry(d, _sum(day));
    });
  }

  DayTotals _sum(List<HealthRecord> list) => DayTotals(
        list.fold(0, (s, r) => s + r.steps),
        list.fold(0, (s, r) => s + r.calories),
        list.fold(0, (s, r) => s + r.water),
      );

  Future<void> loadRecords() => _run(() async {
        _records = await _repo.getAll();
        final savedGoals = await DatabaseHelper.instance.getGoals();
        _goals = DailyGoals(
          steps: savedGoals['steps']!,
          calories: savedGoals['calories']!,
          water: savedGoals['water']!,
        );
        final savedReminders = await DatabaseHelper.instance.getReminders();
        _reminders = ReminderSettings(
          waterEnabled: savedReminders['waterEnabled'] == 1,
          waterMinutes: savedReminders['waterMinutes']!,
          walkingEnabled: savedReminders['walkingEnabled'] == 1,
          walkingMinutes: savedReminders['walkingMinutes']!,
          activityEnabled: savedReminders['activityEnabled'] == 1,
          activityMinutes: savedReminders['activityMinutes']!,
        );
      });

  Future<void> saveGoals(DailyGoals goals) => _run(() async {
        await DatabaseHelper.instance.saveGoals(
          steps: goals.steps,
          calories: goals.calories,
          water: goals.water,
        );
        _goals = goals;
      });

  Future<void> saveReminders(ReminderSettings reminders) => _run(() async {
        await DatabaseHelper.instance.saveReminders(reminders.toMap());
        _reminders = reminders;
      });

  Future<void> addRecord(HealthRecord r) => _run(() async {
        await _repo.insert(r);
        _records = await _repo.getAll();
      });

  Future<void> updateRecord(HealthRecord r) => _run(() async {
        await _repo.update(r);
        _records = await _repo.getAll();
      });

  Future<void> deleteRecord(int id) => _run(() async {
        await _repo.delete(id);
        _records = await _repo.getAll();
      });

  void setFilterDate(DateTime? date) {
    _filterDate = date;
    notifyListeners();
  }

  Future<void> _run(Future<void> Function() action) async {
    _loading = true;
    _error = null;
    notifyListeners();
    try {
      await action();
    } catch (e) {
      _error = 'Something went wrong: $e';
    }
    _loading = false;
    notifyListeners();
  }
}
