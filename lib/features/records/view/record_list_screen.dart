import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:provider/provider.dart';
import '../../../core/app_theme.dart';
import '../model/health_record.dart';
import '../viewmodel/record_viewmodel.dart';
import 'record_form_screen.dart';

class RecordListScreen extends StatelessWidget {
  const RecordListScreen({super.key});

  Future<void> _pickDate(BuildContext context, RecordViewModel vm) async {
    final picked = await showDatePicker(
      context: context,
      initialDate: vm.filterDate ?? DateTime.now(),
      firstDate: DateTime(2020),
      lastDate: DateTime.now(),
    );
    if (picked != null) vm.setFilterDate(picked);
  }

  Future<void> _confirmDelete(
      BuildContext context, RecordViewModel vm, HealthRecord r) async {
    final ok = await showDialog<bool>(
      context: context,
      builder: (ctx) => AlertDialog(
        title: const Text('Delete record?'),
        content: Text('The record for ${r.date} will be permanently removed.'),
        actions: [
          TextButton(
              onPressed: () => Navigator.pop(ctx, false),
              child: const Text('Cancel')),
          FilledButton(
              onPressed: () => Navigator.pop(ctx, true),
              child: const Text('Delete')),
        ],
      ),
    );
    if (ok == true && r.id != null) await vm.deleteRecord(r.id!);
  }

  @override
  Widget build(BuildContext context) {
    final vm = context.watch<RecordViewModel>();
    final records = vm.records;
    final nf = NumberFormat.decimalPattern();

    return Scaffold(
      appBar: AppBar(
        title: const Text('History'),
        actions: [
          IconButton(
            tooltip: 'Search by date',
            icon: const Icon(Icons.search),
            onPressed: () => _pickDate(context, vm),
          ),
        ],
      ),
      body: Column(
        children: [
          if (vm.filterDate != null)
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 16),
              child: Align(
                alignment: Alignment.centerLeft,
                child: InputChip(
                  avatar: const Icon(Icons.event, size: 18),
                  label: Text(DateFormat('d MMM yyyy').format(vm.filterDate!)),
                  onDeleted: () => vm.setFilterDate(null),
                ),
              ),
            ),
          Expanded(
            child: records.isEmpty
                ? const Center(child: Text('No records found'))
                : ListView.separated(
                    padding: const EdgeInsets.fromLTRB(16, 8, 16, 96),
                    itemCount: records.length,
                    separatorBuilder: (_, __) => const SizedBox(height: 8),
                    itemBuilder: (context, i) {
                      final r = records[i];
                      return Card(
                        child: ListTile(
                          title: Text(DateFormat('EEE, d MMM yyyy')
                              .format(DateTime.parse(r.date))),
                          subtitle: Padding(
                            padding: const EdgeInsets.only(top: 6),
                            child: Wrap(spacing: 12, children: [
                              _chip(Icons.directions_walk, AppColors.steps,
                                  nf.format(r.steps)),
                              _chip(Icons.local_fire_department,
                                  AppColors.calories, '${r.calories} kcal'),
                              _chip(Icons.water_drop, AppColors.water,
                                  '${nf.format(r.water)} ml'),
                            ]),
                          ),
                          onTap: () => Navigator.push(
                            context,
                            MaterialPageRoute(
                                builder: (_) => RecordFormScreen(record: r)),
                          ),
                          trailing: IconButton(
                            tooltip: 'Delete',
                            icon: const Icon(Icons.delete_outline),
                            onPressed: () => _confirmDelete(context, vm, r),
                          ),
                        ),
                      );
                    },
                  ),
          ),
        ],
      ),
    );
  }

  Widget _chip(IconData icon, Color color, String text) => Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          Icon(icon, size: 16, color: color),
          const SizedBox(width: 4),
          Text(text),
        ],
      );
}
