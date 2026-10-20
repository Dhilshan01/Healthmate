import 'package:flutter/material.dart';

import 'dashboard_screen.dart';
import 'record_form_screen.dart';
import 'record_list_screen.dart';

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
      floatingActionButton: FloatingActionButton.extended(
        onPressed: () => Navigator.push(
          context,
          MaterialPageRoute(builder: (_) => const RecordFormScreen()),
        ),
        icon: const Icon(Icons.add),
        label: const Text('Add record'),
      ),
      bottomNavigationBar: NavigationBar(
        selectedIndex: _index,
        onDestinationSelected: (i) => setState(() => _index = i),
        destinations: const [
          NavigationDestination(
              icon: Icon(Icons.dashboard_outlined),
              selectedIcon: Icon(Icons.dashboard),
              label: 'Dashboard'),
          NavigationDestination(
              icon: Icon(Icons.history), label: 'History'),
        ],
      ),
    );
  }
}
