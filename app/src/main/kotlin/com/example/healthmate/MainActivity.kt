package com.example.healthmate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.healthmate.core.design.HealthMateTheme
import com.example.healthmate.feature.records.presentation.HealthMateApp
import com.example.healthmate.feature.records.presentation.HealthViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { HealthMateTheme { HealthMateApp(viewModel()) } }
    }
}
