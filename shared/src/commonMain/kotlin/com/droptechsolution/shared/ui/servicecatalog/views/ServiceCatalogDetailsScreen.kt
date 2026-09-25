package com.droptechsolution.shared.ui.servicecatalog.views

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.droptechsolution.shared.services.models.ServiceCatalogItemUi
import com.droptechsolution.shared.ui.common.ToastMessageEffect
import com.droptechsolution.shared.ui.profile.help.SupportScreenHeader
import com.droptechsolution.shared.ui.servicecatalog.presenter.ServiceCatalogDetailsViewModel
import com.droptechsolution.shared.ui.theme.BG_LIGHT
import com.droptechsolution.shared.ui.theme.BG_WHITE
import com.droptechsolution.shared.ui.theme.BLACK
import com.droptechsolution.shared.ui.theme.MenusError
import com.droptechsolution.shared.ui.theme.MenusPrimary
import com.droptechsolution.shared.ui.theme.TextMuted
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ServiceCatalogDetailsScreen(
    serviceId: String,
    onBack: () -> Unit,
    onEdit: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: ServiceCatalogDetailsViewModel = koinViewModel(),
) {
    LaunchedEffect(serviceId) {
        viewModel.loadDetails(serviceId)
    }

    LaunchedEffect(viewModel) {
        viewModel.deletedEvents.collect {
            onBack()
        }
    }

    val details by viewModel.details.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isDeleting by viewModel.isDeleting.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    var showDeleteConfirmation by remember { mutableStateOf(false) }

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
                    title = "Service Details",
                    onBack = onBack,
                )
                Spacer(modifier = Modifier.height(16.dp))

                when {
                    isLoading && details == null -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 48.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            CircularProgressIndicator(color = MenusPrimary)
                        }
                    }
                    errorMessage != null && details == null -> {
                        Text(
                            text = errorMessage.orEmpty(),
                            color = TextMuted,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(horizontal = 8.dp),
                        )
                    }
                    details != null -> {
                        ServiceCatalogDetailsContent(
                            details = details!!,
                            isDeleting = isDeleting,
                            onEditClick = { onEdit(serviceId) },
                            onDeleteClick = { showDeleteConfirmation = true },
                        )
                    }
                }
            }

            if (isDeleting) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = MenusPrimary)
                }
            }
        }
    }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = {
                if (!isDeleting) showDeleteConfirmation = false
            },
            title = {
                Text(
                    text = "Delete Service",
                    color = BLACK,
                    fontWeight = FontWeight.Bold,
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to delete ${details?.title ?: "this service"}?",
                    color = TextMuted,
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmation = false
                        viewModel.deleteService()
                    },
                    enabled = !isDeleting,
                ) {
                    Text(text = "Delete", color = MenusError)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteConfirmation = false },
                    enabled = !isDeleting,
                ) {
                    Text(text = "Cancel", color = TextMuted)
                }
            },
        )
    }
}

@Composable
private fun ServiceCatalogDetailsContent(
    details: ServiceCatalogItemUi,
    isDeleting: Boolean,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    val isActive = details.status.equals("YES", ignoreCase = true)
    val badgeBackground = if (isActive) Color(0xFFDCFCE7) else Color(0xFFE5E7EB)
    val badgeTextColor = if (isActive) Color(0xFF15803D) else Color(0xFF6B7280)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(BG_WHITE)
            .padding(20.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = details.title,
                    color = BLACK,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = details.category,
                    color = TextMuted,
                    fontSize = 14.sp,
                )
            }
            Text(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(badgeBackground)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                text = if (isActive) "Active" else "Inactive",
                color = badgeTextColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(BG_WHITE)
            .padding(horizontal = 20.dp, vertical = 8.dp),
    ) {
        ServiceInfoRow(label = "Action by", value = details.actionByLabel)
        ServiceInfoRow(label = "Priority", value = details.priority)
        ServiceInfoRow(label = "Points", value = details.points)
        ServiceInfoRow(label = "Box ID", value = details.boxId.ifBlank { "—" })
        ServiceInfoRow(label = "Ask date & time", value = details.aksDateTime)
        ServiceInfoRow(label = "Reminder (min)", value = details.reminderTime)
        ServiceInfoRow(label = "Escalation (min)", value = details.escalationTime)
        ServiceInfoRow(label = "On hold option", value = details.onHoldOption)
        ServiceInfoRow(
            label = "Information",
            value = details.information.ifBlank { "—" },
            showDivider = false,
        )
    }

    Spacer(modifier = Modifier.height(24.dp))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFE8F1FF))
            .border(1.dp, MenusPrimary.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
            .clickable(enabled = !isDeleting, onClick = onEditClick)
            .padding(horizontal = 18.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = null,
            tint = MenusPrimary,
            modifier = Modifier.size(20.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Edit Service",
            color = MenusPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
        )
    }

    Spacer(modifier = Modifier.height(12.dp))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFFFEBEE))
            .border(1.dp, MenusError.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
            .clickable(enabled = !isDeleting, onClick = onDeleteClick)
            .padding(horizontal = 18.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = null,
            tint = MenusError,
            modifier = Modifier.size(20.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Delete Service",
            color = MenusError,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun ServiceInfoRow(
    label: String,
    value: String,
    showDivider: Boolean = true,
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
        Text(text = label, color = TextMuted, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            color = BLACK,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
        )
    }
    if (showDivider) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color(0xFFE5E7EB)),
        )
    }
}
