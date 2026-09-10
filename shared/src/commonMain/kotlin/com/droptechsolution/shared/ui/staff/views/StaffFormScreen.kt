package com.droptechsolution.shared.ui.staff.views

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.droptechsolution.shared.common.ui.GradientButton
import com.droptechsolution.shared.common.ui.MenusKeyValueDropdown
import com.droptechsolution.shared.ui.common.ToastMessageEffect
import com.droptechsolution.shared.ui.profile.help.SupportScreenHeader
import com.droptechsolution.shared.ui.staff.presenter.StaffFormViewModel
import com.droptechsolution.shared.ui.theme.BG_LIGHT
import com.droptechsolution.shared.ui.theme.BG_WHITE
import com.droptechsolution.shared.ui.theme.MenusGradients
import com.droptechsolution.shared.ui.theme.MenusPrimary
import com.droptechsolution.shared.ui.theme.MenusTextStyles
import com.droptechsolution.shared.ui.theme.TextMuted
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun StaffFormScreen(
    staffId: String? = null,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: StaffFormViewModel = koinViewModel(),
) {
    LaunchedEffect(staffId) {
        viewModel.load(staffId)
    }

    LaunchedEffect(viewModel) {
        viewModel.successEvents.collect {
            onBack()
        }
    }

    val uiState by viewModel.uiState.collectAsState()
    val isEdit = staffId != null
    val title = if (isEdit) "Edit Staff" else "Add Staff"

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
                    title = title,
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
                    StaffFormTextField(
                        label = "Full Name",
                        value = uiState.name,
                        onValueChange = viewModel::onNameChanged,
                        placeholder = "Enter full name",
                    )
                    StaffFormTextField(
                        label = "Email Address",
                        value = uiState.email,
                        onValueChange = viewModel::onEmailChanged,
                        placeholder = "Enter email address",
                        keyboardType = KeyboardType.Email,
                    )
                    StaffFormTextField(
                        label = "Mobile Number",
                        value = uiState.mobile,
                        onValueChange = viewModel::onMobileChanged,
                        placeholder = "Enter mobile number",
                        keyboardType = KeyboardType.Phone,
                    )
                    StaffFormTextField(
                        label = "Username",
                        value = uiState.username,
                        onValueChange = viewModel::onUsernameChanged,
                        placeholder = "Enter username",
                    )
                    if (!isEdit) {
                        StaffFormTextField(
                            label = "Password",
                            value = uiState.password,
                            onValueChange = viewModel::onPasswordChanged,
                            placeholder = "Enter password",
                            visualTransformation = PasswordVisualTransformation(),
                        )
                    }

                    Text(
                        text = "Department",
                        style = MenusTextStyles.formLabelDark,
                        modifier = Modifier.padding(bottom = 6.dp),
                    )
                    MenusKeyValueDropdown(
                        options = uiState.departmentOptions,
                        selectedKey = uiState.department,
                        label = "Select department",
                        onKeySelected = viewModel::onDepartmentChanged,
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Type",
                        style = MenusTextStyles.formLabelDark,
                        modifier = Modifier.padding(bottom = 6.dp),
                    )
                    MenusKeyValueDropdown(
                        options = uiState.typeOptions,
                        selectedKey = uiState.type,
                        label = "Select type",
                        onKeySelected = viewModel::onTypeChanged,
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    GradientButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        text = when {
                            uiState.isSaving && isEdit -> "Saving..."
                            uiState.isSaving -> "Adding..."
                            isEdit -> "Save Staff"
                            else -> "Add Staff"
                        },
                        gradient = MenusGradients.appBackground,
                        onClick = {
                            if (!uiState.isSaving && !uiState.isLoading) {
                                viewModel.save()
                            }
                        },
                    )
                }
            }

            if (uiState.isLoading || uiState.isSaving) {
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
private fun StaffFormTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
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
        visualTransformation = visualTransformation,
        textStyle = MenusTextStyles.landingParagraph.copy(
            color = Color.Black,
            fontSize = 14.sp,
        ),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
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
