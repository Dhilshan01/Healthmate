import 'package:flutter_test/flutter_test.dart';
import 'package:healthmate/core/validators.dart';

void main() {
  group('Validators', () {
    test('rejects empty input', () {
      expect(Validators.steps(''), 'Steps is required');
    });
    test('rejects non-numeric input', () {
      expect(Validators.water('abc'), 'Water must be a whole number');
    });
    test('rejects negative values', () {
      expect(Validators.calories('-5'), 'Calories cannot be negative');
    });
    test('rejects values above the limit', () {
      expect(Validators.steps('200000'), isNotNull);
    });
    test('accepts valid input', () {
      expect(Validators.steps('7850'), isNull);
    });
  });
}
