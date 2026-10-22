import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:provider/provider.dart';
import '../../../core/app_theme.dart';
import '../viewmodel/record_viewmodel.dart';
import '../widgets/metric_card.dart';
import 'goals_screen.dart';
import 'reminders_screen.dart';

class DashboardScreen extends StatelessWidget {
  const DashboardScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final vm = context.watch<RecordViewModel>();
    final t = vm.today;
    final nf = NumberFormat.decimalPattern();

    return Scaffold(
      appBar: AppBar(
        title: const Text('HealthMate'),
        actions: [
          IconButton(
            tooltip: 'Daily reminders',
            icon: const Icon(Icons.notifications_outlined),
            onPressed: () => Navigator.push(
              context,
              MaterialPageRoute(builder: (_) => const RemindersScreen()),
            ),
          ),
          IconButton(
            tooltip: 'Daily goals',
            icon: const Icon(Icons.flag_outlined),
            onPressed: () => Navigator.push(
              context,
              MaterialPageRoute(builder: (_) => const GoalsScreen()),
            ),
          ),
        ],
      ),
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
                  goal: '${nf.format(vm.goals.steps)} goal',
                  progress: t.steps / vm.goals.steps,
                ),
                const SizedBox(height: 12),
                MetricCard(
                  icon: Icons.local_fire_department,
                  color: AppColors.calories,
                  label: 'Calories burned',
                  value: '${nf.format(t.calories)} kcal',
                  goal: '${nf.format(vm.goals.calories)} kcal goal',
                  progress: t.calories / vm.goals.calories,
                ),
                const SizedBox(height: 12),
                MetricCard(
                  icon: Icons.water_drop,
                  color: AppColors.water,
                  label: 'Water intake',
                  value: '${nf.format(t.water)} ml',
                  goal: '${nf.format(vm.goals.water)} ml goal',
                  progress: t.water / vm.goals.water,
                ),
                const SizedBox(height: 24),
                Text('Last 7 days – steps',
                    style: Theme.of(context).textTheme.titleMedium),
                const SizedBox(height: 12),
                _WeeklyChart(
                  days: vm.lastSevenDays,
                  stepsGoal: vm.goals.steps,
                ),
              ],
            ),
    );
  }
}

/// Simple bar chart built from containers (no extra chart dependency).
class _WeeklyChart extends StatelessWidget {
  final List<MapEntry<DateTime, DayTotals>> days;
  final int stepsGoal;
  const _WeeklyChart({required this.days, required this.stepsGoal});

  @override
  Widget build(BuildContext context) {
    final maxSteps = days
        .map((e) => e.value.steps)
        .fold<int>(stepsGoal, (a, b) => b > a ? b : a);
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
