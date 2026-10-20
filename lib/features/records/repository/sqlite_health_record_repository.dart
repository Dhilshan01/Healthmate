import '../../../core/database/database_helper.dart';
import '../model/health_record.dart';
import 'health_record_repository.dart';

class SqliteHealthRecordRepository implements HealthRecordRepository {
  final DatabaseHelper _helper;
  SqliteHealthRecordRepository([DatabaseHelper? helper])
      : _helper = helper ?? DatabaseHelper.instance;

  @override
  Future<int> insert(HealthRecord r) async {
    final db = await _helper.database;
    return db.insert(DatabaseHelper.table, r.toMap());
  }

  @override
  Future<List<HealthRecord>> getAll() async {
    final db = await _helper.database;
    final rows = await db.query(DatabaseHelper.table,
        orderBy: 'date DESC, id DESC');
    return rows.map(HealthRecord.fromMap).toList();
  }

  @override
  Future<List<HealthRecord>> getByDate(String date) async {
    final db = await _helper.database;
    final rows = await db.query(DatabaseHelper.table,
        where: 'date = ?', whereArgs: [date], orderBy: 'id DESC');
    return rows.map(HealthRecord.fromMap).toList();
  }

  @override
  Future<int> update(HealthRecord r) async {
    final db = await _helper.database;
    return db.update(DatabaseHelper.table, r.toMap(),
        where: 'id = ?', whereArgs: [r.id]);
  }

  @override
  Future<int> delete(int id) async {
    final db = await _helper.database;
    return db.delete(DatabaseHelper.table, where: 'id = ?', whereArgs: [id]);
  }
}
