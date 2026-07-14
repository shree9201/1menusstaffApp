package com.droptechsolution.shared.ui.staff.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.droptechsolution.shared.ui.staff.models.StaffAvailability
import com.droptechsolution.shared.ui.staff.models.StaffMemberUi
import com.droptechsolution.shared.ui.staff.models.StaffSummaryUi
import com.droptechsolution.shared.ui.staff.presenter.StaffViewModel
import com.droptechsolution.shared.ui.theme.BG_LIGHT
import com.droptechsolution.shared.ui.theme.BG_WHITE
import com.droptechsolution.shared.ui.theme.BLACK
import com.droptechsolution.shared.ui.theme.MenusSuccess
import com.droptechsolution.shared.ui.theme.MenusWarning
import com.droptechsolution.shared.ui.theme.TextMuted
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun StaffScreen(
    modifier: Modifier = Modifier,
    viewModel: StaffViewModel = koinViewModel(),
) {
    LaunchedEffect(Unit) {
        viewModel.loadStaff()
    }

    val staffMembers by viewModel.staffMembers.collectAsState()
    val summary by viewModel.summary.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BG_LIGHT)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
    ) {
        Text(
            text = "Staff",
            color = BLACK,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Team availability",
            color = TextMuted,
            fontSize = 14.sp,
        )
        Spacer(modifier = Modifier.height(20.dp))

        when {
            isLoading && staffMembers.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 48.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }
            errorMessage != null && staffMembers.isEmpty() -> {
                Text(
                    text = errorMessage.orEmpty(),
                    color = TextMuted,
                    fontSize = 14.sp,
                )
            }
            else -> {
                StaffSummaryCard(summary = summary)
                Spacer(modifier = Modifier.height(16.dp))
                staffMembers.forEach { member ->
                    StaffMemberCard(member = member)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun StaffSummaryCard(summary: StaffSummaryUi) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(BG_WHITE)
            .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        SummaryRow(
            label = "Available",
            count = summary.availableCount,
            dotColor = MenusSuccess,
            labelColor = MenusSuccess,
        )
        Spacer(modifier = Modifier.height(12.dp))
        SummaryRow(
            label = "Busy",
            count = summary.busyCount,
            dotColor = MenusWarning,
            labelColor = MenusWarning,
        )
    }
}

@Composable
private fun SummaryRow(
    label: String,
    count: Int,
    dotColor: Color,
    labelColor: Color,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(dotColor)
                    .padding(5.dp),
            )
            Text(
                modifier = Modifier.padding(start = 10.dp),
                text = label,
                color = labelColor,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
        Text(
            text = "$count staff",
            color = BLACK,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun StaffMemberCard(member: StaffMemberUi) {
    val badgeBackground = when (member.availability) {
        StaffAvailability.Available -> Color(0xFFDCFCE7)
        StaffAvailability.Busy -> Color(0xFFFFEDD5)
        StaffAvailability.Offline -> Color(0xFFE5E7EB)
    }
    val badgeTextColor = when (member.availability) {
        StaffAvailability.Available -> Color(0xFF15803D)
        StaffAvailability.Busy -> Color(0xFFC2410C)
        StaffAvailability.Offline -> Color(0xFF6B7280)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(BG_WHITE)
            .padding(horizontal = 20.dp, vertical = 18.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = member.name,
                color = BLACK,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = member.subtitle,
                color = TextMuted,
                fontSize = 13.sp,
            )
        }
        Text(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(badgeBackground)
                .padding(horizontal = 14.dp, vertical = 6.dp),
            text = member.badgeLabel,
            color = badgeTextColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Preview
@Composable
private fun StaffScreenPreview() {
    StaffScreen()
}
