import 'package:path/path.dart';
import 'package:sqflite/sqflite.dart';

/// Opens the SQLite database, creates the schema and seeds dummy data.
class DatabaseHelper {
  DatabaseHelper._();
  static final DatabaseHelper instance = DatabaseHelper._();

  static const dbName = 'healthmate.db';
  static const table = 'health_records';

  Database? _db;

  Future<Database> get database async => _db ??= await _open();

  Future<Database> _open() async {
    final path = join(await getDatabasesPath(), dbName);
    return openDatabase(path, version: 1, onCreate: _onCreate);
  }

  Future<void> _onCreate(Database db, int version) async {
    await db.execute('''
      CREATE TABLE $table (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        date TEXT NOT NULL,
        steps INTEGER NOT NULL,
        calories INTEGER NOT NULL,
        water INTEGER NOT NULL
      )
    ''');
    await _seed(db);
  }

  /// Dummy records for the last 10 days so the app is testable on first run.
  Future<void> _seed(Database db) async {
    final now = DateTime.now();
    const steps = [7850, 9120, 6400, 10850, 5300, 8200, 11200, 7000, 9600, 4800];
    const cals = [420, 510, 360, 620, 300, 450, 640, 390, 540, 280];
    const water = [2000, 2400, 1600, 2600, 1500, 2100, 2800, 1900, 2300, 1400];
    final batch = db.batch();
    for (var i = 0; i < 10; i++) {
      final d = now.subtract(Duration(days: i));
      final date =
          '${d.year}-${d.month.toString().padLeft(2, '0')}-${d.day.toString().padLeft(2, '0')}';
      batch.insert(table, {
        'date': date,
        'steps': steps[i],
        'calories': cals[i],
        'water': water[i],
      });
    }
    await batch.commit(noResult: true);
  }
}
