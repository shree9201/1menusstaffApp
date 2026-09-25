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

    fun goToHelpSupport() {
        navController.navigate(HelpSupportRoute)
    }

    fun goToReportOperationalIssue() {
        navController.navigate(ReportOperationalIssueRoute)
    }

    fun goToContactItSupport() {
        navController.navigate(ContactItSupportRoute)
    }

    fun goToMyAttendance() {
        navController.navigate(MyAttendanceRoute)
    }

    fun goToChangePassword() {
        navController.navigate(ChangePasswordRoute)
    }

    fun goToUpdateProfile() {
        navController.navigate(UpdateProfileRoute)
    }

    fun goToStaff() = navigateToTab(StaffRoute)

    fun goToServices() = navigateToTab(ServicesRoute)

    fun goToServiceDetail(serviceId: String) {
        navController.navigate(ServiceDetailRoute(serviceId = serviceId))
    }

    fun goToEditService(serviceId: String) {
        navController.navigate(ServiceEditRoute(serviceId = serviceId))
    }

    fun goToStaffDetail(staffId: String) {
        navController.navigate(StaffDetailRoute(staffId = staffId))
    }

    fun goToAddStaff() {
        navController.navigate(StaffAddRoute)
    }

    fun goToEditStaff(staffId: String) {
        navController.navigate(StaffEditRoute(staffId = staffId))
    }

    fun goToActivityList() {
        navController.navigate(ActivityListRoute)
    }

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
