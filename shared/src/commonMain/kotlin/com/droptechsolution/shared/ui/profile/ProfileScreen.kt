package com.droptechsolution.shared.ui.profile

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
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
import com.droptechsolution.shared.ui.profile.models.ProfileUiState
import com.droptechsolution.shared.ui.profile.presenter.ProfileViewModel
import com.droptechsolution.shared.ui.theme.BG_LIGHT
import com.droptechsolution.shared.ui.theme.BG_WHITE
import com.droptechsolution.shared.ui.theme.BLACK
import com.droptechsolution.shared.ui.theme.MenusPrimary
import com.droptechsolution.shared.ui.theme.MenusSecondary
import com.droptechsolution.shared.ui.theme.MenusSuccess
import com.droptechsolution.shared.ui.theme.TextMuted
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    onLogout: () -> Unit = {},
    viewModel: ProfileViewModel = koinViewModel(),
) {
    LaunchedEffect(Unit) {
        viewModel.loadProfile()
    }

    LaunchedEffect(viewModel) {
        viewModel.logoutEvents.collect {
            onLogout()
        }
    }

    val uiState by viewModel.uiState.collectAsState()
    var showLogoutConfirmation by remember { mutableStateOf(false) }

    if (showLogoutConfirmation) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirmation = false },
            title = {
                Text(
                    text = "Logout",
                    color = BLACK,
                    fontWeight = FontWeight.Bold,
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to logout?",
                    color = TextMuted,
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutConfirmation = false
                        viewModel.logout()
                    },
                ) {
                    Text(text = "Yes", color = MenusPrimary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutConfirmation = false }) {
                    Text(text = "No", color = TextMuted)
                }
            },
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BG_LIGHT)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
    ) {
        ProfileHeader(isManagerProfile = uiState.isManagerProfile)
        Spacer(modifier = Modifier.height(20.dp))
        ProfileOverviewCard(uiState = uiState)
        if (uiState.isManagerProfile) {
            Spacer(modifier = Modifier.height(16.dp))
            ProfileStatsGrid(uiState = uiState)
        }
        Spacer(modifier = Modifier.height(16.dp))
        ContactInfoCard(uiState = uiState)
        Spacer(modifier = Modifier.height(16.dp))
        ProfileMenuItem(
            title = "Profile Settings",
            subtitle = "Theme, alerts & team notifications",
        )
        Spacer(modifier = Modifier.height(12.dp))
        ProfileMenuItem(
            title = "Change Password",
            subtitle = "Keep your account secure",
        )
        Spacer(modifier = Modifier.height(12.dp))
        ProfileMenuItem(
            title = "Help & Support",
            subtitle = "Raise issue to admin/support team",
        )
        Spacer(modifier = Modifier.height(12.dp))
        ProfileMenuItem(
            title = "Logout",
            subtitle = "Sign out of manager account",
            onClick = { showLogoutConfirmation = true },
        )
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun ProfileHeader(isManagerProfile: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(
                text = if (isManagerProfile) "Manager Profile" else "Profile",
                color = BLACK,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (isManagerProfile) "Team overview & account" else "Account details",
                color = TextMuted,
                fontSize = 14.sp,
            )
        }
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(BG_WHITE),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                tint = TextMuted,
            )
        }
    }
}

@Composable
private fun ProfileOverviewCard(uiState: ProfileUiState) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFEFFAF3))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(MenusSecondary),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = uiState.initials,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 14.dp),
        ) {
            Text(
                text = uiState.name.ifBlank { "Staff Member" },
                color = BLACK,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = uiState.roleLabel,
                color = TextMuted,
                fontSize = 13.sp,
            )
        }
        if (uiState.isManagerProfile) {
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFFEF9C3))
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.BarChart,
                        contentDescription = null,
                        tint = Color(0xFFCA8A04),
                        modifier = Modifier.size(16.dp),
                    )
                    Text(
                        modifier = Modifier.padding(start = 4.dp),
                        text = "Team Score",
                        color = TextMuted,
                        fontSize = 11.sp,
                    )
                }
                Text(
                    text = uiState.teamScore,
                    color = BLACK,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun ProfileStatsGrid(uiState: ProfileUiState) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                title = "Team Rating",
                value = uiState.teamRating,
                trailing = {
                    Row {
                        repeat(5) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFFFBBF24),
                                modifier = Modifier.size(14.dp),
                            )
                        }
                    }
                },
            )
            StatCard(
                modifier = Modifier.weight(1f),
                title = "Dept Performance",
                value = uiState.deptPerformance,
                valueColor = MenusSuccess,
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                title = "Active Staff",
                value = uiState.activeStaffCount,
                trailing = {
                    Icon(
                        imageVector = Icons.Default.Groups,
                        contentDescription = null,
                        tint = MenusPrimary,
                        modifier = Modifier.size(20.dp),
                    )
                },
            )
            StatCard(
                modifier = Modifier.weight(1f),
                title = "Tasks Today",
                value = uiState.tasksTodayCount,
                trailing = {
                    Icon(
                        imageVector = Icons.Default.Assignment,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(20.dp),
                    )
                },
            )
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = BLACK,
    trailing: @Composable (() -> Unit)? = null,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(BG_WHITE)
            .padding(16.dp),
    ) {
        Text(
            text = title,
            color = TextMuted,
            fontSize = 12.sp,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = value,
                color = valueColor,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
            )
            trailing?.invoke()
        }
    }
}

@Composable
private fun ContactInfoCard(uiState: ProfileUiState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(BG_WHITE)
            .padding(horizontal = 20.dp, vertical = 4.dp),
    ) {
        ContactRow(label = "Username", value = uiState.username.ifBlank { "—" })
        HorizontalDivider(color = Color(0xFFE5E7EB))
        ContactRow(label = "Email", value = "${uiState.username.ifBlank { "staff" }}@hotel.com")
        HorizontalDivider(color = Color(0xFFE5E7EB))
        ContactRow(label = "Department", value = uiState.departmentLabel.ifBlank { "—" })
    }
}

@Composable
private fun ContactRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            color = TextMuted,
            fontSize = 14.sp,
        )
        Text(
            text = value,
            color = BLACK,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun ProfileMenuItem(
    title: String,
    subtitle: String,
    onClick: (() -> Unit)? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(BG_WHITE)
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = BLACK,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                color = TextMuted,
                fontSize = 13.sp,
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = TextMuted,
        )
    }
}

@Preview
@Composable
private fun ProfileScreenPreview() {
    ProfileScreen()
}
