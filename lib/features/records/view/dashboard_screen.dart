import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:provider/provider.dart';

import '../../../core/app_theme.dart';
import '../viewmodel/record_viewmodel.dart';
import '../widgets/metric_card.dart';

class DashboardScreen extends StatelessWidget {
  const DashboardScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final vm = context.watch<RecordViewModel>();
    final t = vm.today;
    final nf = NumberFormat.decimalPattern();

    return Scaffold(
      appBar: AppBar(title: const Text('HealthMate')),
      body: vm.loading && vm.records.isEmpty
          ? const Center(child: CircularProgressIndicator())
          : ListView(
              padding: const EdgeInsets.fromLTRB(16, 8, 16, 96),
              children: [
                Text(
                  'Today – ${DateFormat('d MMMM yyyy').format(DateTime.now())}',
                  style: Theme.of(context).textTheme.titleLarge,
                ),
                const SizedBox(height: 12),
                if (vm.error != null)
                  Padding(
                    padding: const EdgeInsets.only(bottom: 8),
                    child: Text(vm.error!,
                        style: TextStyle(
                            color: Theme.of(context).colorScheme.error)),
                  ),
                MetricCard(
                  icon: Icons.directions_walk,
                  color: AppColors.steps,
                  label: 'Steps',
                  value: nf.format(t.steps),
                  progress: t.steps / RecordViewModel.stepsGoal,
                ),
                const SizedBox(height: 12),
                MetricCard(
                  icon: Icons.local_fire_department,
                  color: AppColors.calories,
                  label: 'Calories burned',
                  value: '${nf.format(t.calories)} kcal',
                  progress: t.calories / RecordViewModel.caloriesGoal,
                ),
                const SizedBox(height: 12),
                MetricCard(
                  icon: Icons.water_drop,
                  color: AppColors.water,
                  label: 'Water intake',
                  value: '${nf.format(t.water)} ml',
                  progress: t.water / RecordViewModel.waterGoal,
                ),
                const SizedBox(height: 24),
                Text('Last 7 days – steps',
                    style: Theme.of(context).textTheme.titleMedium),
                const SizedBox(height: 12),
                _WeeklyChart(days: vm.lastSevenDays),
              ],
            ),
    );
  }
}

/// Simple bar chart built from containers (no extra chart dependency).
class _WeeklyChart extends StatelessWidget {
  final List<MapEntry<DateTime, DayTotals>> days;
  const _WeeklyChart({required this.days});

  @override
  Widget build(BuildContext context) {
    final maxSteps = days
        .map((e) => e.value.steps)
        .fold<int>(RecordViewModel.stepsGoal, (a, b) => b > a ? b : a);
    return SizedBox(
      height: 150,
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.end,
        children: [
          for (final d in days)
            Expanded(
              child: Padding(
                padding: const EdgeInsets.symmetric(horizontal: 4),
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.end,
                  children: [
                    Container(
                      height: 100 * d.value.steps / maxSteps,
                      decoration: BoxDecoration(
                        color: AppColors.steps,
                        borderRadius: BorderRadius.circular(4),
                      ),
                    ),
                    const SizedBox(height: 4),
                    Text(DateFormat('E').format(d.key),
                        style: Theme.of(context).textTheme.labelSmall),
                  ],
                ),
              ),
            ),
        ],
      ),
    );
  }
}
