import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:provider/provider.dart';

import '../../../core/app_theme.dart';
import '../model/health_record.dart';
import '../viewmodel/record_viewmodel.dart';

/// Shows rewards and progress calculated from the user's saved health records.
class AchievementsScreen extends StatelessWidget {
  const AchievementsScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final vm = context.watch<RecordViewModel>();
    final progress = _AchievementProgress.fromRecords(vm.allRecords, vm.goals);

    return Scaffold(
      appBar: AppBar(title: const Text('Achievements')),
      body: ListView(
        padding: const EdgeInsets.fromLTRB(20, 12, 20, 32),
        children: [
          Text(
            'Your journey',
            style: Theme.of(context).textTheme.headlineSmall?.copyWith(
                  color: AppColors.ink,
                  fontWeight: FontWeight.w800,
                ),
          ),
          const SizedBox(height: 6),
          Text(
            'Every recorded day helps build a healthier routine.',
            style: Theme.of(context).textTheme.bodyLarge?.copyWith(
                  color: const Color(0xFF59658C),
                ),
          ),
          const SizedBox(height: 24),
          _StreakCard(streak: progress.currentStreak),
          const SizedBox(height: 28),
          _SectionTitle(title: 'Badges', trailing: '${progress.earnedBadges.length}/${progress.badges.length} earned'),
          const SizedBox(height: 12),
          SizedBox(
            height: 156,
            child: ListView.separated(
              scrollDirection: Axis.horizontal,
              itemCount: progress.badges.length,
              separatorBuilder: (_, __) => const SizedBox(width: 12),
              itemBuilder: (_, index) => _BadgeCard(badge: progress.badges[index]),
            ),
          ),
          const SizedBox(height: 28),
          const _SectionTitle(title: 'Fitness milestones'),
          const SizedBox(height: 12),
          ...progress.milestones.map(
            (milestone) => Padding(
              padding: const EdgeInsets.only(bottom: 12),
              child: _MilestoneCard(milestone: milestone),
            ),
          ),
        ],
      ),
    );
  }
}

class _SectionTitle extends StatelessWidget {
  final String title;
  final String? trailing;
  const _SectionTitle({required this.title, this.trailing});

  @override
  Widget build(BuildContext context) => Row(
        children: [
          Text(title, style: Theme.of(context).textTheme.titleLarge?.copyWith(color: AppColors.ink, fontWeight: FontWeight.w800)),
          const Spacer(),
          if (trailing != null) Text(trailing!, style: Theme.of(context).textTheme.labelLarge?.copyWith(color: const Color(0xFF59658C))),
        ],
      );
}

class _StreakCard extends StatelessWidget {
  final int streak;
  const _StreakCard({required this.streak});

  @override
  Widget build(BuildContext context) => Container(
        padding: const EdgeInsets.all(22),
        decoration: BoxDecoration(
          color: AppColors.ink,
          borderRadius: BorderRadius.circular(26),
        ),
        child: Row(children: [
          Container(
            width: 62,
            height: 62,
            decoration: BoxDecoration(color: const Color(0xFFFFC45A), borderRadius: BorderRadius.circular(20)),
            child: const Icon(Icons.local_fire_department_rounded, color: AppColors.ink, size: 35),
          ),
          const SizedBox(width: 18),
          Expanded(
            child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
              Text('$streak day${streak == 1 ? '' : 's'}', style: Theme.of(context).textTheme.headlineSmall?.copyWith(color: Colors.white, fontWeight: FontWeight.w800)),
              const SizedBox(height: 3),
              Text(
                streak == 0 ? 'Record an activity to begin your streak.' : 'Keep showing up—your streak is growing!',
                style: Theme.of(context).textTheme.bodyMedium?.copyWith(color: const Color(0xFFC8D0FF)),
              ),
            ]),
          ),
        ]),
      );
}

class _BadgeCard extends StatelessWidget {
  final _Badge badge;
  const _BadgeCard({required this.badge});

  @override
  Widget build(BuildContext context) => SizedBox(
        width: 140,
        child: Card(
          color: badge.earned ? badge.color.withValues(alpha: .14) : Colors.white,
          child: Padding(
            padding: const EdgeInsets.all(14),
            child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
              CircleAvatar(
                backgroundColor: badge.earned ? badge.color : const Color(0xFFE4E8F3),
                child: Icon(badge.earned ? badge.icon : Icons.lock_outline_rounded, color: badge.earned ? Colors.white : const Color(0xFF7A849F)),
              ),
              const Spacer(),
              Text(badge.title, maxLines: 1, overflow: TextOverflow.ellipsis, style: TextStyle(color: badge.earned ? AppColors.ink : const Color(0xFF7A849F), fontWeight: FontWeight.w800)),
              const SizedBox(height: 3),
              Text(badge.detail, maxLines: 2, overflow: TextOverflow.ellipsis, style: Theme.of(context).textTheme.labelSmall?.copyWith(color: const Color(0xFF59658C))),
            ]),
          ),
        ),
      );
}

class _MilestoneCard extends StatelessWidget {
  final _Milestone milestone;
  const _MilestoneCard({required this.milestone});

  @override
  Widget build(BuildContext context) {
    final percent =
        (milestone.value / milestone.target).clamp(0.0, 1.0).toDouble();
    final valueText = NumberFormat.compact().format(milestone.value);
    final targetText = NumberFormat.compact().format(milestone.target);
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(18),
        child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
          Row(children: [
            CircleAvatar(backgroundColor: milestone.color.withValues(alpha: .14), child: Icon(milestone.icon, color: milestone.color)),
            const SizedBox(width: 12),
            Expanded(child: Text(milestone.title, style: const TextStyle(color: AppColors.ink, fontWeight: FontWeight.w800))),
            Text('$valueText / $targetText ${milestone.unit}', style: Theme.of(context).textTheme.labelMedium?.copyWith(color: const Color(0xFF59658C))),
          ]),
          const SizedBox(height: 14),
          ClipRRect(
            borderRadius: BorderRadius.circular(20),
            child: LinearProgressIndicator(value: percent, minHeight: 9, color: milestone.color, backgroundColor: milestone.color.withValues(alpha: .14)),
          ),
        ]),
      ),
    );
  }
}

class _AchievementProgress {
  final int currentStreak;
  final List<_Badge> badges;
  final List<_Milestone> milestones;
  const _AchievementProgress(this.currentStreak, this.badges, this.milestones);

  List<_Badge> get earnedBadges => badges.where((badge) => badge.earned).toList();

  factory _AchievementProgress.fromRecords(List<HealthRecord> records, DailyGoals goals) {
    final perDay = <String, DayTotals>{};
    for (final record in records) {
      final existing = perDay[record.date] ?? const DayTotals(0, 0, 0);
      perDay[record.date] = DayTotals(existing.steps + record.steps, existing.calories + record.calories, existing.water + record.water);
    }
    final totalSteps = records.fold(0, (sum, record) => sum + record.steps);
    final totalWater = records.fold(0, (sum, record) => sum + record.water);
    final goalDays = perDay.values.where((day) => day.steps >= goals.steps || day.calories >= goals.calories || day.water >= goals.water).length;
    final streak = _currentStreak(perDay, goals);
    final badges = [
      _Badge('First step', 'Save your first record', Icons.emoji_events_rounded, AppColors.seed, records.isNotEmpty),
      _Badge('Goal getter', 'Reach a daily goal 3 times', Icons.flag_rounded, AppColors.steps, goalDays >= 3),
      _Badge('On a roll', 'Maintain a 3-day streak', Icons.local_fire_department_rounded, const Color(0xFFE18A00), streak >= 3),
      _Badge('Century club', 'Walk 100,000 total steps', Icons.workspace_premium_rounded, AppColors.calories, totalSteps >= 100000),
    ];
    return _AchievementProgress(streak, badges, [
      _Milestone('Steps explorer', totalSteps, 100000, 'steps', Icons.directions_walk_rounded, AppColors.steps),
      _Milestone('Hydration hero', totalWater, 25000, 'ml', Icons.water_drop_rounded, AppColors.water),
      _Milestone('Goal getter', goalDays, 10, 'days', Icons.flag_rounded, AppColors.seed),
    ]);
  }

  static int _currentStreak(Map<String, DayTotals> perDay, DailyGoals goals) {
    var day = DateTime.now();
    var streak = 0;
    while (true) {
      final total = perDay[RecordViewModel.format(day)];
      final active = total != null && (total.steps >= goals.steps || total.calories >= goals.calories || total.water >= goals.water);
      if (!active) return streak;
      streak++;
      day = day.subtract(const Duration(days: 1));
    }
  }
}

class _Badge {
  final String title;
  final String detail;
  final IconData icon;
  final Color color;
  final bool earned;
  const _Badge(this.title, this.detail, this.icon, this.color, this.earned);
}

class _Milestone {
  final String title;
  final int value;
  final int target;
  final String unit;
  final IconData icon;
  final Color color;
  const _Milestone(this.title, this.value, this.target, this.unit, this.icon, this.color);
}
