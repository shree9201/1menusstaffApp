package com.droptechsolution.shared.ui.login

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.droptechsolution.shared.constants.strings.dimens.SUBLINE_DIVIDER
import com.droptechsolution.shared.constants.strings.strings.BECOME_PARTNER_BUTTON
import com.droptechsolution.shared.constants.strings.strings.BODY_TITLE
import com.droptechsolution.shared.constants.strings.strings.BULLET_1
import com.droptechsolution.shared.constants.strings.strings.COPYRIGHT
import com.droptechsolution.shared.constants.strings.strings.GUIDE_BUTTON
import com.droptechsolution.shared.constants.strings.strings.LOGIN_BUTTON
import com.droptechsolution.shared.constants.strings.strings.LOGIN_SUBTITLE
import com.droptechsolution.shared.constants.strings.strings.LOGIN_TITLE
import com.droptechsolution.shared.constants.strings.strings.Menus_DESCRIPTION
import com.droptechsolution.shared.ui.theme.MenusGradients
import com.droptechsolution.shared.ui.theme.MenusTextStyles
import com.droptechsolution.shared.ui.theme.MenusTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onLoginClick: () -> Unit = {},
    onGuideClick: () -> Unit = {},
    onPartnerClick: () -> Unit = {},
    onLoginSuccess : () -> Unit = {},
    viewModel: LoginViewModel = koinViewModel(),
) {
    viewModel.checkUserInfo()
    val isLoading by viewModel._isLoggedIn.collectAsState()

    LaunchedEffect(isLoading) {
        if (isLoading) {
            onLoginSuccess()
        }
    }

    MenusTheme {
        Surface(
            modifier = modifier
                .fillMaxSize()
                .background(MenusGradients.appBackground),
            color = Color.Transparent
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 36.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                BrandHeader()

                Spacer(modifier = Modifier.height(20.dp))

                LandingPanel(
                    onLoginClick = onLoginClick,
                    onGuideClick = onGuideClick,
                    onPartnerClick = onPartnerClick
                )
            }
        }
    }
}

@Composable
private fun BrandHeader() {
    Text(
        modifier = Modifier.fillMaxWidth(),
        text = LOGIN_TITLE,
        style = MenusTextStyles.landingTitle
    )

    Spacer(modifier = Modifier.height(SUBLINE_DIVIDER))
    Text(
        modifier = Modifier.fillMaxWidth(),
        text = LOGIN_SUBTITLE,
        style = MenusTextStyles.landingCenterParagraph
    )
}

@Composable
private fun LandingPanel(
    onLoginClick: () -> Unit,
    onGuideClick: () -> Unit,
    onPartnerClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(MenusGradients.contentPanel)
            .padding(horizontal = 24.dp, vertical = 10.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = BODY_TITLE,
                style = MenusTextStyles.landingTitle,
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = Menus_DESCRIPTION,
                style = MenusTextStyles.landingParagraph,
                lineHeight = 26.sp
            )

            Spacer(modifier = Modifier.height(18.dp))

            BulletText(BULLET_1)
            BulletText("Role-based dashboards for Staff, Manager,\nand HR.")
            BulletText("Training, performance, and support\nworkflows in one app.")
            BulletText("Consistent operations with standardized\nservice guidelines.")

            Spacer(modifier = Modifier.height(22.dp))

            LandingActionButton(
                text = LOGIN_BUTTON,
                onClick = onLoginClick
            )
            LandingActionButton(
                text = GUIDE_BUTTON,
                onClick = onGuideClick
            )
            LandingActionButton(
                text = BECOME_PARTNER_BUTTON,
                onClick = onPartnerClick
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = COPYRIGHT,
                style = MenusTextStyles.landingCenterParagraph
            )
        }
    }
}

@Composable
private fun BulletText(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
    ) {
        Text(
            text = "•",
            color = Color.White,
            fontSize = 18.sp,
            lineHeight = 26.sp
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            modifier = Modifier.weight(1f),
            text = text,
            style = MenusTextStyles.landingParagraph,
            lineHeight = 26.sp
        )
    }
}

@Composable
private fun LandingActionButton(
    text: String,
    onClick: () -> Unit
) {
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .height(48.dp),
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White.copy(alpha = 0.14f),
            contentColor = Color.White
        ),
        border = BorderStroke(
            width = 1.dp,
            color = Color.White.copy(alpha = 0.28f)
        )
    ) {
        Text(
            text = text,
            style = MenusTextStyles.landingParagraph,
            fontWeight = FontWeight.ExtraBold
        )
    }
}


@Preview
@Composable
fun LoginScreenPreview() {
    LoginScreen()
}
