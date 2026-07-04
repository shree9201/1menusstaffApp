package com.droptechsolution.shared.services.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.droptechsolution.shared.services.models.ServiceRequestRowUi
import com.droptechsolution.shared.services.models.TaskActionType
import com.droptechsolution.shared.services.models.TaskPriority
import com.droptechsolution.shared.ui.theme.BLACK
import com.droptechsolution.shared.ui.theme.MenusTeal
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ActiveTasksSection(
    tasks: List<ServiceRequestRowUi>,
    modifier: Modifier = Modifier,
    onViewAllClick: () -> Unit = {},
    onTaskActionClick: (ServiceRequestRowUi) -> Unit = {},
) {
    Column(modifier = modifier.fillMaxWidth()) {
        ActiveTasksHeader(
            taskCount = tasks.size,
            onViewAllClick = onViewAllClick,
        )

        Spacer(modifier = Modifier.height(14.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            tasks.forEach { task ->
                ServiceRequestRow(
                    item = task,
                    onActionClick = onTaskActionClick,
                )
            }
        }
    }
}

@Composable
fun ActiveTasksHeader(
    taskCount: Int,
    modifier: Modifier = Modifier,
    onViewAllClick: () -> Unit = {},
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Active Tasks ($taskCount)",
            color = BLACK,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
        )

        Text(
            text = "View All",
            color = MenusTeal,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .clickable(onClick = onViewAllClick)
                .padding(4.dp),
        )
    }
}

@Preview
@Composable
private fun ActiveTasksSectionPreview() {
    ActiveTasksSection(
        tasks = listOf(
            ServiceRequestRowUi(
                id = "1",
                roomNumber = "204",
                title = "Towels Request",
                subtitle = "22:14",
                priority = TaskPriority.HIGH,
                action = TaskActionType.ACCEPT,
            ),
            ServiceRequestRowUi(
                id = "2",
                roomNumber = "305",
                title = "Room Cleaning",
                subtitle = "Accepted · Ready to start",
                priority = TaskPriority.MEDIUM,
                action = TaskActionType.START,
            ),
        ),
        modifier = Modifier.padding(16.dp),
    )
}
