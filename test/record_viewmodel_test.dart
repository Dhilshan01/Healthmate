import 'package:flutter_test/flutter_test.dart';
import 'package:healthmate/features/records/model/health_record.dart';
import 'package:healthmate/features/records/repository/health_record_repository.dart';
import 'package:healthmate/features/records/viewmodel/record_viewmodel.dart';

class FakeRepo implements HealthRecordRepository {
  final List<HealthRecord> _rows = [];
  int _next = 1;

  @override
  Future<int> insert(HealthRecord r) async {
    _rows.add(r.copyWith(id: _next));
    return _next++;
  }

  @override
  Future<List<HealthRecord>> getAll() async => List.of(_rows);

  @override
  Future<List<HealthRecord>> getByDate(String d) async =>
      _rows.where((r) => r.date == d).toList();

  @override
  Future<int> update(HealthRecord r) async {
    final i = _rows.indexWhere((x) => x.id == r.id);
    if (i < 0) return 0;
    _rows[i] = r;
    return 1;
  }

  @override
  Future<int> delete(int id) async {
    _rows.removeWhere((r) => r.id == id);
    return 1;
  }
}

void main() {
  late RecordViewModel vm;
  final today = RecordViewModel.format(DateTime.now());

  setUp(() => vm = RecordViewModel(FakeRepo()));

  test('addRecord stores a record and updates today totals', () async {
    await vm.addRecord(HealthRecord(
        date: today, steps: 1000, calories: 50, water: 500));
    await vm.addRecord(HealthRecord(
        date: today, steps: 2000, calories: 70, water: 700));
    expect(vm.records.length, 2);
    expect(vm.today.water, 1200);
  });

  test('updateRecord changes values', () async {
    await vm.addRecord(
        HealthRecord(date: today, steps: 1, calories: 1, water: 1));
    final saved = vm.records.first;
    await vm.updateRecord(saved.copyWith(steps: 999));
    expect(vm.records.first.steps, 999);
  });

  test('deleteRecord removes a record', () async {
    await vm.addRecord(
        HealthRecord(date: today, steps: 1, calories: 1, water: 1));
    await vm.deleteRecord(vm.records.first.id!);
    expect(vm.records, isEmpty);
  });

  test('date filter only returns matching records', () async {
    await vm.addRecord(
        HealthRecord(date: today, steps: 1, calories: 1, water: 1));
    await vm.addRecord(
        const HealthRecord(date: '2020-01-01', steps: 2, calories: 2, water: 2));
    vm.setFilterDate(DateTime(2020, 1, 1));
    expect(vm.records.length, 1);
    expect(vm.records.first.steps, 2);
  });
}
