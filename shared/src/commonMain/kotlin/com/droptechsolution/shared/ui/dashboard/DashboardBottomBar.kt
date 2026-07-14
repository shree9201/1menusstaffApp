package com.droptechsolution.shared.ui.dashboard

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.droptechsolution.shared.ui.common.user.UserSession
import com.droptechsolution.shared.ui.theme.MenusPrimary

enum class DashboardTab(
    val label: String,
    val icon: ImageVector,
) {
    Home("Home", Icons.Default.Home),
    Tasks("Tasks", Icons.Default.Assignment),
    Staff("Staff", Icons.Default.Groups),
    Profile("Profile", Icons.Default.Person),
}

fun dashboardTabsFor(session: UserSession?): List<DashboardTab> {
    val tabs = mutableListOf(DashboardTab.Home, DashboardTab.Tasks)
    if (session?.staffType?.showsStaffTab() == true) {
        tabs.add(DashboardTab.Staff)
    }
    tabs.add(DashboardTab.Profile)
    return tabs
}

@Composable
fun DashboardBottomBar(
    selectedTab: DashboardTab,
    visibleTabs: List<DashboardTab>,
    onTabSelected: (DashboardTab) -> Unit,
) {
    NavigationBar(
        containerColor = MenusPrimary,
        tonalElevation = 0.dp,
    ) {
        visibleTabs.forEach { tab ->
            val selected = selectedTab == tab
            NavigationBarItem(
                selected = selected,
                onClick = { onTabSelected(tab) },
                icon = {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.label,
                    )
                },
                label = {
                    Text(
                        text = tab.label,
                        fontSize = 11.sp,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    selectedTextColor = Color.White,
                    unselectedIconColor = Color.White.copy(alpha = 0.72f),
                    unselectedTextColor = Color.White.copy(alpha = 0.72f),
                    indicatorColor = Color.White.copy(alpha = 0.18f),
                ),
            )
        }
    }
}
