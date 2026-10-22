import 'package:flutter/material.dart';
import '../../../core/app_theme.dart';
import 'dashboard_screen.dart';
import 'record_form_screen.dart';
import 'record_list_screen.dart';
import 'settings_screen.dart';

/// Bottom navigation between Dashboard and History, with an Add button.
class HomeShell extends StatefulWidget {
  const HomeShell({super.key});

  @override
  State<HomeShell> createState() => _HomeShellState();
}

class _HomeShellState extends State<HomeShell> {
  int _index = 0;

  static const _screens = [DashboardScreen(), RecordListScreen()];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: _screens[_index],
      bottomNavigationBar: SafeArea(
        minimum: const EdgeInsets.fromLTRB(12, 0, 12, 12),
        child: Container(
          height: 72,
          padding: const EdgeInsets.symmetric(horizontal: 14),
          decoration: BoxDecoration(
            color: AppColors.ink,
            borderRadius: BorderRadius.circular(40),
          ),
          child: Row(children: [
            _navButton(0, Icons.grid_view_rounded, 'Home'),
            const Spacer(),
            _navButton(1, Icons.history_rounded, 'History'),
            const SizedBox(width: 8),
            _AddButton(onTap: () => Navigator.push(
                  context,
                  MaterialPageRoute(builder: (_) => const RecordFormScreen()),
                )),
            const SizedBox(width: 8),
            _SettingsButton(onTap: () => Navigator.push(
                  context,
                  MaterialPageRoute(builder: (_) => const SettingsScreen()),
                )),
          ]),
        ),
      ),
    );
  }

  Widget _navButton(int target, IconData icon, String label) {
    final selected = _index == target;
    return InkWell(
      onTap: () => setState(() => _index = target),
      borderRadius: BorderRadius.circular(30),
      child: AnimatedContainer(
        duration: const Duration(milliseconds: 240),
        curve: Curves.easeOut,
        padding: EdgeInsets.symmetric(horizontal: selected ? 20 : 15, vertical: 14),
        decoration: BoxDecoration(color: selected ? Colors.white : Colors.transparent, borderRadius: BorderRadius.circular(30)),
        child: Row(mainAxisSize: MainAxisSize.min, children: [
          Icon(icon, color: selected ? AppColors.ink : const Color(0xFFC8D0FF), size: 25),
          if (selected) ...[const SizedBox(width: 8), Text(label, style: const TextStyle(color: AppColors.ink, fontWeight: FontWeight.w800))],
        ]),
      ),
    );
  }
}

class _AddButton extends StatelessWidget {
  final VoidCallback onTap;
  const _AddButton({required this.onTap});

  @override
  Widget build(BuildContext context) => Material(
        color: AppColors.seed,
        shape: const CircleBorder(),
        child: InkWell(
          onTap: onTap,
          customBorder: const CircleBorder(),
          child: const SizedBox(
            width: 56,
            height: 56,
            child: Icon(Icons.add_rounded, color: Colors.white, size: 33),
          ),
        ),
      );
}

class _SettingsButton extends StatelessWidget {
  final VoidCallback onTap;
  const _SettingsButton({required this.onTap});

  @override
  Widget build(BuildContext context) => Material(
        color: const Color(0xFFC8D0FF),
        shape: const CircleBorder(),
        child: InkWell(
          onTap: onTap,
          customBorder: const CircleBorder(),
          child: const SizedBox(
            width: 56,
            height: 56,
            child: Icon(Icons.settings_outlined, color: AppColors.ink, size: 27),
          ),
        ),
      );
}
