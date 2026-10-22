import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:provider/provider.dart';
import '../../../core/app_theme.dart';
import '../../../core/validators.dart';
import '../viewmodel/record_viewmodel.dart';

/// Lets people choose the daily targets used by the dashboard progress cards.
class GoalsScreen extends StatefulWidget {
  const GoalsScreen({super.key});

  @override
  State<GoalsScreen> createState() => _GoalsScreenState();
}

class _GoalsScreenState extends State<GoalsScreen> {
  final _formKey = GlobalKey<FormState>();
  late final TextEditingController _steps;
  late final TextEditingController _calories;
  late final TextEditingController _water;

  @override
  void initState() {
    super.initState();
    final goals = context.read<RecordViewModel>().goals;
    _steps = TextEditingController(text: goals.steps.toString());
    _calories = TextEditingController(text: goals.calories.toString());
    _water = TextEditingController(text: goals.water.toString());
  }

  @override
  void dispose() {
    _steps.dispose();
    _calories.dispose();
    _water.dispose();
    super.dispose();
  }

  String? _goalValidator(String? value, String label, int max) {
    final error = Validators.wholeNumber(value, label, max);
    if (error != null) return error;
    if (int.parse(value!.trim()) == 0) return '$label goal must be greater than zero';
    return null;
  }

  Future<void> _save() async {
    if (!_formKey.currentState!.validate()) return;
    await context.read<RecordViewModel>().saveGoals(DailyGoals(
          steps: int.parse(_steps.text.trim()),
          calories: int.parse(_calories.text.trim()),
          water: int.parse(_water.text.trim()),
        ));
    if (!mounted) return;
    Navigator.pop(context);
    ScaffoldMessenger.of(context).showSnackBar(
      const SnackBar(content: Text('Daily goals saved')),
    );
  }

  @override
  Widget build(BuildContext context) {
    final loading = context.watch<RecordViewModel>().loading;
    return Scaffold(
      appBar: AppBar(title: const Text('Daily goals')),
      body: Form(
        key: _formKey,
        child: ListView(
          padding: const EdgeInsets.all(16),
          children: [
            Text('Set targets that fit your routine.',
                style: Theme.of(context).textTheme.bodyLarge),
            const SizedBox(height: 20),
            _field(_steps, 'Steps goal', 'steps', Icons.directions_walk,
                AppColors.steps, (v) => _goalValidator(v, 'Steps', 100000)),
            const SizedBox(height: 16),
            _field(_calories, 'Calories goal', 'kcal',
                Icons.local_fire_department, AppColors.calories,
                (v) => _goalValidator(v, 'Calories', 20000)),
            const SizedBox(height: 16),
            _field(_water, 'Water goal', 'ml', Icons.water_drop,
                AppColors.water, (v) => _goalValidator(v, 'Water', 20000)),
            const SizedBox(height: 28),
            FilledButton.icon(
              onPressed: loading ? null : _save,
              icon: const Icon(Icons.check),
              label: const Text('Save goals'),
            ),
          ],
        ),
      ),
    );
  }

  Widget _field(
    TextEditingController controller,
    String label,
    String suffix,
    IconData icon,
    Color color,
    String? Function(String?) validator,
  ) => TextFormField(
        controller: controller,
        validator: validator,
        keyboardType: TextInputType.number,
        inputFormatters: [FilteringTextInputFormatter.digitsOnly],
        decoration: InputDecoration(
          labelText: label,
          suffixText: suffix,
          prefixIcon: Icon(icon, color: color),
          border: const OutlineInputBorder(),
        ),
      );
}
