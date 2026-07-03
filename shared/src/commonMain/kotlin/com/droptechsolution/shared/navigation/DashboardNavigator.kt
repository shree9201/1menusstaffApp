package com.droptechsolution.shared.navigation

import androidx.navigation.NavHostController

/**
 * Navigator scoped to the dashboard nested graph (bottom-bar tabs).
 */
class DashboardNavigator(
    private val navController: NavHostController,
) {
    fun goToFeed() = navigateToTab(HomeRoute)

    fun goToProfile() = navigateToTab(ProfileRoute)

    fun goToSettings() = navigateToTab(SettingsRoute)

    private inline fun <reified T : Any> navigateToTab(route: T) {
        navController.navigate(route) {
            popUpTo<HomeRoute> { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }
}
