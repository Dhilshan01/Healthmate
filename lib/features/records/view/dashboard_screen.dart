import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:provider/provider.dart';

import '../../../core/app_theme.dart';
import '../viewmodel/record_viewmodel.dart';
import '../widgets/metric_card.dart';
import 'goals_screen.dart';
import 'reminders_screen.dart';

/// The original dashboard layout, with the refreshed greeting header retained.
class DashboardScreen extends StatelessWidget {
  const DashboardScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final vm = context.watch<RecordViewModel>();
    final totals = vm.today;
    final numbers = NumberFormat.decimalPattern();
    return Scaffold(
      body: SafeArea(
        child: vm.loading && vm.records.isEmpty
            ? const Center(child: CircularProgressIndicator())
            : ListView(
                padding: const EdgeInsets.fromLTRB(18, 20, 18, 126),
                children: [
                  Row(children: [
                    Expanded(child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
                      Text('Good morning!', style: Theme.of(context).textTheme.headlineMedium?.copyWith(color: AppColors.ink, fontWeight: FontWeight.w800)),
                      const SizedBox(height: 2),
                      Text(DateFormat('EEEE, d MMMM').format(DateTime.now()), style: Theme.of(context).textTheme.titleMedium?.copyWith(color: const Color(0xFF59658C))),
                    ])),
                    _HeaderButton(icon: Icons.notifications_none_rounded, onTap: () => Navigator.push(context, MaterialPageRoute(builder: (_) => const RemindersScreen()))),
                    const SizedBox(width: 10),
                    _HeaderButton(icon: Icons.tune_rounded, onTap: () => Navigator.push(context, MaterialPageRoute(builder: (_) => const GoalsScreen()))),
                  ]),
                  const SizedBox(height: 28),
                  if (vm.error != null) Padding(padding: const EdgeInsets.only(bottom: 12), child: Text(vm.error!, style: TextStyle(color: Theme.of(context).colorScheme.error))),
                  MetricCard(icon: Icons.directions_walk, color: AppColors.steps, label: 'Steps', value: numbers.format(totals.steps), goal: '${numbers.format(vm.goals.steps)} goal', progress: totals.steps / vm.goals.steps),
                  const SizedBox(height: 12),
                  MetricCard(icon: Icons.local_fire_department, color: AppColors.calories, label: 'Calories burned', value: '${numbers.format(totals.calories)} kcal', goal: '${numbers.format(vm.goals.calories)} kcal goal', progress: totals.calories / vm.goals.calories),
                  const SizedBox(height: 12),
                  MetricCard(icon: Icons.water_drop, color: AppColors.water, label: 'Water intake', value: '${numbers.format(totals.water)} ml', goal: '${numbers.format(vm.goals.water)} ml goal', progress: totals.water / vm.goals.water),
                  const SizedBox(height: 24),
                  Text('Last 7 days – steps', style: Theme.of(context).textTheme.titleMedium?.copyWith(fontWeight: FontWeight.w800, color: AppColors.ink)),
                  const SizedBox(height: 12),
                  Card(child: Padding(padding: const EdgeInsets.all(16), child: _WeeklyChart(days: vm.lastSevenDays, goal: vm.goals.steps))),
                ],
              ),
      ),
    );
  }
}

class _HeaderButton extends StatelessWidget {
  final IconData icon;
  final VoidCallback onTap;
  const _HeaderButton({required this.icon, required this.onTap});
  @override
  Widget build(BuildContext context) => Material(color: Colors.white, shape: const CircleBorder(), child: IconButton(onPressed: onTap, icon: Icon(icon, color: AppColors.ink), padding: const EdgeInsets.all(16)));
}

class _WeeklyChart extends StatelessWidget {
  final List<MapEntry<DateTime, DayTotals>> days;
  final int goal;
  const _WeeklyChart({required this.days, required this.goal});
  @override
  Widget build(BuildContext context) {
    final maxSteps = days.map((day) => day.value.steps).fold<int>(goal, (max, steps) => steps > max ? steps : max);
    return SizedBox(height: 150, child: Row(crossAxisAlignment: CrossAxisAlignment.end, children: [
      for (final day in days) Expanded(child: Padding(padding: const EdgeInsets.symmetric(horizontal: 4), child: Column(mainAxisAlignment: MainAxisAlignment.end, children: [
        AnimatedContainer(duration: const Duration(milliseconds: 500), height: 100 * day.value.steps / maxSteps, decoration: BoxDecoration(color: AppColors.steps, borderRadius: BorderRadius.circular(5))),
        const SizedBox(height: 5), Text(DateFormat('E').format(day.key), style: Theme.of(context).textTheme.labelSmall),
      ]))),
    ]));
  }
}
