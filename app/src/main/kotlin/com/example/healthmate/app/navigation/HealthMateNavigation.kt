package com.example.healthmate.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.healthmate.feature.achievements.presentation.Achievements
import com.example.healthmate.feature.bmi.presentation.BmiCalculator
import com.example.healthmate.feature.common.presentation.Shell
import com.example.healthmate.feature.dashboard.presentation.Dashboard
import com.example.healthmate.feature.goals.presentation.Goals
import com.example.healthmate.feature.profile.presentation.Profile
import com.example.healthmate.feature.records.data.HealthRecord
import com.example.healthmate.feature.records.presentation.HealthViewModel
import com.example.healthmate.feature.dashboard.presentation.HealthUiState
import com.example.healthmate.feature.records.presentation.History
import com.example.healthmate.feature.records.presentation.RecordForm
import com.example.healthmate.feature.reminders.presentation.Reminders
import com.example.healthmate.feature.settings.presentation.Settings
import java.time.LocalDate

@Composable fun HealthMateNavigation(viewModel: HealthViewModel) {
    val nav = rememberNavController()
    val state by viewModel.state.collectAsStateWithLifecycle()
    NavHost(navController = nav, startDestination = Routes.HOME) {
        composable(Routes.HOME) { Shell(nav, 0) { Dashboard(state, { nav.navigate(Routes.REMINDERS) }, { nav.navigate(Routes.GOALS) }) } }
        composable(Routes.HISTORY) { Shell(nav, 1) { History(state.records, { id -> nav.navigate("${Routes.FORM}?id=$id") }, { viewModel.delete(it) }) } }
        composable("${Routes.FORM}?id={id}", arguments = listOf(navArgument("id") { type = NavType.LongType; defaultValue = 0L })) { entry -> Shell(nav, 2) { RecordForm(state.records.firstOrNull { it.id == entry.arguments?.getLong("id") }, onBack = { nav.popBackStack() }, onSave = { record -> if (record.id == 0L) viewModel.add(LocalDate.parse(record.date), record.steps, record.calories, record.water) else viewModel.update(record); nav.popBackStack() }) } }
        composable(Routes.GOALS) { Goals(state.goals, { viewModel.saveGoals(it); nav.popBackStack() }, { nav.popBackStack() }) }
        composable(Routes.SETTINGS) { Shell(nav, 3) { Settings(open = { nav.navigate(it) }, back = { nav.popBackStack() }) } }
        composable(Routes.PROFILE) { Profile(state.profile, { viewModel.saveProfile(it); nav.popBackStack() }, { nav.popBackStack() }) }
        composable(Routes.ACHIEVEMENTS) { Achievements(state.records, state.goals, { nav.popBackStack() }) }
        composable(Routes.BMI) { BmiCalculator(state.profile, { nav.popBackStack() }) }
        composable(Routes.REMINDERS) { Reminders(state.reminders, { settings -> viewModel.saveReminders(settings); nav.popBackStack() }, { nav.popBackStack() }) }
    }
}
