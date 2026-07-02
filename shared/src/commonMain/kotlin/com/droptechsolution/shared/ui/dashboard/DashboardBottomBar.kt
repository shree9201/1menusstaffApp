package com.droptechsolution.shared.ui.dashboard

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.droptechsolution.shared.ui.theme.MenusPrimary

enum class DashboardTab(
    val label: String,
) {
    Feed("Feed"),
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
        contentColor = MenusPrimary,
    ) {
        NavigationBarItem(
            selected = selectedTab == DashboardTab.Feed,
            onClick = { onTabSelected(DashboardTab.Feed) },
            icon = { Icon(Icons.Default.Home, contentDescription = DashboardTab.Feed.label) },
            label = { Text(DashboardTab.Feed.label) },
        )
        NavigationBarItem(
            selected = selectedTab == DashboardTab.Profile,
            onClick = { onTabSelected(DashboardTab.Profile) },
            icon = { Icon(Icons.Default.Person, contentDescription = DashboardTab.Profile.label) },
            label = { Text(DashboardTab.Profile.label) },
        )
        NavigationBarItem(
            selected = selectedTab == DashboardTab.Settings,
            onClick = { onTabSelected(DashboardTab.Settings) },
            icon = { Icon(Icons.Default.Settings, contentDescription = DashboardTab.Settings.label) },
            label = { Text(DashboardTab.Settings.label) },
        )
    }
}
