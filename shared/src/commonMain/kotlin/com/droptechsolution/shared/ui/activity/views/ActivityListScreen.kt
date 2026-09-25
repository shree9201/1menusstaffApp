package com.droptechsolution.shared.ui.activity.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
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
import com.droptechsolution.shared.services.models.ActivityListItemUi
import com.droptechsolution.shared.services.models.RequestStatusDisplay
import com.droptechsolution.shared.ui.activity.presenter.ActivityListViewModel
import com.droptechsolution.shared.ui.profile.help.SupportScreenHeader
import com.droptechsolution.shared.ui.theme.BG_LIGHT
import com.droptechsolution.shared.ui.theme.BG_WHITE
import com.droptechsolution.shared.ui.theme.BLACK
import com.droptechsolution.shared.ui.theme.MenusPrimary
import com.droptechsolution.shared.ui.theme.MenusTeal
import com.droptechsolution.shared.ui.theme.TextMuted
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ActivityListScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ActivityListViewModel = koinViewModel(),
) {
    LaunchedEffect(Unit) {
        viewModel.loadActivities()
    }

    val activities by viewModel.activities.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BG_LIGHT)
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        SupportScreenHeader(
            title = "Notifications",
            onBack = onBack,
        )
        Spacer(modifier = Modifier.height(12.dp))

        when {
            isLoading && activities.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = MenusTeal)
                }
            }
            errorMessage != null && activities.isEmpty() -> {
                Text(
                    text = errorMessage.orEmpty(),
                    color = TextMuted,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 24.dp),
                )
            }
            else -> {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(activities, key = { it.id }) { item ->
                        ActivityListRow(item = item)
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun ActivityListRow(item: ActivityListItemUi) {
    val (background, textColor, label) = item.statusDisplay.toActivityPillStyle()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(BG_WHITE)
            .padding(16.dp),
    ) {
        Text(
            text = item.comment,
            color = BLACK,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Room ${item.roomTitle} · ID ${item.roomId}",
            color = TextMuted,
            fontSize = 13.sp,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = item.userName,
            color = BLACK,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = item.dateTime,
                color = TextMuted,
                fontSize = 13.sp,
                modifier = Modifier.weight(1f),
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(background)
                    .padding(horizontal = 10.dp, vertical = 4.dp),
            ) {
                Text(
                    text = label,
                    color = textColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

private fun RequestStatusDisplay.toActivityPillStyle(): Triple<Color, Color, String> = when (this) {
    RequestStatusDisplay.NEW -> Triple(Color(0xFFFFF7E6), Color(0xFFD97706), "New")
    RequestStatusDisplay.ASSIGNED -> Triple(Color(0xFFE8F1FF), MenusPrimary, "Assigned")
    RequestStatusDisplay.ACCEPTED -> Triple(Color(0xFFE6F7F1), MenusTeal, "Accepted")
    RequestStatusDisplay.IN_PROGRESS -> Triple(Color(0xFFE8F1FF), MenusPrimary, "In Progress")
    RequestStatusDisplay.ON_HOLD -> Triple(Color(0xFFFFF4E8), Color(0xFFF59E0B), "On Hold")
    RequestStatusDisplay.COMPLETED -> Triple(Color(0xFFFFE8E8), Color(0xFFE53935), "Completed")
    RequestStatusDisplay.REJECTED -> Triple(Color(0xFFFFE8E8), Color(0xFFE53935), "Rejected")
    RequestStatusDisplay.ESCALATED -> Triple(Color(0xFFFFE8E8), Color(0xFFDC2626), "Escalated")
    RequestStatusDisplay.UNKNOWN -> Triple(Color(0xFFF3F4F6), TextMuted, "Unknown")
}
