@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.healthmate.feature.records.presentation

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.healthmate.core.design.HealthColors
import com.example.healthmate.core.notifications.ReminderScheduler
import com.example.healthmate.feature.records.data.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

private object Route { const val HOME = "home"; const val HISTORY = "history"; const val FORM = "form"; const val GOALS = "goals"; const val SETTINGS = "settings"; const val ACHIEVEMENTS = "achievements"; const val BMI = "bmi"; const val REMINDERS = "reminders" }

@Composable fun HealthMateApp(viewModel: HealthViewModel) {
    val nav = rememberNavController(); val state by viewModel.state.collectAsStateWithLifecycle()
    NavHost(nav, Route.HOME) {
        composable(Route.HOME) { Shell(nav, 0) { Dashboard(state, { nav.navigate(Route.REMINDERS) }, { nav.navigate(Route.GOALS) }) } }
        composable(Route.HISTORY) { Shell(nav, 1) { History(state.records, { id -> nav.navigate("${Route.FORM}?id=$id") }, { viewModel.delete(it) }) } }
        composable("${Route.FORM}?id={id}", arguments = listOf(navArgument("id") { type = NavType.LongType; defaultValue = 0L })) { entry -> RecordForm(state.records.firstOrNull { it.id == entry.arguments?.getLong("id") }, onBack = { nav.popBackStack() }, onSave = { r -> if (r.id == 0L) viewModel.add(LocalDate.parse(r.date), r.steps, r.calories, r.water) else viewModel.update(r); nav.popBackStack() }) }
        composable(Route.GOALS) { Goals(state.goals, { viewModel.saveGoals(it); nav.popBackStack() }, { nav.popBackStack() }) }
        composable(Route.SETTINGS) { Settings { nav.navigate(it) } }
        composable(Route.ACHIEVEMENTS) { Achievements(state.records, state.goals, { nav.popBackStack() }) }
        composable(Route.BMI) { Bmi({ nav.popBackStack() }) }
        composable(Route.REMINDERS) { Reminders(state.reminders, { s -> viewModel.saveReminders(s); nav.popBackStack() }, { nav.popBackStack() }) }
    }
}

@Composable private fun Shell(nav: androidx.navigation.NavHostController, selected: Int, content: @Composable () -> Unit) {
    Scaffold(bottomBar = { NavigationBar { NavigationBarItem(selected = selected == 0, onClick = { nav.navigate(Route.HOME) { launchSingleTop = true } }, icon = { Icon(Icons.Filled.Home, null) }, label = { Text("Home") }); NavigationBarItem(selected = selected == 1, onClick = { nav.navigate(Route.HISTORY) { launchSingleTop = true } }, icon = { Icon(Icons.Filled.History, null) }, label = { Text("History") }); NavigationBarItem(selected = false, onClick = { nav.navigate("${Route.FORM}?id=0") }, icon = { Icon(Icons.Filled.AddCircle, null) }, label = { Text("Add") }); NavigationBarItem(selected = false, onClick = { nav.navigate(Route.SETTINGS) }, icon = { Icon(Icons.Filled.Settings, null) }, label = { Text("Settings") }) } }) { padding -> Box(Modifier.padding(padding)) { content() } }
}

@Composable private fun Dashboard(state: HealthUiState, reminders: () -> Unit, goals: () -> Unit) {
    val today = LocalDate.now().toString(); val records = state.records.filter { it.date == today }; val steps = records.sumOf { it.steps }; val calories = records.sumOf { it.calories }; val water = records.sumOf { it.water }
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item { Row(verticalAlignment = Alignment.CenterVertically) { Column(Modifier.weight(1f)) { Text("Good morning!", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = HealthColors.Ink); Text(LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, d MMMM")), color = Color(0xFF59658C)) }; IconButton(reminders) { Icon(Icons.Outlined.Notifications, "Reminders") }; IconButton(goals) { Icon(Icons.Outlined.Tune, "Goals") } } }
        item { MetricCard("Steps", "$steps", "${state.goals.steps} goal", steps.toFloat() / state.goals.steps, HealthColors.Steps, Icons.Filled.DirectionsWalk) }
        item { MetricCard("Calories burned", "$calories kcal", "${state.goals.calories} kcal goal", calories.toFloat() / state.goals.calories, HealthColors.Calories, Icons.Filled.LocalFireDepartment) }
        item { MetricCard("Water intake", "$water ml", "${state.goals.water} ml goal", water.toFloat() / state.goals.water, HealthColors.Water, Icons.Filled.WaterDrop) }
        item { Text("Last 7 days – steps", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = HealthColors.Ink); Spacer(Modifier.height(8.dp)); WeeklyChart(state.records, state.goals.steps) }
    }
}

@Composable private fun MetricCard(label: String, value: String, goal: String, progress: Float, color: Color, icon: androidx.compose.ui.graphics.vector.ImageVector) = Card { Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) { Icon(icon, null, tint = color, modifier = Modifier.size(34.dp)); Spacer(Modifier.width(16.dp)); Column(Modifier.weight(1f)) { Text(label); Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold); Text(goal, style = MaterialTheme.typography.bodySmall); Spacer(Modifier.height(8.dp)); LinearProgressIndicator(progress = { progress.coerceIn(0f, 1f) }, color = color, modifier = Modifier.fillMaxWidth()) } } }

@Composable private fun WeeklyChart(records: List<HealthRecord>, goal: Int) { val days = (6 downTo 0).map { LocalDate.now().minusDays(it.toLong()) }; val values = days.map { d -> records.filter { it.date == d.toString() }.sumOf { it.steps } }; val max = maxOf(goal, values.maxOrNull() ?: 0)
    Card { Row(Modifier.fillMaxWidth().height(170.dp).padding(14.dp), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.Bottom) { days.zip(values).forEach { (day, value) -> Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Bottom, modifier = Modifier.weight(1f).fillMaxHeight()) { Spacer(Modifier.weight(1f)); Box(Modifier.fillMaxWidth(.58f).height((110f * value / max).dp.coerceAtLeast(2.dp)).background(HealthColors.Steps)); Text(day.dayOfWeek.name.take(3), style = MaterialTheme.typography.labelSmall) } } } }
}

@Composable private fun History(records: List<HealthRecord>, edit: (Long) -> Unit, delete: (HealthRecord) -> Unit) { var filter by remember { mutableStateOf("") }; val shown = records.filter { filter.isBlank() || it.date.contains(filter) }; var pending by remember { mutableStateOf<HealthRecord?>(null) }
    Scaffold(topBar = { TopAppBar(title = { Text("History") }, actions = { Icon(Icons.Outlined.Search, null); OutlinedTextField(filter, { filter = it }, label = { Text("Date (yyyy-mm-dd)") }, modifier = Modifier.width(190.dp)) }) }) { p -> LazyColumn(Modifier.padding(p).fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) { if (shown.isEmpty()) item { Box(Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) { Text("No records found") } }; items(shown, key = { it.id }) { r -> Card(Modifier.fillMaxWidth().clickable { edit(r.id) }) { Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) { Column(Modifier.weight(1f)) { Text(r.date, fontWeight = FontWeight.Bold); Text("🚶 ${r.steps}    🔥 ${r.calories} kcal    💧 ${r.water} ml") }; IconButton({ pending = r }) { Icon(Icons.Outlined.Delete, "Delete") } } } } } }
    pending?.let { r -> AlertDialog(onDismissRequest = { pending = null }, title = { Text("Delete record?") }, text = { Text("The record for ${r.date} will be permanently removed.") }, confirmButton = { TextButton({ delete(r); pending = null }) { Text("Delete") } }, dismissButton = { TextButton({ pending = null }) { Text("Cancel") } }) }
}

@Composable private fun RecordForm(record: HealthRecord?, onBack: () -> Unit, onSave: (HealthRecord) -> Unit) { var date by remember { mutableStateOf(record?.date ?: LocalDate.now().toString()) }; var steps by remember { mutableStateOf(record?.steps?.toString() ?: "") }; var calories by remember { mutableStateOf(record?.calories?.toString() ?: "") }; var water by remember { mutableStateOf(record?.water?.toString() ?: "") }; var error by remember { mutableStateOf<String?>(null) }
    Scaffold(topBar = { TopAppBar(title = { Text(if (record == null) "Add record" else "Edit record") }, navigationIcon = { IconButton(onBack) { Icon(Icons.Filled.ArrowBack, "Back") } }) }) { p -> Column(Modifier.padding(p).padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) { Input(date, { date = it }, "Date (yyyy-mm-dd)"); Input(steps, { steps = it }, "Steps", KeyboardType.Number); Input(calories, { calories = it }, "Calories burned (kcal)", KeyboardType.Number); Input(water, { water = it }, "Water intake (ml)", KeyboardType.Number); error?.let { Text(it, color = MaterialTheme.colorScheme.error) }; Button({ val s = steps.toIntOrNull(); val c = calories.toIntOrNull(); val w = water.toIntOrNull(); if (runCatching { LocalDate.parse(date) }.isFailure || s == null || c == null || w == null || s !in 0..100000 || c !in 0..20000 || w !in 0..20000) error = "Enter a valid date and non-negative values within the allowed limits." else onSave(HealthRecord(record?.id ?: 0, date, s, c, w)) }, Modifier.fillMaxWidth()) { Text(if (record == null) "Save" else "Update") } } }
}

@Composable private fun Input(value: String, change: (String) -> Unit, label: String, type: KeyboardType = KeyboardType.Text) = OutlinedTextField(value, change, label = { Text(label) }, keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = type), singleLine = true, modifier = Modifier.fillMaxWidth())

@Composable private fun Settings(open: (String) -> Unit) = Scaffold(topBar = { TopAppBar(title = { Text("Settings") }) }) { p -> LazyColumn(Modifier.padding(p).padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) { item { Text("Make HealthMate fit your routine.", style = MaterialTheme.typography.titleMedium) }; item { SettingItem("Daily goals", "Steps, calories, and water targets", Icons.Outlined.Flag, HealthColors.Steps) { open(Route.GOALS) } }; item { SettingItem("Achievements", "Badges, streaks, and fitness milestones", Icons.Outlined.EmojiEvents, Color(0xFFE18A00)) { open(Route.ACHIEVEMENTS) } }; item { SettingItem("BMI calculator", "Calculate BMI from height and weight", Icons.Outlined.Calculate, HealthColors.Seed) { open(Route.BMI) } }; item { SettingItem("Reminders", "Water, walking, and activity prompts", Icons.Outlined.Notifications, HealthColors.Seed) { open(Route.REMINDERS) } } } }
@Composable private fun SettingItem(title: String, subtitle: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, click: () -> Unit) = Card(Modifier.fillMaxWidth().clickable(onClick = click)) { Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) { Icon(icon, null, tint = color, modifier = Modifier.size(30.dp)); Spacer(Modifier.width(16.dp)); Column(Modifier.weight(1f)) { Text(title, fontWeight = FontWeight.Bold); Text(subtitle, style = MaterialTheme.typography.bodySmall) }; Icon(Icons.Filled.ChevronRight, null) } }

@Composable private fun Goals(current: DailyGoals, save: (DailyGoals) -> Unit, back: () -> Unit) { var steps by remember { mutableStateOf(current.steps.toString()) }; var calories by remember { mutableStateOf(current.calories.toString()) }; var water by remember { mutableStateOf(current.water.toString()) }; var error by remember { mutableStateOf<String?>(null) }
    Scaffold(topBar = { BackBar("Daily goals", back) }) { p -> Column(Modifier.padding(p).padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) { Text("Set targets that fit your routine."); Input(steps, { steps = it }, "Steps goal", KeyboardType.Number); Input(calories, { calories = it }, "Calories goal", KeyboardType.Number); Input(water, { water = it }, "Water goal", KeyboardType.Number); error?.let { Text(it, color = MaterialTheme.colorScheme.error) }; Button({ val s = steps.toIntOrNull(); val c = calories.toIntOrNull(); val w = water.toIntOrNull(); if (s == null || c == null || w == null || s !in 1..100000 || c !in 1..20000 || w !in 1..20000) error = "Goals must be whole numbers greater than zero within the allowed limits." else save(DailyGoals(steps = s, calories = c, water = w)) }, Modifier.fillMaxWidth()) { Text("Save goals") } } }
}

@Composable private fun Bmi(back: () -> Unit) { var height by remember { mutableStateOf("") }; var weight by remember { mutableStateOf("") }; var bmi by remember { mutableStateOf<Double?>(null) }; var error by remember { mutableStateOf<String?>(null) }
    Scaffold(topBar = { BackBar("BMI calculator", back) }) { p -> Column(Modifier.padding(p).padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) { Text("Check your body mass index", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold); Text("Enter your height and weight to calculate your BMI."); Input(height, { height = it }, "Height (cm)", KeyboardType.Decimal); Input(weight, { weight = it }, "Weight (kg)", KeyboardType.Decimal); Button({ val h = height.toDoubleOrNull(); val w = weight.toDoubleOrNull(); if (h == null || w == null || h !in 50.0..300.0 || w !in 10.0..500.0) error = "Height must be 50–300 cm and weight 10–500 kg." else { error = null; bmi = w / ((h / 100) * (h / 100)) } }, Modifier.fillMaxWidth()) { Text("Calculate BMI") }; error?.let { Text(it, color = MaterialTheme.colorScheme.error) }; bmi?.let { value -> val (label, color) = when { value < 18.5 -> "Underweight" to HealthColors.Water; value < 25 -> "Healthy range" to HealthColors.Steps; value < 30 -> "Overweight" to Color(0xFFE18A00); else -> "Obesity range" to HealthColors.Calories }; Card(colors = CardDefaults.cardColors(containerColor = color.copy(alpha = .12f))) { Column(Modifier.padding(22.dp)) { Text(label, color = color, fontWeight = FontWeight.Bold); Text("%.1f".format(value), style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Bold); Text("Your BMI") } } }; Spacer(Modifier.height(4.dp)); Text("BMI is a screening measure and does not diagnose body fat or health on its own.", style = MaterialTheme.typography.bodySmall) } }
}

@Composable private fun Achievements(records: List<HealthRecord>, goals: DailyGoals, back: () -> Unit) { val totalSteps = records.sumOf { it.steps }; val totalWater = records.sumOf { it.water }; val days = records.groupBy { it.date }.mapValues { (_, r) -> Triple(r.sumOf { it.steps }, r.sumOf { it.calories }, r.sumOf { it.water }) }; val goalDays = days.values.count { it.first >= goals.steps || it.second >= goals.calories || it.third >= goals.water }; var streak = 0; var day = LocalDate.now(); while (days[day.toString()]?.let { it.first >= goals.steps || it.second >= goals.calories || it.third >= goals.water } == true) { streak++; day = day.minusDays(1) }
    Scaffold(topBar = { BackBar("Achievements", back) }) { p -> LazyColumn(Modifier.padding(p).padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) { item { Text("Your journey", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold); Text("Every recorded day helps build a healthier routine.") }; item { Card(colors = CardDefaults.cardColors(containerColor = HealthColors.Ink)) { Row(Modifier.padding(22.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Filled.LocalFireDepartment, null, tint = Color(0xFFFFC45A), modifier = Modifier.size(46.dp)); Spacer(Modifier.width(18.dp)); Column { Text("$streak day${if (streak == 1) "" else "s"}", color = Color.White, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold); Text(if (streak == 0) "Record an activity to begin your streak." else "Keep showing up—your streak is growing!", color = Color(0xFFC8D0FF)) } } } }; item { Text("Badges", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) }; item { Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) { Badge("First step", records.isNotEmpty()); Badge("Goal getter", goalDays >= 3); Badge("On a roll", streak >= 3); Badge("Century club", totalSteps >= 100000) } }; item { Text("Fitness milestones", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) }; item { Milestone("Steps explorer", totalSteps, 100000, "steps", HealthColors.Steps) }; item { Milestone("Hydration hero", totalWater, 25000, "ml", HealthColors.Water) }; item { Milestone("Goal getter", goalDays, 10, "days", HealthColors.Seed) } } }
}
@Composable private fun Badge(title: String, earned: Boolean) = Card(Modifier.width(82.dp)) { Column(Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) { Icon(if (earned) Icons.Filled.EmojiEvents else Icons.Outlined.Lock, null, tint = if (earned) Color(0xFFE18A00) else Color.Gray); Text(title, style = MaterialTheme.typography.labelSmall, maxLines = 2) } }
@Composable private fun Milestone(title: String, value: Int, target: Int, unit: String, color: Color) = Card { Column(Modifier.padding(16.dp)) { Row { Text(title, Modifier.weight(1f), fontWeight = FontWeight.Bold); Text("$value / $target $unit", style = MaterialTheme.typography.labelSmall) }; Spacer(Modifier.height(10.dp)); LinearProgressIndicator(progress = { (value.toFloat() / target).coerceIn(0f, 1f) }, color = color, modifier = Modifier.fillMaxWidth()) } }

@Composable private fun Reminders(current: ReminderSettings, save: (ReminderSettings) -> Unit, back: () -> Unit) { var settings by remember { mutableStateOf(current) }; val context = androidx.compose.ui.platform.LocalContext.current; val requestPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { }
    Scaffold(topBar = { BackBar("Daily reminders", back) }) { p -> Column(Modifier.padding(p).padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) { Text("Gentle prompts for your healthy habits."); ReminderRow("Drink water", "Stay hydrated throughout the day", settings.waterEnabled, settings.waterMinutes, HealthColors.Water, { settings = settings.copy(waterEnabled = it) }, { settings = settings.copy(waterMinutes = it) }); ReminderRow("Go for a walk", "Take a short movement break", settings.walkingEnabled, settings.walkingMinutes, HealthColors.Steps, { settings = settings.copy(walkingEnabled = it) }, { settings = settings.copy(walkingMinutes = it) }); ReminderRow("Daily activity", "Make time to move your body", settings.activityEnabled, settings.activityMinutes, HealthColors.Calories, { settings = settings.copy(activityEnabled = it) }, { settings = settings.copy(activityMinutes = it) }); Button({ if (Build.VERSION.SDK_INT >= 33) requestPermission.launch(Manifest.permission.POST_NOTIFICATIONS); listOf(Triple(101, settings.waterEnabled, settings.waterMinutes), Triple(102, settings.walkingEnabled, settings.walkingMinutes), Triple(103, settings.activityEnabled, settings.activityMinutes)).forEachIndexed { i, (id, enabled, minutes) -> if (enabled) ReminderScheduler.schedule(context, id, minutes, listOf("Drink some water", "Time for a walk", "Move your body")[i], listOf("A little hydration goes a long way.", "Take a short walk and add to your step count.", "Make time for your daily activity.")[i]) else ReminderScheduler.cancel(context, id) }; save(settings) }, Modifier.fillMaxWidth()) { Text("Save reminders") } } }
}
@Composable private fun ReminderRow(title: String, subtitle: String, enabled: Boolean, minutes: Int, color: Color, toggle: (Boolean) -> Unit, updateMinutes: (Int) -> Unit) {
    var text by remember(minutes) { mutableStateOf("%02d:%02d".format(minutes / 60, minutes % 60)) }
    Card(colors = CardDefaults.cardColors(containerColor = color.copy(alpha = if (enabled) .13f else .06f))) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) { Text(title, fontWeight = FontWeight.Bold); Text(subtitle, style = MaterialTheme.typography.bodySmall) }
                Switch(enabled, toggle)
            }
            if (enabled) Input(text, {
                text = it
                Regex("^(\\d{1,2}):(\\d{2})$").matchEntire(it)?.let { match ->
                    val hour = match.groupValues[1].toInt(); val minute = match.groupValues[2].toInt()
                    if (hour in 0..23 && minute in 0..59) updateMinutes(hour * 60 + minute)
                }
            }, "Daily at (HH:mm)")
        }
    }
}
@Composable private fun BackBar(title: String, back: () -> Unit) = TopAppBar(title = { Text(title) }, navigationIcon = { IconButton(back) { Icon(Icons.Filled.ArrowBack, "Back") } })
