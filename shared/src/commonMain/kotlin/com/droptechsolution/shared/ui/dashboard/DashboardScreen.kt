package com.droptechsolution.shared.ui.dashboard

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import org.koin.compose.viewmodel.koinViewModel
import com.droptechsolution.shared.ui.common.user.LocalUserSession
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.droptechsolution.shared.navigation.DashboardNavigator
import com.droptechsolution.shared.navigation.HomeRoute
import com.droptechsolution.shared.navigation.ProfileRoute
import com.droptechsolution.shared.navigation.StaffRoute
import com.droptechsolution.shared.navigation.TaskDetailRoute
import com.droptechsolution.shared.navigation.TasksRoute
import com.droptechsolution.shared.navigation.toRequestSource
import com.droptechsolution.shared.services.views.RequestDetailsScreen
import com.droptechsolution.shared.ui.dashboard.presenter.DashboardViewModel
import com.droptechsolution.shared.ui.home.views.HomeScreen
import com.droptechsolution.shared.ui.profile.ProfileScreen
import com.droptechsolution.shared.ui.staff.views.StaffScreen
import com.droptechsolution.shared.ui.tasks.views.TasksScreen
import com.droptechsolution.shared.ui.theme.MenusTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier,
    onLogout: () -> Unit = {},
    viewModel: DashboardViewModel = koinViewModel(),
) {
    LaunchedEffect(Unit) {
        viewModel.loadSession()
    }

    val userSession by viewModel.userSession.collectAsState()
    val navController = rememberNavController()
    val dashboardNavigator = remember(navController) { DashboardNavigator(navController) }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val visibleTabs = remember(userSession) { dashboardTabsFor(userSession) }

    val selectedTab = when {
        currentDestination?.hasRoute(TasksRoute::class) == true -> DashboardTab.Tasks
        currentDestination?.hasRoute(StaffRoute::class) == true -> DashboardTab.Staff
        currentDestination?.hasRoute(ProfileRoute::class) == true -> DashboardTab.Profile
        else -> DashboardTab.Home
    }

    val showBottomBar = currentDestination?.hasRoute(TaskDetailRoute::class) != true

    MenusTheme {
        CompositionLocalProvider(LocalUserSession provides userSession) {
            Scaffold(
            modifier = modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            bottomBar = {
                if (showBottomBar) {
                    DashboardBottomBar(
                        selectedTab = selectedTab,
                        visibleTabs = visibleTabs,
                        onTabSelected = { tab ->
                            when (tab) {
                                DashboardTab.Home -> dashboardNavigator.goToFeed()
                                DashboardTab.Tasks -> dashboardNavigator.goToTasks()
                                DashboardTab.Staff -> dashboardNavigator.goToStaff()
                                DashboardTab.Profile -> dashboardNavigator.goToProfile()
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
                        onViewAllTasks = { dashboardNavigator.goToTasks() },
                        onOverviewClick = dashboardNavigator::goToTasksFromOverview,
                        onTaskClick = dashboardNavigator::goToTaskDetail,
                    )
                }
                composable<TasksRoute> { backStackEntry ->
                    val route = backStackEntry.toRoute<TasksRoute>()
                    TasksScreen(
                        modifier = Modifier.fillMaxSize(),
                        statusFilter = route.statusFilter,
                        overviewCategory = route.overviewCategory,
                        onTaskClick = dashboardNavigator::goToTaskDetail,
                        viewModelStoreOwner = backStackEntry,
                    )
                }
                composable<StaffRoute> {
                    StaffScreen(modifier = Modifier.fillMaxSize())
                }
                composable<ProfileRoute> {
                    ProfileScreen(
                        modifier = Modifier.fillMaxSize(),
                        onLogout = onLogout,
                    )
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
}

@Preview
@Composable
private fun DashboardScreenPreview() {
    DashboardScreen()
}
