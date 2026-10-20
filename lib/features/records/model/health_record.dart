class HealthRecord {
  final int? id;
  final String date; // stored as yyyy-MM-dd
  final int steps;
  final int calories;
  final int water; // ml

  const HealthRecord({
    this.id,
    required this.date,
    required this.steps,
    required this.calories,
    required this.water,
  });

  Map<String, Object?> toMap() => {
        if (id != null) 'id': id,
        'date': date,
        'steps': steps,
        'calories': calories,
        'water': water,
      };

  factory HealthRecord.fromMap(Map<String, Object?> m) => HealthRecord(
        id: m['id'] as int?,
        date: m['date'] as String,
        steps: m['steps'] as int,
        calories: m['calories'] as int,
        water: m['water'] as int,
      );

  HealthRecord copyWith({
    int? id,
    String? date,
    int? steps,
    int? calories,
    int? water,
  }) =>
      HealthRecord(
        id: id ?? this.id,
        date: date ?? this.date,
        steps: steps ?? this.steps,
        calories: calories ?? this.calories,
        water: water ?? this.water,
      );
}
