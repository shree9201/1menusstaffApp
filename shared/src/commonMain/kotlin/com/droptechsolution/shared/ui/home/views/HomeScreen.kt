package com.droptechsolution.shared.ui.home.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.droptechsolution.shared.services.models.ServiceRequestRowUi
import com.droptechsolution.shared.services.views.ActiveTasksSection
import com.droptechsolution.shared.ui.home.presenter.HomeViewModel
import com.droptechsolution.shared.ui.theme.BG_LIGHT
import com.droptechsolution.shared.ui.theme.MenusGradients
import com.droptechsolution.shared.ui.theme.MenusTextStyles
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onViewAllTasks: () -> Unit = {},
    onTaskClick: (ServiceRequestRowUi) -> Unit = {},
    viewModel: HomeViewModel = koinViewModel(),
) {
    viewModel.checkUserInfo()
    val userInfo = viewModel.loginState.collectAsState()
    val activeTasks by viewModel.activeTasks.collectAsState()
    val activeTaskCount by viewModel.activeTaskCount.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BG_LIGHT)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        DashboardHeaderRow(
            userName = userInfo.value.userName,
            role = userInfo.value.role,
            status = userInfo.value.status,
            initials = userInfo.value.initials.ifBlank { userInfo.value.userName.take(2).uppercase() },
        )

        Spacer(modifier = Modifier.height(24.dp))

        ActiveTasksSection(
            tasks = activeTasks.take(1),
            totalCount = activeTaskCount,
            onViewAllClick = onViewAllTasks,
            onTaskClick = onTaskClick,
        )
    }
}


@Composable
fun DashboardHeaderRow(
    userName: String = "Ravi",
    role: String = "Housekeeping",
    status: String = "Available",
    initials: String = "RK",
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF2CC6B5),
                                Color(0xFF0F9C8D),
                            )
                        )
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = initials,
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Hi $userName 👋",
                    color = Color(0xFF1C2B4A),
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = role,
                        color = Color(0xFF6F7684),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                    )

                    Text(
                        text = " | ",
                        color = Color(0xFF9AA3B2),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                    )

                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF2CCB5F))
                    )

                    Spacer(modifier = Modifier.width(8.dp))

//                    Text(
//                        text = status,
//                        color = Color(0xFF159947),
//                        fontSize = 16.sp,
//                        fontWeight = FontWeight.SemiBold,
//                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        NotificationBellButton(
            hasUnread = true
        )
    }
}

@Composable
fun NotificationBellButton(
    hasUnread: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.size(64.dp),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Notifications",
                tint = Color(0xFFF4B400),
                modifier = Modifier.size(28.dp)
            )
        }

        if (hasUnread) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-4).dp, y = 6.dp)
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFF4D4F))
            )
        }
    }
}
//
//@Preview
//@Composable
//private fun FeedScreenPreview() {
//    HomeScreen()
//}

@Composable
@org.jetbrains.compose.ui.tooling.preview.Preview
fun DashboardHeaderRowPreview() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF3F7FB))
            .padding(vertical = 20.dp)
    ) {
        DashboardHeaderRow()
    }
}