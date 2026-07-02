package com.droptechsolution.shared.ui.login

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.droptechsolution.shared.common.ui.GradientButton
import com.droptechsolution.shared.common.ui.MenusDropdown
import com.droptechsolution.shared.ui.theme.BG_LIGHT
import com.droptechsolution.shared.ui.theme.BG_WHITE
import com.droptechsolution.shared.ui.theme.MenusError
import com.droptechsolution.shared.ui.theme.MenusGradients
import com.droptechsolution.shared.ui.theme.MenusPrimary
import com.droptechsolution.shared.ui.theme.MenusTextStyles
import com.droptechsolution.shared.ui.theme.MenusTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

enum class LoginRole(val label: String, val value: String) {
    Staff("Staff", "STAFF"),
    Manager("Manager", "HOD"),
    Hr("HR", "HR")
}

@Composable
fun SecureLoginScreen(
    modifier: Modifier = Modifier,
    //viewModel: LoginViewModel = viewModel(factory = loginViewModelFactory),
     viewModel: LoginViewModel = koinViewModel(),
    onLoginSuccess: () -> Unit = {},
    onSubmit: (
        outletCode: String,
        role: LoginRole,
        username: String,
        password: String,
        autoLogin: Boolean
    ) -> Unit = { _, _, _, _, _ -> }
) {
    val errorMessage by viewModel.errorMessage.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val loginSuccess by viewModel.loginState.collectAsState()

    LaunchedEffect(loginSuccess) {
        if (loginSuccess) {
            onLoginSuccess()
        }
    }

    var outletCodeError by remember { mutableStateOf<String?>(null) }
    var usernameError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    var outletCode by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf(LoginRole.Staff) }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var autoLogin by remember { mutableStateOf(false) }

    MenusTheme {
        Surface(
            modifier = modifier
                .fillMaxSize()
                .background(BG_LIGHT),
            color = BG_LIGHT
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
                    .background(BG_LIGHT),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(BG_WHITE)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Secure Login",
                        style = MenusTextStyles.loginTitleDark,
                        textAlign = TextAlign.Start
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Sign in to continue to your role-specific dashboard.",
                        style = MenusTextStyles.landingParagraphMuted
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    LoginTextField(
                        label = "Outlet Code",
                        value = outletCode,
                        onValueChange = {
                            outletCode = it
                            outletCodeError = null
                        },
                        placeholder = "e.g. 101",
                        keyboardType = KeyboardType.Number,
                        error = outletCodeError
                    )

                    FormLabel("I am")
                    RoleSelector(
                        selectedRole = selectedRole,
                        onRoleSelected = { selectedRole = it }
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    LoginTextField(
                        label = "Login Username",
                        value = username,
                        onValueChange = {
                            username = it
                            usernameError = null
                        },
                        placeholder = "staff / manager / hr",
                        error = usernameError
                    )

                    LoginTextField(
                        label = "Password",
                        value = password,
                        onValueChange = {
                            password = it
                            passwordError = null
                        },
                        placeholder = "Enter password",
                        keyboardType = KeyboardType.Password,
                        isPassword = true,
                        error = passwordError
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp, bottom = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = autoLogin,
                            onCheckedChange = { autoLogin = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = MenusPrimary,
                                uncheckedColor = Color.White.copy(alpha = 0.7f),
                                checkmarkColor = Color.White
                            )
                        )
                        Text(
                            text = "Save / Auto-login",
                            style = MenusTextStyles.formLabel
                        )
                    }
                    GradientButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),

                        text = if (isLoading) "Signing in..." else "Secure login",
                        gradient = MenusGradients.appBackground
                    ) {
//                        onSubmit(outletCode, selectedRole, username, password, autoLogin)
                        outletCodeError =
                            if (outletCode.isBlank()) "Outlet code is required" else null

                        usernameError =
                            if (username.isBlank()) "Username is required" else null

                        passwordError =
                            if (password.isBlank()) "Password is required" else null

                        val hasError = listOf(
                            outletCodeError,
                            usernameError,
                            passwordError
                        ).any { it != null }

                        if (!hasError) {
                            viewModel.login(outletCode, username, password,selectedRole.value)
                            onSubmit(
                                outletCode,
                                selectedRole,
                                username,
                                password,
                                autoLogin
                            )
                        }
                    }

                    if (errorMessage.isNotBlank()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = errorMessage,
                            color = MenusError,
                            style = MenusTextStyles.formLabel
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Examples:\nStaff staff/staff, Manager manager/manager, HR hr/hr",
                        style = MenusTextStyles.landingParagraph
                    )
                }
            }
        }
    }
}

@Composable
private fun LoginTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    error: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false
) {
    FormLabel(label)

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth(),
        value = value,
        onValueChange = onValueChange,
        isError = error != null,
        placeholder = {
            Text(
                text = placeholder,
                color = Color.Gray
            )
        },
        singleLine = true,
        textStyle = MenusTextStyles.landingParagraph.copy(
            color = Color.Black,
            fontSize = 14.sp
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType
        ),
        visualTransformation = if (isPassword) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },
        shape = RoundedCornerShape(10.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            cursorColor = Color.Black,

            focusedBorderColor = if (error != null)
                Color.Red else Color(0xFFD1D5DB),

            unfocusedBorderColor = if (error != null)
                Color.Red else Color(0xFFD1D5DB),

            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        )
    )

    if (error != null) {
        Text(
            text = error,
            color = Color.Red,
            fontSize = 12.sp,
            modifier = Modifier.padding(top = 4.dp, bottom = 10.dp)
        )
    } else {
        Spacer(modifier = Modifier.height(10.dp))
    }
}

@Composable
private fun FormLabel(text: String) {
    Text(
        modifier = Modifier.padding(bottom = 6.dp),
        text = text,
        style = MenusTextStyles.formLabelDark
    )
}

@Composable
private fun RoleSelector(
    selectedRole: LoginRole,
    onRoleSelected: (LoginRole) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val roles = listOf<String>("Staff","Manager","HR")
        MenusDropdown(roles){selected->

        }
    }
}

