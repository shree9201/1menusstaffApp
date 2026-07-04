package com.droptechsolution.shared.navigation

import androidx.navigation.NavHostController
import com.droptechsolution.shared.services.models.RequestSource
import com.droptechsolution.shared.services.models.ServiceRequestRowUi

/**
 * Navigator scoped to the dashboard nested graph (bottom-bar tabs).
 */
class DashboardNavigator(
    private val navController: NavHostController,
) {
    fun goToFeed() = navigateToTab(HomeRoute)

    fun goToTasks() = navigateToTab(TasksRoute)

    fun goToProfile() = navigateToTab(ProfileRoute)

    fun goToSettings() = navigateToTab(SettingsRoute)

    fun goToTaskDetail(task: ServiceRequestRowUi) {
        navController.navigate(
            TaskDetailRoute(
                requestId = task.id,
                source = task.source.name,
            ),
        )
    }

    fun navigateUp() {
        navController.navigateUp()
    }

    private inline fun <reified T : Any> navigateToTab(route: T) {
        navController.navigate(route) {
            popUpTo<HomeRoute> { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }
}

fun TaskDetailRoute.toRequestSource(): RequestSource =
    RequestSource.entries.firstOrNull { it.name == source } ?: RequestSource.ROOM
