package com.example.healthmate.feature.dashboard.presentation

import com.example.healthmate.feature.records.data.DailyGoals
import com.example.healthmate.feature.records.data.HealthRecord
import com.example.healthmate.feature.records.data.ReminderSettings
import com.example.healthmate.feature.records.data.UserProfile

data class HealthUiState(
    val records: List<HealthRecord> = emptyList(),
    val goals: DailyGoals = DailyGoals(),
    val reminders: ReminderSettings = ReminderSettings(),
    val profile: UserProfile = UserProfile(),
    val loading: Boolean = true,
)
