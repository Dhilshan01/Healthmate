import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../../../core/validators.dart';
import '../model/health_record.dart';
import '../viewmodel/record_viewmodel.dart';

/// Used for both adding a new record and editing an existing one.
class RecordFormScreen extends StatefulWidget {
  final HealthRecord? record;
  const RecordFormScreen({super.key, this.record});

  @override
  State<RecordFormScreen> createState() => _RecordFormScreenState();
}

class _RecordFormScreenState extends State<RecordFormScreen> {
  final _formKey = GlobalKey<FormState>();
  late DateTime _date;
  late final TextEditingController _steps;
  late final TextEditingController _calories;
  late final TextEditingController _water;

  bool get _editing => widget.record != null;

  @override
  void initState() {
    super.initState();
    final r = widget.record;
    _date = r != null ? DateTime.parse(r.date) : DateTime.now();
    _steps = TextEditingController(text: r?.steps.toString() ?? '');
    _calories = TextEditingController(text: r?.calories.toString() ?? '');
    _water = TextEditingController(text: r?.water.toString() ?? '');
  }

  @override
  void dispose() {
    _steps.dispose();
    _calories.dispose();
    _water.dispose();
    super.dispose();
  }

  Future<void> _pickDate() async {
    final picked = await showDatePicker(
      context: context,
      initialDate: _date,
      firstDate: DateTime(2020),
      lastDate: DateTime.now(),
    );
    if (picked != null) setState(() => _date = picked);
  }

  Future<void> _save() async {
    if (!_formKey.currentState!.validate()) return;
    final vm = context.read<RecordViewModel>();
    final record = HealthRecord(
      id: widget.record?.id,
      date: RecordViewModel.format(_date),
      steps: int.parse(_steps.text.trim()),
      calories: int.parse(_calories.text.trim()),
      water: int.parse(_water.text.trim()),
    );
    final messenger = ScaffoldMessenger.of(context);
    final navigator = Navigator.of(context);
    if (_editing) {
      await vm.updateRecord(record);
    } else {
      await vm.addRecord(record);
    }
    messenger.showSnackBar(SnackBar(
        content: Text(_editing ? 'Record updated' : 'Record added')));
    navigator.pop();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text(_editing ? 'Edit record' : 'Add record')),
      body: Form(
        key: _formKey,
        child: ListView(
          padding: const EdgeInsets.all(16),
          children: [
            ListTile(
              contentPadding: EdgeInsets.zero,
              leading: const Icon(Icons.event),
              title: const Text('Date'),
              subtitle: Text(RecordViewModel.format(_date)),
              trailing: TextButton(
                  onPressed: _pickDate, child: const Text('Change')),
            ),
            const SizedBox(height: 8),
            _field(_steps, 'Steps', Icons.directions_walk, Validators.steps),
            const SizedBox(height: 16),
            _field(_calories, 'Calories burned (kcal)',
                Icons.local_fire_department, Validators.calories),
            const SizedBox(height: 16),
            _field(_water, 'Water intake (ml)', Icons.water_drop,
                Validators.water),
            const SizedBox(height: 24),
            FilledButton.icon(
              onPressed: _save,
              icon: const Icon(Icons.save),
              label: Text(_editing ? 'Update' : 'Save'),
            ),
          ],
        ),
      ),
    );
  }

  Widget _field(TextEditingController c, String label, IconData icon,
          String? Function(String?) validator) =>
      TextFormField(
        controller: c,
        keyboardType: TextInputType.number,
        decoration: InputDecoration(labelText: label, prefixIcon: Icon(icon)),
        validator: validator,
      );
}
