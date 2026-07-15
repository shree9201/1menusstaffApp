package com.droptechsolution.shared.ui.profile.help

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
fun ReportOperationalIssueScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
) {
    var issueDescription by rememberSaveable { mutableStateOf("") }
    var showSubmittedDialog by rememberSaveable { mutableStateOf(false) }

    if (showSubmittedDialog) {
        AlertDialog(
            onDismissRequest = { showSubmittedDialog = false },
            title = {
                Text(
                    text = "Issue Reported",
                    color = BLACK,
                    fontWeight = FontWeight.Bold,
                )
            },
            text = {
                Text(
                    text = "Your operational issue has been sent to the admin team.",
                    color = TextMuted,
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSubmittedDialog = false
                        onBack()
                    },
                ) {
                    Text(text = "OK", color = MenusPrimary)
                }
            },
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BG_LIGHT)
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        SupportScreenHeader(
            title = "Report Operational Issue",
            onBack = onBack,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Describe the workflow or technical issue so the admin team can investigate.",
            color = TextMuted,
            fontSize = 14.sp,
            modifier = Modifier.padding(horizontal = 8.dp),
        )
        Spacer(modifier = Modifier.height(20.dp))
        OutlinedTextField(
            value = issueDescription,
            onValueChange = { issueDescription = it },
            modifier = Modifier
                .fillMaxWidth()
                .background(BG_WHITE, RoundedCornerShape(16.dp)),
            placeholder = {
                Text(
                    text = "Describe the issue...",
                    color = TextMuted,
                )
            },
            minLines = 6,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MenusPrimary,
                unfocusedBorderColor = TextMuted.copy(alpha = 0.3f),
                focusedTextColor = BLACK,
                unfocusedTextColor = BLACK,
            ),
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = { showSubmittedDialog = true },
            enabled = issueDescription.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MenusPrimary,
            ),
        ) {
            Text(
                text = "Submit to Admin",
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(vertical = 4.dp),
            )
        }
    }
}

@Preview
@Composable
private fun ReportOperationalIssueScreenPreview() {
    ReportOperationalIssueScreen()
}
