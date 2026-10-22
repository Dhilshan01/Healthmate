import 'package:path/path.dart';
import 'package:sqflite/sqflite.dart';

/// Opens the SQLite database, creates the schema and seeds dummy data.
class DatabaseHelper {
  DatabaseHelper._();
  static final DatabaseHelper instance = DatabaseHelper._();

  static const dbName = 'healthmate.db';
  static const table = 'health_records';
  static const goalsTable = 'daily_goals';
  static const remindersTable = 'daily_reminders';

  Database? _db;

  Future<Database> get database async => _db ??= await _open();

  Future<Database> _open() async {
    final path = join(await getDatabasesPath(), dbName);
    return openDatabase(
      path,
      version: 3,
      onCreate: _onCreate,
      onUpgrade: _onUpgrade,
    );
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
    await _createGoalsTable(db);
    await _createRemindersTable(db);
    await _seed(db);
  }

  Future<void> _onUpgrade(Database db, int oldVersion, int newVersion) async {
    if (oldVersion < 2) await _createGoalsTable(db);
    if (oldVersion < 3) await _createRemindersTable(db);
  }

  Future<void> _createGoalsTable(Database db) => db.execute('''
    CREATE TABLE $goalsTable (
      id INTEGER PRIMARY KEY CHECK (id = 1),
      steps INTEGER NOT NULL,
      calories INTEGER NOT NULL,
      water INTEGER NOT NULL
    )
  ''');

  Future<void> _createRemindersTable(Database db) => db.execute('''
    CREATE TABLE $remindersTable (
      id INTEGER PRIMARY KEY CHECK (id = 1),
      waterEnabled INTEGER NOT NULL,
      waterMinutes INTEGER NOT NULL,
      walkingEnabled INTEGER NOT NULL,
      walkingMinutes INTEGER NOT NULL,
      activityEnabled INTEGER NOT NULL,
      activityMinutes INTEGER NOT NULL
    )
  ''');

  Future<Map<String, int>> getReminders() async {
    final db = await database;
    final rows = await db.query(remindersTable, where: 'id = ?', whereArgs: [1]);
    if (rows.isEmpty) {
      return const {
        'waterEnabled': 0, 'waterMinutes': 600,
        'walkingEnabled': 0, 'walkingMinutes': 1020,
        'activityEnabled': 0, 'activityMinutes': 1200,
      };
    }
    return Map<String, int>.from(rows.single);
  }

  Future<void> saveReminders(Map<String, int> values) async {
    final db = await database;
    await db.insert(remindersTable, {'id': 1, ...values},
        conflictAlgorithm: ConflictAlgorithm.replace);
  }

  Future<Map<String, int>> getGoals() async {
    final db = await database;
    final rows = await db.query(goalsTable, where: 'id = ?', whereArgs: [1]);
    if (rows.isEmpty) {
      return const {'steps': 10000, 'calories': 500, 'water': 2500};
    }
    final row = rows.single;
    return {
      'steps': row['steps'] as int,
      'calories': row['calories'] as int,
      'water': row['water'] as int,
    };
  }

  Future<void> saveGoals({
    required int steps,
    required int calories,
    required int water,
  }) async {
    final db = await database;
    await db.insert(goalsTable, {
      'id': 1,
      'steps': steps,
      'calories': calories,
      'water': water,
    }, conflictAlgorithm: ConflictAlgorithm.replace);
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
