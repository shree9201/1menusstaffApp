package com.droptechsolution.shared.ui.profile.help

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.droptechsolution.shared.ui.theme.BG_LIGHT
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun HelpSupportScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onReportOperationalIssue: () -> Unit = {},
    onContactItSupport: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BG_LIGHT)
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        SupportScreenHeader(
            title = "Help & Support",
            onBack = onBack,
        )
        Spacer(modifier = Modifier.height(12.dp))
        SupportMenuCard(
            title = "Report Operational Issue",
            subtitle = "Notify admin on workflow/technical issue",
            onClick = onReportOperationalIssue,
        )
        Spacer(modifier = Modifier.height(12.dp))
        SupportMenuCard(
            title = "Contact IT Support",
            subtitle = "Email: ${HelpSupportConfig.IT_SUPPORT_EMAIL}",
            onClick = onContactItSupport,
        )
    }
}

@Preview
@Composable
private fun HelpSupportScreenPreview() {
    HelpSupportScreen()
}
