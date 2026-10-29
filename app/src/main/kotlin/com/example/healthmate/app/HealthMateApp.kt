package com.example.healthmate.app

import androidx.compose.runtime.Composable
import com.example.healthmate.app.navigation.HealthMateNavigation
import com.example.healthmate.feature.records.presentation.HealthViewModel

@Composable fun HealthMateApp(viewModel: HealthViewModel) {
    HealthMateNavigation(viewModel)
}
