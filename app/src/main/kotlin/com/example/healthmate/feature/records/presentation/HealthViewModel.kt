package com.example.healthmate.feature.records.presentation

import android.app.Application
import androidx.lifecycle.*
import com.example.healthmate.feature.records.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class HealthUiState(val records: List<HealthRecord> = emptyList(), val goals: DailyGoals = DailyGoals(), val reminders: ReminderSettings = ReminderSettings(), val loading: Boolean = true)

class HealthViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = HealthRepository(application)
    val state: StateFlow<HealthUiState> = combine(repository.records, repository.goals, repository.reminders) { records, goals, reminders -> HealthUiState(records, goals, reminders, false) }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HealthUiState())
    fun add(date: LocalDate, steps: Int, calories: Int, water: Int) = viewModelScope.launch { repository.add(HealthRecord(date = date.toString(), steps = steps, calories = calories, water = water)) }
    fun update(record: HealthRecord) = viewModelScope.launch { repository.update(record) }
    fun delete(record: HealthRecord) = viewModelScope.launch { repository.delete(record) }
    fun saveGoals(goals: DailyGoals) = viewModelScope.launch { repository.saveGoals(goals) }
    fun saveReminders(settings: ReminderSettings) = viewModelScope.launch { repository.saveReminders(settings) }
    companion object { val dateFormatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE }
}
