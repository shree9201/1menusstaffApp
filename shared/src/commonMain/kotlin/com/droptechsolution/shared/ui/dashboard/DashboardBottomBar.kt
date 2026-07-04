package com.droptechsolution.shared.ui.dashboard

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.droptechsolution.shared.ui.theme.MenusPrimary

enum class DashboardTab(
    val label: String,
) {
    Home("Home"),
    Tasks("Tasks"),
    Profile("Profile"),
    Settings("Settings"),
}

@Composable
fun DashboardBottomBar(
    selectedTab: DashboardTab,
    onTabSelected: (DashboardTab) -> Unit,
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 0.dp,
        windowInsets = WindowInsets(0, 0, 0, 0),
    ) {
        NavigationBarItem(
            selected = selectedTab == DashboardTab.Home,
            onClick = { onTabSelected(DashboardTab.Home) },
            icon = {
                Icon(
                    Icons.Default.Home,
                    contentDescription = DashboardTab.Home.label,
                )
            },
        )
        NavigationBarItem(
            selected = selectedTab == DashboardTab.Tasks,
            onClick = { onTabSelected(DashboardTab.Tasks) },
            icon = {
                Icon(
                    Icons.Default.Assignment,
                    contentDescription = DashboardTab.Tasks.label,
                    tint = if (selectedTab == DashboardTab.Tasks) MenusPrimary else Color.Unspecified,
                )
            },
        )
        NavigationBarItem(
            selected = selectedTab == DashboardTab.Profile,
            onClick = { onTabSelected(DashboardTab.Profile) },
            icon = {
                Icon(
                    Icons.Default.Person,
                    contentDescription = DashboardTab.Profile.label,
                )
            },
        )
        NavigationBarItem(
            selected = selectedTab == DashboardTab.Settings,
            onClick = { onTabSelected(DashboardTab.Settings) },
            icon = {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = DashboardTab.Settings.label,
                )
            },
        )
    }
}
