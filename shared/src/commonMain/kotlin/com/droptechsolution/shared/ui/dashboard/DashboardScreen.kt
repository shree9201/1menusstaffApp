package com.droptechsolution.shared.ui.dashboard

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.droptechsolution.shared.navigation.DashboardNavigator
import com.droptechsolution.shared.navigation.HomeRoute
import com.droptechsolution.shared.navigation.ProfileRoute
import com.droptechsolution.shared.navigation.SettingsRoute
import com.droptechsolution.shared.navigation.TaskDetailRoute
import com.droptechsolution.shared.navigation.TasksRoute
import com.droptechsolution.shared.navigation.toRequestSource
import com.droptechsolution.shared.services.views.RequestDetailsScreen
import com.droptechsolution.shared.ui.home.views.HomeScreen
import com.droptechsolution.shared.ui.profile.ProfileScreen
import com.droptechsolution.shared.ui.settings.SettingsScreen
import com.droptechsolution.shared.ui.tasks.views.TasksScreen
import com.droptechsolution.shared.ui.theme.MenusTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    val dashboardNavigator = remember(navController) { DashboardNavigator(navController) }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val selectedTab = when {
        currentDestination?.hasRoute(TasksRoute::class) == true -> DashboardTab.Tasks
        currentDestination?.hasRoute(ProfileRoute::class) == true -> DashboardTab.Profile
        currentDestination?.hasRoute(SettingsRoute::class) == true -> DashboardTab.Settings
        else -> DashboardTab.Home
    }

    val showBottomBar = currentDestination?.hasRoute(TaskDetailRoute::class) != true

    MenusTheme {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            bottomBar = {
                if (showBottomBar) {
                    DashboardBottomBar(
                        selectedTab = selectedTab,
                        onTabSelected = { tab ->
                            when (tab) {
                                DashboardTab.Home -> dashboardNavigator.goToFeed()
                                DashboardTab.Tasks -> dashboardNavigator.goToTasks()
                                DashboardTab.Profile -> dashboardNavigator.goToProfile()
                                DashboardTab.Settings -> dashboardNavigator.goToSettings()
                            }
                        },
                    )
                }
            },
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = HomeRoute,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            ) {
                composable<HomeRoute> {
                    HomeScreen(
                        modifier = Modifier.fillMaxSize(),
                        onViewAllTasks = dashboardNavigator::goToTasks,
                        onTaskClick = dashboardNavigator::goToTaskDetail,
                    )
                }
                composable<TasksRoute> {
                    TasksScreen(
                        modifier = Modifier.fillMaxSize(),
                        onTaskClick = dashboardNavigator::goToTaskDetail,
                    )
                }
                composable<ProfileRoute> {
                    ProfileScreen()
                }
                composable<SettingsRoute> {
                    SettingsScreen()
                }
                composable<TaskDetailRoute> { backStackEntry ->
                    val route = backStackEntry.toRoute<TaskDetailRoute>()
                    RequestDetailsScreen(
                        requestId = route.requestId,
                        source = route.toRequestSource(),
                        onBack = dashboardNavigator::navigateUp,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun DashboardScreenPreview() {
    DashboardScreen()
}
