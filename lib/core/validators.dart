/// Pure functions so they can be unit tested without Flutter widgets.
class Validators {
  /// Validates a non-negative whole number with an upper bound.
  static String? wholeNumber(String? value, String label, int max) {
    final text = value?.trim() ?? '';
    if (text.isEmpty) return '$label is required';
    final n = int.tryParse(text);
    if (n == null) return '$label must be a whole number';
    if (n < 0) return '$label cannot be negative';
    if (n > max) return '$label cannot exceed $max';
    return null;
  }

  static String? steps(String? v) => wholeNumber(v, 'Steps', 100000);
  static String? calories(String? v) => wholeNumber(v, 'Calories', 20000);
  static String? water(String? v) => wholeNumber(v, 'Water', 20000);
}
