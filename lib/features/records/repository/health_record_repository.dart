import '../model/health_record.dart';

/// Abstraction over the data source (lets the ViewModel be tested with a fake).
abstract class HealthRecordRepository {
  Future<int> insert(HealthRecord record);
  Future<List<HealthRecord>> getAll();
  Future<List<HealthRecord>> getByDate(String date);
  Future<int> update(HealthRecord record);
  Future<int> delete(int id);
}
