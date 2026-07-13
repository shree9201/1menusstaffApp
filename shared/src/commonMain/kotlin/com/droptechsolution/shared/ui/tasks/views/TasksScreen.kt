package com.droptechsolution.shared.ui.tasks.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.droptechsolution.shared.services.models.DepartmentOverviewCategory
import com.droptechsolution.shared.services.models.ServiceRequestRowUi
import com.droptechsolution.shared.services.views.ServiceRequestRow
import com.droptechsolution.shared.ui.tasks.presenter.TasksViewModel
import com.droptechsolution.shared.ui.theme.BG_LIGHT
import com.droptechsolution.shared.ui.theme.BLACK
import com.droptechsolution.shared.ui.theme.MenusTeal
import com.droptechsolution.shared.ui.theme.TextMuted
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TasksScreen(
    statusFilter: String? = null,
    overviewCategory: String? = null,
    onTaskClick: (ServiceRequestRowUi) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TasksViewModel = koinViewModel(),
) {
    LaunchedEffect(statusFilter, overviewCategory) {
        viewModel.loadTasks(statusFilter, overviewCategory)
    }

    val tasks by viewModel.tasks.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val activeFilter by viewModel.statusFilter.collectAsState()
    val activeOverview by viewModel.overviewCategory.collectAsState()

    val filterLabel = activeOverview?.toLabel() ?: activeFilter?.toFilterLabel()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BG_LIGHT)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
    ) {
        Text(
            text = if (filterLabel != null) "$filterLabel (${tasks.size})" else "Tasks (${tasks.size})",
            color = BLACK,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (filterLabel != null) {
                "Filtered room requests"
            } else {
                "Room requests and outlet services"
            },
            color = TextMuted,
            fontSize = 15.sp,
        )

        Spacer(modifier = Modifier.height(20.dp))

        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 48.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = MenusTeal)
                }
            }
            errorMessage != null -> {
                Text(
                    text = errorMessage.orEmpty(),
                    color = TextMuted,
                    fontSize = 16.sp,
                )
            }
            tasks.isEmpty() -> {
                Text(
                    text = if (filterLabel != null) {
                        "No $filterLabel requests found"
                    } else {
                        "No tasks available"
                    },
                    color = TextMuted,
                    fontSize = 16.sp,
                )
            }
            else -> {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    tasks.forEach { task ->
                        ServiceRequestRow(
                            item = task,
                            onRowClick = onTaskClick,
                        )
                    }
                }
            }
        }
    }
}

private fun DepartmentOverviewCategory.toLabel(): String = when (this) {
    DepartmentOverviewCategory.PENDING -> "Pending"
    DepartmentOverviewCategory.IN_PROGRESS -> "In Progress"
    DepartmentOverviewCategory.COMPLETED -> "Completed"
    DepartmentOverviewCategory.DELAYED -> "Delayed"
}

private fun String.toFilterLabel(): String = when (uppercase()) {
    "NEW" -> "New"
    "ACCEPT", "ACCEPTED" -> "Accepted"
    "START", "STARTED" -> "In Progress"
    "CLOSE", "CLOSED" -> "Completed"
    "HOLD", "PAUSE", "PAUSED" -> "On Hold"
    "REJECT", "REJECTED" -> "Rejected"
    "ESCALATED" -> "Escalated"
    else -> this
}
