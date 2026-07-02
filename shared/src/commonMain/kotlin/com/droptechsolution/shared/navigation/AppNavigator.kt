package com.droptechsolution.shared.navigation

import androidx.navigation.NavHostController

/**
 * Single entry point for imperative navigation from ViewModels / UI callbacks.
 * Keeps route strings and popUpTo rules out of composables and ViewModels.
 */
class AppNavigator(
    private val navController: NavHostController,
) {
    fun goToSecureLogin() {
        navController.navigate(SecureLoginRoute)
    }

    fun goToGuide() {
        navController.navigate(GuideRoute)
    }

    fun goToPartner() {
        navController.navigate(PartnerRoute)
    }

    /**
     * Replaces the entire auth backstack with the dashboard.
     * Users cannot navigate back to login after a successful sign-in.
     */
    fun goToDashboardClearingAuth() {
        navController.navigate(DashboardRoute) {
            popUpTo<LandingRoute> { inclusive = true }
            launchSingleTop = true
        }
    }

    fun navigateUp() {
        navController.navigateUp()
    }
}
