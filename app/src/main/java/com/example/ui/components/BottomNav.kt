package com.example.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun MindCoachBottomNav(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    NavigationBar(modifier = Modifier.fillMaxWidth()) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Dashboard") },
            label = { Text("Dashboard") },
            selected = currentRoute == "DashboardRoute",
            onClick = { onNavigate("DashboardRoute") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.TrackChanges, contentDescription = "Goals") },
            label = { Text("Goals") },
            selected = currentRoute == "GoalsRoute",
            onClick = { onNavigate("GoalsRoute") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Analytics, contentDescription = "Insights") },
            label = { Text("Insights") },
            selected = currentRoute == "InsightsRoute",
            onClick = { onNavigate("InsightsRoute") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = "AI Coach") },
            label = { Text("AI Coach") },
            selected = currentRoute == "AICoachRoute",
            onClick = { onNavigate("AICoachRoute") }
        )
    }
}
