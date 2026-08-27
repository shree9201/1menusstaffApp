package com.droptechsolution.shared.ui.profile.changepassword.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.droptechsolution.shared.common.ui.GradientButton
import com.droptechsolution.shared.ui.common.ToastMessageEffect
import com.droptechsolution.shared.ui.profile.changepassword.presenter.ChangePasswordViewModel
import com.droptechsolution.shared.ui.profile.help.SupportScreenHeader
import com.droptechsolution.shared.ui.theme.BG_LIGHT
import com.droptechsolution.shared.ui.theme.BG_WHITE
import com.droptechsolution.shared.ui.theme.MenusGradients
import com.droptechsolution.shared.ui.theme.MenusPrimary
import com.droptechsolution.shared.ui.theme.MenusTextStyles
import com.droptechsolution.shared.ui.theme.TextMuted
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ChangePasswordScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    viewModel: ChangePasswordViewModel = koinViewModel(),
) {
    val isLoading by viewModel.isLoading.collectAsState()

    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    LaunchedEffect(viewModel) {
        viewModel.successEvents.collect {
            currentPassword = ""
            newPassword = ""
            confirmPassword = ""
            onBack()
        }
    }

    ToastMessageEffect(messages = viewModel.toastEvents) {
        Box(modifier = modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BG_LIGHT)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                SupportScreenHeader(
                    title = "Change Password",
                    onBack = onBack,
                )
                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(BG_WHITE)
                        .padding(20.dp),
                ) {
                    PasswordField(
                        label = "Current Password",
                        value = currentPassword,
                        onValueChange = { currentPassword = it },
                        placeholder = "Enter current password",
                    )
                    PasswordField(
                        label = "New Password",
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        placeholder = "Enter new password",
                    )
                    PasswordField(
                        label = "Confirm New Password",
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        placeholder = "Re-enter new password",
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    GradientButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        text = if (isLoading) "Updating..." else "Update Password",
                        gradient = MenusGradients.appBackground,
                        onClick = {
                            if (!isLoading) {
                                viewModel.changePassword(
                                    currentPassword = currentPassword,
                                    newPassword = newPassword,
                                    confirmPassword = confirmPassword,
                                )
                            }
                        },
                    )
                }
            }

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = MenusPrimary)
                }
            }
        }
    }
}

@Composable
private fun PasswordField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
) {
    Text(
        text = label,
        style = MenusTextStyles.formLabelDark,
        modifier = Modifier.padding(bottom = 6.dp),
    )
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(text = placeholder, color = TextMuted, fontSize = 14.sp)
        },
        singleLine = true,
        textStyle = MenusTextStyles.landingParagraph.copy(
            color = Color.Black,
            fontSize = 14.sp,
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        visualTransformation = PasswordVisualTransformation(),
        shape = RoundedCornerShape(10.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            cursorColor = Color.Black,
            focusedBorderColor = Color(0xFFD1D5DB),
            unfocusedBorderColor = Color(0xFFD1D5DB),
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
        ),
    )
}

@Preview
@Composable
private fun ChangePasswordScreenPreview() {
    ChangePasswordScreen()
}
