package com.example.healthmate.feature.common.presentation

import com.example.healthmate.app.navigation.Routes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable fun Input(value: String, change: (String) -> Unit, label: String, type: KeyboardType = KeyboardType.Text) = OutlinedTextField(value, change, label = { Text(label) }, keyboardOptions = KeyboardOptions(keyboardType = type), singleLine = true, modifier = Modifier.fillMaxWidth())

@Composable fun SettingItem(title: String, subtitle: String, icon: ImageVector, color: Color, click: () -> Unit) = Card(Modifier.fillMaxWidth().clickable(onClick = click)) { Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) { Icon(icon, null, tint = color, modifier = Modifier.size(30.dp)); Spacer(Modifier.width(16.dp)); Column(Modifier.weight(1f)) { Text(title, fontWeight = FontWeight.Bold); Text(subtitle, style = MaterialTheme.typography.bodySmall) }; Icon(Icons.Filled.ChevronRight, null) } }

@Composable fun BackBar(title: String, back: () -> Unit) = TopAppBar(title = { Text(title) }, navigationIcon = { IconButton(back) { Icon(Icons.Filled.ArrowBack, "Back") } })

@Composable fun Shell(nav: androidx.navigation.NavHostController, selected: Int, content: @Composable () -> Unit) = Scaffold(bottomBar = { NavigationBar { NavigationBarItem(selected = selected == 0, onClick = { nav.navigate(Routes.HOME) { launchSingleTop = true } }, icon = { Icon(Icons.Filled.Home, null) }, label = { Text("Home") }); NavigationBarItem(selected = selected == 1, onClick = { nav.navigate(Routes.HISTORY) { launchSingleTop = true } }, icon = { Icon(Icons.Filled.History, null) }, label = { Text("History") }); NavigationBarItem(selected = selected == 2, onClick = { nav.navigate("${Routes.FORM}?id=0") { launchSingleTop = true } }, icon = { Icon(Icons.Filled.AddCircle, null) }, label = { Text("Add") }); NavigationBarItem(selected = selected == 3, onClick = { nav.navigate(Routes.SETTINGS) { launchSingleTop = true } }, icon = { Icon(Icons.Filled.Settings, null) }, label = { Text("Settings") }) } }) { padding -> Box(Modifier.padding(padding)) { content() } }
