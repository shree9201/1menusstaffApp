package com.droptechsolution.shared.services.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.droptechsolution.shared.services.models.RequestActivityUi
import com.droptechsolution.shared.services.models.RequestDetailsUi
import com.droptechsolution.shared.services.models.RequestSource
import com.droptechsolution.shared.services.models.TaskActionType
import com.droptechsolution.shared.ui.tasks.presenter.RequestDetailsViewModel
import com.droptechsolution.shared.ui.theme.BG_LIGHT
import com.droptechsolution.shared.ui.theme.BLACK
import com.droptechsolution.shared.ui.theme.MenusTeal
import com.droptechsolution.shared.ui.theme.TextMuted
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RequestDetailsScreen(
    requestId: String,
    source: RequestSource,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RequestDetailsViewModel = koinViewModel(),
) {
    LaunchedEffect(requestId, source) {
        viewModel.loadDetails(requestId, source)
    }

    val details by viewModel.details.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BG_LIGHT),
    ) {
        RequestDetailsTopBar(
            title = details?.title ?: "Request Details",
            onBack = onBack,
        )

        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = MenusTeal)
                }
            }
            errorMessage != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = errorMessage.orEmpty(),
                        color = TextMuted,
                        fontSize = 16.sp,
                    )
                }
            }
            details != null -> {
                RequestDetailsContent(
                    details = details!!,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

@Composable
private fun RequestDetailsTopBar(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 8.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = BLACK,
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = BLACK,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun RequestDetailsContent(
    details: RequestDetailsUi,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
    ) {
        RequestSummaryCard(details = details)

        Spacer(modifier = Modifier.height(20.dp))

        RequestInfoSection(details = details)

        if (details.activities.isNotEmpty()) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Activity Timeline",
                color = BLACK,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(12.dp))
            details.activities.forEach { activity ->
                ActivityTimelineItem(activity = activity)
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        if (details.action != TaskActionType.NONE) {
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { /* TODO: update request status */ },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (details.action == TaskActionType.ACCEPT) MenusTeal else Color(0xFF22C55E),
                ),
            ) {
                Text(
                    text = if (details.action == TaskActionType.ACCEPT) "ACCEPT" else "START",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 4.dp),
                )
            }
        }
    }
}

@Composable
private fun RequestSummaryCard(details: RequestDetailsUi) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White)
            .padding(20.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = details.requestCode,
                color = TextMuted,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
            )
            StatusChip(label = details.statusLabel)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Room ${details.roomNumber}",
            color = BLACK,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
        )

        if (details.title.isNotBlank()) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = details.title,
                color = BLACK,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun RequestInfoSection(details: RequestDetailsUi) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text(
            text = "Details",
            color = BLACK,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
        )

        if (details.note.isNotBlank()) {
            DetailRow(label = "Note", value = details.note)
        }
        if (details.guestName.isNotBlank()) {
            DetailRow(label = "Guest", value = details.guestName)
        }
        DetailRow(label = "Points", value = details.points.toString())
        DetailRow(label = "Created", value = details.createdDate)
        DetailRow(label = "Updated", value = details.updatedDate)
        if (details.startTime.isNotBlank()) {
            DetailRow(label = "Started", value = details.startTime)
        }
        if (details.endTime.isNotBlank()) {
            DetailRow(label = "Ended", value = details.endTime)
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            color = TextMuted,
            fontSize = 14.sp,
            modifier = Modifier.width(88.dp),
        )
        Text(
            text = value,
            color = BLACK,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun StatusChip(label: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MenusTeal.copy(alpha = 0.12f))
            .padding(horizontal = 10.dp, vertical = 4.dp),
    ) {
        Text(
            text = label.uppercase(),
            color = MenusTeal,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun ActivityTimelineItem(activity: RequestActivityUi) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(16.dp),
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(MenusTeal)
                .align(Alignment.Top),
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = activity.status,
                    color = BLACK,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = activity.dateTime.toDisplayTime(),
                    color = TextMuted,
                    fontSize = 12.sp,
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = activity.comment,
                color = TextMuted,
                fontSize = 13.sp,
            )
            if (activity.assignedTo.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Assigned to ${activity.assignedTo}",
                    color = MenusTeal,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}

private fun String.toDisplayTime(): String {
    val timePart = substringAfter(" ", "")
    return if (timePart.length >= 5) timePart.take(5) else this
}
