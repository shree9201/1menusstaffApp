package com.droptechsolution.shared.navigation

import androidx.navigation.NavHostController
import com.droptechsolution.shared.services.models.DepartmentOverviewStatUi
import com.droptechsolution.shared.services.models.RequestSource
import com.droptechsolution.shared.services.models.ServiceRequestRowUi
import com.droptechsolution.shared.services.models.apiKey

/**
 * Navigator scoped to the dashboard nested graph (bottom-bar tabs).
 */
class DashboardNavigator(
    private val navController: NavHostController,
) {
    fun goToFeed() = navigateToTab(HomeRoute)

    fun goToTasks(
        statusFilter: String? = null,
        overviewCategory: String? = null,
    ) {
        navController.navigate(
            TasksRoute(
                statusFilter = statusFilter,
                overviewCategory = overviewCategory,
            ),
        ) {
            popUpTo<HomeRoute> { saveState = true }
            launchSingleTop = statusFilter == null && overviewCategory == null
            restoreState = statusFilter == null && overviewCategory == null
        }
    }

    fun goToTasksFromOverview(stat: DepartmentOverviewStatUi) {
        goToTasks(
            overviewCategory = stat.category.apiKey(),
        )
    }

    fun goToProfile() = navigateToTab(ProfileRoute)

    fun goToSettings() = navigateToTab(SettingsRoute)

    fun goToTaskDetail(task: ServiceRequestRowUi) {
        navController.navigate(
            TaskDetailRoute(
                requestId = task.id,
                source = task.source.name
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
