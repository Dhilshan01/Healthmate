package com.example.healthmate.feature.records.presentation

import android.app.Application
import androidx.lifecycle.*
import com.example.healthmate.feature.records.data.*
import com.example.healthmate.feature.dashboard.presentation.HealthUiState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.LocalDate

class HealthViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = HealthRepository(application)
    private val logMutex = Mutex()
    val state: StateFlow<HealthUiState> = combine(repository.records, repository.goals, repository.reminders, repository.profile) { records, goals, reminders, profile -> HealthUiState(records, goals, reminders, profile, false) }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HealthUiState())
    fun add(date: LocalDate, steps: Int, calories: Int, water: Int) = viewModelScope.launch { repository.add(HealthRecord(date = date.toString(), steps = steps, calories = calories, water = water)) }

    fun logToday(steps: Int = 0, calories: Int = 0, water: Int = 0) = viewModelScope.launch {
        logMutex.withLock {
            val today = LocalDate.now().toString()
            val existing = repository.records.value.firstOrNull { it.date == today }
            val nextSteps = (existing?.steps ?: 0) + steps
            val nextCalories = (existing?.calories ?: 0) + calories
            val nextWater = (existing?.water ?: 0) + water
            val fits = nextSteps <= 100_000 && nextCalories <= 20_000 && nextWater <= 20_000
            if (existing == null || !fits) {
                repository.add(HealthRecord(date = today, steps = steps, calories = calories, water = water))
            } else {
                repository.update(existing.copy(steps = nextSteps, calories = nextCalories, water = nextWater))
            }
        }
    }
    fun update(record: HealthRecord) = viewModelScope.launch { repository.update(record) }
    fun delete(record: HealthRecord) = viewModelScope.launch { repository.delete(record) }
    fun saveGoals(goals: DailyGoals) = viewModelScope.launch { repository.saveGoals(goals) }
    fun saveReminders(settings: ReminderSettings) = viewModelScope.launch { repository.saveReminders(settings) }
    fun saveProfile(profile: UserProfile) = viewModelScope.launch { repository.saveProfile(profile) }
}
