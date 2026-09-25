package com.droptechsolution.shared.ui.servicecatalog.views

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.droptechsolution.shared.common.ui.GradientButton
import com.droptechsolution.shared.common.ui.MenusKeyValueDropdown
import com.droptechsolution.shared.ui.common.ToastMessageEffect
import com.droptechsolution.shared.ui.profile.help.SupportScreenHeader
import com.droptechsolution.shared.ui.servicecatalog.presenter.ServiceCatalogFormViewModel
import com.droptechsolution.shared.ui.theme.BG_LIGHT
import com.droptechsolution.shared.ui.theme.BG_WHITE
import com.droptechsolution.shared.ui.theme.MenusGradients
import com.droptechsolution.shared.ui.theme.MenusPrimary
import com.droptechsolution.shared.ui.theme.MenusTextStyles
import com.droptechsolution.shared.ui.theme.TextMuted
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ServiceCatalogFormScreen(
    serviceId: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ServiceCatalogFormViewModel = koinViewModel(),
) {
    LaunchedEffect(serviceId) {
        viewModel.load(serviceId)
    }

    LaunchedEffect(viewModel) {
        viewModel.successEvents.collect {
            onBack()
        }
    }

    val uiState by viewModel.uiState.collectAsState()

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
                    title = "Edit Service",
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
                    ServiceFormTextField(
                        label = "Title",
                        value = uiState.title,
                        onValueChange = viewModel::onTitleChanged,
                        placeholder = "Enter service title",
                    )
                    ServiceFormTextField(
                        label = "Information",
                        value = uiState.information,
                        onValueChange = viewModel::onInformationChanged,
                        placeholder = "Enter information",
                    )
                    ServiceFormTextField(
                        label = "Box ID",
                        value = uiState.boxId,
                        onValueChange = viewModel::onBoxIdChanged,
                        placeholder = "Enter box ID",
                    )

                    Text(
                        text = "Action by",
                        style = MenusTextStyles.formLabelDark,
                        modifier = Modifier.padding(bottom = 6.dp),
                    )
                    MenusKeyValueDropdown(
                        options = uiState.actionByOptions,
                        selectedKey = uiState.actionBy,
                        label = "Select action by",
                        onKeySelected = viewModel::onActionByChanged,
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Ask date & time",
                        style = MenusTextStyles.formLabelDark,
                        modifier = Modifier.padding(bottom = 6.dp),
                    )
                    MenusKeyValueDropdown(
                        options = viewModel.toggleOptions,
                        selectedKey = uiState.aksDateTime,
                        label = "Select option",
                        onKeySelected = viewModel::onAksDateTimeChanged,
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    ServiceFormTextField(
                        label = "Reminder time (minutes)",
                        value = uiState.reminderTime,
                        onValueChange = viewModel::onReminderTimeChanged,
                        placeholder = "0",
                        keyboardType = KeyboardType.Number,
                    )
                    ServiceFormTextField(
                        label = "Escalation time (minutes)",
                        value = uiState.escalationTime,
                        onValueChange = viewModel::onEscalationTimeChanged,
                        placeholder = "0",
                        keyboardType = KeyboardType.Number,
                    )

                    Text(
                        text = "Priority",
                        style = MenusTextStyles.formLabelDark,
                        modifier = Modifier.padding(bottom = 6.dp),
                    )
                    MenusKeyValueDropdown(
                        options = viewModel.priorityOptions,
                        selectedKey = uiState.priority,
                        label = "Select priority",
                        onKeySelected = viewModel::onPriorityChanged,
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    ServiceFormTextField(
                        label = "Points",
                        value = uiState.points,
                        onValueChange = viewModel::onPointsChanged,
                        placeholder = "0",
                        keyboardType = KeyboardType.Number,
                    )

                    Text(
                        text = "On hold option",
                        style = MenusTextStyles.formLabelDark,
                        modifier = Modifier.padding(bottom = 6.dp),
                    )
                    MenusKeyValueDropdown(
                        options = viewModel.toggleOptions,
                        selectedKey = uiState.onHoldOption,
                        label = "Select option",
                        onKeySelected = viewModel::onHoldOptionChanged,
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Status",
                        style = MenusTextStyles.formLabelDark,
                        modifier = Modifier.padding(bottom = 6.dp),
                    )
                    MenusKeyValueDropdown(
                        options = viewModel.toggleOptions,
                        selectedKey = uiState.status,
                        label = "Select status",
                        onKeySelected = viewModel::onStatusChanged,
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    GradientButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        text = if (uiState.isSaving) "Saving..." else "Save Service",
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
private fun ServiceFormTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
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
