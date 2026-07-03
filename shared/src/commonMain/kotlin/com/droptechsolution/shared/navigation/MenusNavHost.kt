package com.droptechsolution.shared.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.droptechsolution.shared.ui.common.PlaceholderScreen
import com.droptechsolution.shared.ui.dashboard.DashboardScreen
import com.droptechsolution.shared.ui.login.LoginScreen
import com.droptechsolution.shared.ui.login.SecureLoginScreen
import com.droptechsolution.shared.ui.theme.MenusTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Root navigation host for the single-activity / single-controller pattern.
 *
 * Graph layout:
 * ```
 * Landing ──► SecureLogin ──► Dashboard (nested bottom-nav graph)
 *    ├── Guide
 *    └── Partner
 * ```
 *
 * On successful login, [AppNavigator.goToDashboardClearingAuth] pops the entire auth
 * stack so the user cannot return to login via back navigation.
 */
@Composable
fun MenusNavHost(
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    val appNavigator = remember(navController) { AppNavigator(navController) }



    MenusTheme {
        NavHost(
            navController = navController,
            startDestination = LandingRoute,
            modifier = modifier,
        ) {
            composable<LandingRoute> {
                LoginScreen(
                    onLoginClick = appNavigator::goToSecureLogin,
                    onGuideClick = appNavigator::goToGuide,
                    onPartnerClick = appNavigator::goToPartner,
                    onLoginSuccess = appNavigator::goToDashboardClearingAuth
                )
            }

            composable<SecureLoginRoute> {
                SecureLoginScreen(
                    onLoginSuccess = appNavigator::goToDashboardClearingAuth,
                )
            }

            composable<GuideRoute> {
                PlaceholderScreen(
                    title = "Guide",
                    body = "Add onboarding, help, and product walkthrough content here.",
                    primaryAction = "Back to home",
                    onPrimaryAction = appNavigator::navigateUp,
                )
            }

            composable<PartnerRoute> {
                PlaceholderScreen(
                    title = "Become a Partner",
                    body = "Add partner enquiry and contact details here.",
                    primaryAction = "Back to home",
                    onPrimaryAction = appNavigator::navigateUp,
                )
            }

            composable<DashboardRoute> {
                DashboardScreen()
            }
        }
    }
}

@Preview
@Composable
private fun MenusNavHostPreview() {
    MenusNavHost()
}
