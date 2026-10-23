import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import '../../../core/app_theme.dart';

/// Calculates body mass index (BMI) from a person's metric height and weight.
class BmiCalculatorScreen extends StatefulWidget {
  const BmiCalculatorScreen({super.key});

  @override
  State<BmiCalculatorScreen> createState() => _BmiCalculatorScreenState();
}

class _BmiCalculatorScreenState extends State<BmiCalculatorScreen> {
  final _formKey = GlobalKey<FormState>();
  final _heightController = TextEditingController();
  final _weightController = TextEditingController();
  double? _bmi;

  @override
  void dispose() {
    _heightController.dispose();
    _weightController.dispose();
    super.dispose();
  }

  void _calculate() {
    if (!_formKey.currentState!.validate()) return;

    final heightInMetres = double.parse(_heightController.text.trim()) / 100;
    final weight = double.parse(_weightController.text.trim());
    FocusScope.of(context).unfocus();
    setState(() => _bmi = weight / (heightInMetres * heightInMetres));
  }

  String? _heightValidator(String? value) {
    final height = double.tryParse(value?.trim() ?? '');
    if (height == null || height <= 0) return 'Enter a valid height';
    if (height < 50 || height > 300) return 'Height must be between 50 and 300 cm';
    return null;
  }

  String? _weightValidator(String? value) {
    final weight = double.tryParse(value?.trim() ?? '');
    if (weight == null || weight <= 0) return 'Enter a valid weight';
    if (weight < 10 || weight > 500) return 'Weight must be between 10 and 500 kg';
    return null;
  }

  _BmiResult _resultFor(double bmi) {
    if (bmi < 18.5) {
      return const _BmiResult('Underweight', 'A BMI below 18.5 is considered underweight.', Color(0xFF2374AB), Icons.arrow_downward_rounded);
    }
    if (bmi < 25) {
      return const _BmiResult('Healthy range', 'A BMI from 18.5 to 24.9 is considered a healthy range.', AppColors.steps, Icons.favorite_rounded);
    }
    if (bmi < 30) {
      return const _BmiResult('Overweight', 'A BMI from 25 to 29.9 is considered overweight.', Color(0xFFE18A00), Icons.arrow_upward_rounded);
    }
    return const _BmiResult('Obesity range', 'A BMI of 30 or above is in the obesity range.', AppColors.calories, Icons.warning_amber_rounded);
  }

  @override
  Widget build(BuildContext context) {
    final result = _bmi == null ? null : _resultFor(_bmi!);
    return Scaffold(
      appBar: AppBar(title: const Text('BMI calculator')),
      body: Form(
        key: _formKey,
        child: ListView(
          padding: const EdgeInsets.fromLTRB(20, 12, 20, 32),
          children: [
            Text('Check your body mass index', style: Theme.of(context).textTheme.headlineSmall?.copyWith(color: AppColors.ink, fontWeight: FontWeight.w800)),
            const SizedBox(height: 8),
            Text('Enter your height and weight to calculate your BMI.', style: Theme.of(context).textTheme.bodyLarge?.copyWith(color: const Color(0xFF59658C))),
            const SizedBox(height: 28),
            _numberField(controller: _heightController, label: 'Height', unit: 'cm', icon: Icons.height_rounded, validator: _heightValidator),
            const SizedBox(height: 16),
            _numberField(controller: _weightController, label: 'Weight', unit: 'kg', icon: Icons.monitor_weight_outlined, validator: _weightValidator),
            const SizedBox(height: 24),
            FilledButton.icon(onPressed: _calculate, icon: const Icon(Icons.calculate_outlined), label: const Text('Calculate BMI')),
            if (result != null) ...[
              const SizedBox(height: 28),
              _ResultCard(bmi: _bmi!, result: result),
            ],
            const SizedBox(height: 20),
            Text('BMI is a screening measure and does not diagnose body fat or health on its own.', style: Theme.of(context).textTheme.bodySmall?.copyWith(color: const Color(0xFF59658C))),
          ],
        ),
      ),
    );
  }

  Widget _numberField({required TextEditingController controller, required String label, required String unit, required IconData icon, required String? Function(String?) validator}) => TextFormField(
        controller: controller,
        validator: validator,
        keyboardType: const TextInputType.numberWithOptions(decimal: true),
        inputFormatters: [FilteringTextInputFormatter.allow(RegExp(r'^\d*\.?\d{0,2}'))],
        decoration: InputDecoration(labelText: label, prefixIcon: Icon(icon), suffixText: unit),
      );
}

class _BmiResult {
  final String label;
  final String detail;
  final Color color;
  final IconData icon;
  const _BmiResult(this.label, this.detail, this.color, this.icon);
}

class _ResultCard extends StatelessWidget {
  final double bmi;
  final _BmiResult result;
  const _ResultCard({required this.bmi, required this.result});

  @override
  Widget build(BuildContext context) => Card(
        color: result.color.withValues(alpha: .11),
        child: Padding(
          padding: const EdgeInsets.all(22),
          child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
            Row(children: [
              Icon(result.icon, color: result.color),
              const SizedBox(width: 8),
              Text(result.label, style: Theme.of(context).textTheme.titleMedium?.copyWith(color: result.color, fontWeight: FontWeight.w800)),
            ]),
            const SizedBox(height: 18),
            Text(bmi.toStringAsFixed(1), style: Theme.of(context).textTheme.displayMedium?.copyWith(color: AppColors.ink, fontWeight: FontWeight.w800)),
            Text('Your BMI', style: Theme.of(context).textTheme.titleSmall?.copyWith(color: const Color(0xFF59658C))),
            const SizedBox(height: 12),
            Text(result.detail, style: Theme.of(context).textTheme.bodyMedium?.copyWith(color: AppColors.ink)),
          ]),
        ),
      );
}
