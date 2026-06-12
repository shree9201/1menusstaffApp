package com.droptechsolution.shared.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.droptechsolution.shared.ui.login.LoginScreen
import com.droptechsolution.shared.ui.theme.MenusGradients
import com.droptechsolution.shared.ui.theme.MenusTextStyles
import com.droptechsolution.shared.ui.theme.MenusTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

object MenusRoutes {
    const val Landing = "landing"
    const val Login = "login"
    const val Guide = "guide"
    const val Partner = "partner"
}

@Composable
fun MenusNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = MenusRoutes.Landing
    ) {
        composable(MenusRoutes.Landing) {
            LoginScreen(
                onLoginClick = { navController.navigateSingleTop(MenusRoutes.Login) },
                onGuideClick = { navController.navigateSingleTop(MenusRoutes.Guide) },
                onPartnerClick = { navController.navigateSingleTop(MenusRoutes.Partner) }
            )
        }

        composable(MenusRoutes.Login) {
            NavigationPlaceholderScreen(
                title = "Login",
                body = "The shared navigation graph is ready for the login form screen.",
                primaryAction = "Back to home",
                onPrimaryAction = { navController.popBackStack() }
            )
        }

        composable(MenusRoutes.Guide) {
            NavigationPlaceholderScreen(
                title = "Guide",
                body = "Add onboarding, help, and product walkthrough content here.",
                primaryAction = "Back to home",
                onPrimaryAction = { navController.popBackStack() }
            )
        }

        composable(MenusRoutes.Partner) {
            NavigationPlaceholderScreen(
                title = "Become a Partner",
                body = "Add partner enquiry and contact details here.",
                primaryAction = "Back to home",
                onPrimaryAction = { navController.popBackStack() }
            )
        }
    }
}

private fun NavHostController.navigateSingleTop(route: String) {
    navigate(route) {
        launchSingleTop = true
    }
}

@Composable
private fun NavigationPlaceholderScreen(
    title: String,
    body: String,
    primaryAction: String,
    onPrimaryAction: () -> Unit
) {
    MenusTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MenusGradients.appBackground)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(MenusGradients.contentPanel)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = title,
                    style = MenusTextStyles.landingTitle
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = body,
                    style = MenusTextStyles.landingCenterParagraph,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onPrimaryAction,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White.copy(alpha = 0.14f),
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = primaryAction,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                TextButton(onClick = onPrimaryAction) {
                    Text(
                        text = "Back",
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun MenusNavHostPreview() {
    MenusNavHost()
}
