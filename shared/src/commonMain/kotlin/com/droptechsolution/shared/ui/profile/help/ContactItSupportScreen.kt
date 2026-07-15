package com.droptechsolution.shared.ui.profile.help

import androidx.compose.foundation.background
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.droptechsolution.shared.ui.theme.BG_LIGHT
import com.droptechsolution.shared.ui.theme.BG_WHITE
import com.droptechsolution.shared.ui.theme.BLACK
import com.droptechsolution.shared.ui.theme.MenusPrimary
import com.droptechsolution.shared.ui.theme.TextMuted
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ContactItSupportScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
) {
    val uriHandler = LocalUriHandler.current
    val supportEmail = HelpSupportConfig.IT_SUPPORT_EMAIL

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BG_LIGHT)
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        SupportScreenHeader(
            title = "Contact IT Support",
            onBack = onBack,
        )
        Spacer(modifier = Modifier.height(20.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(BG_WHITE, RoundedCornerShape(16.dp))
                .padding(20.dp),
        ) {
            Text(
                text = "Email Support",
                color = TextMuted,
                fontSize = 13.sp,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = supportEmail,
                color = BLACK,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Reach out for technical assistance with the 1Menus app or account access.",
                color = TextMuted,
                fontSize = 14.sp,
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = { uriHandler.openUri("mailto:$supportEmail") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MenusPrimary,
            ),
        ) {
            Text(
                text = "Send Email",
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(vertical = 4.dp),
            )
        }
    }
}

@Preview
@Composable
private fun ContactItSupportScreenPreview() {
    ContactItSupportScreen()
}
