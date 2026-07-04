package com.droptechsolution.shared.services.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.droptechsolution.shared.services.models.RequestAction
import com.droptechsolution.shared.services.models.RequestDetailsUi
import com.droptechsolution.shared.services.models.RequestSource
import com.droptechsolution.shared.services.models.RequestStatusDisplay
import com.droptechsolution.shared.services.models.TimelineStepUi
import com.droptechsolution.shared.ui.tasks.presenter.RequestDetailsViewModel
import com.droptechsolution.shared.ui.theme.BG_LIGHT
import com.droptechsolution.shared.ui.theme.BLACK
import com.droptechsolution.shared.ui.theme.MenusPrimary
import com.droptechsolution.shared.ui.theme.MenusTeal
import com.droptechsolution.shared.ui.theme.TextMuted
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

private val CardShape = RoundedCornerShape(20.dp)
private val ActionGreen = Color(0xFF22C55E)
private val ActionBlue = Color(0xFF2563EB)
private val ActionBlueBg = Color(0xFFEFF6FF)
private val ActionPauseBg = Color(0xFFFFF4E8)
private val ActionPauseBorder = Color(0xFFF59E0B)
private val ActionRejectBg = Color(0xFFFFEBEE)
private val ActionRejectText = Color(0xFFE53935)
private val TimelineBlue = Color(0xFF2563EB)
private val TimelineLine = Color(0xFFDCE7F6)
private val DurationGreen = Color(0xFF16A34A)

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
    val isUpdating by viewModel.isUpdating.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BG_LIGHT),
    ) {
        when {
            isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MenusTeal)
                }
            }
            errorMessage != null && details == null -> {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(text = errorMessage.orEmpty(), color = TextMuted, fontSize = 16.sp)
                }
            }
            details != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                ) {
                    Spacer(modifier = Modifier.height(36.dp))
                    TaskSummaryCard(details = details!!)
                    Spacer(modifier = Modifier.height(14.dp))
                    DepartmentDurationCard(details = details!!)
                    if (details!!.timelineSteps.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(14.dp))
                        RequestTimelineCard(steps = details!!.timelineSteps)
                    }
                    if (details!!.availableActions.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(20.dp))
                        TaskActionsSection(
                            actions = details!!.availableActions,
                            isUpdating = isUpdating,
                            onAction = viewModel::performAction,
                        )
                    }
                    Spacer(modifier = Modifier.height(88.dp))
                }
            }
        }

        IconButton(
            onClick = onBack,
            modifier = Modifier
                .padding(start = 8.dp, top = 8.dp)
                .align(Alignment.TopStart),
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = BLACK,
            )
        }

        if (details != null) {
            FloatingActionButton(
                onClick = { scope.launch { scrollState.animateScrollTo(0) } },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 20.dp, bottom = 20.dp),
                containerColor = MenusTeal,
                contentColor = Color.White,
                shape = CircleShape,
            ) {
                Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Scroll to top")
            }
        }
    }
}

@Composable
private fun TaskSummaryCard(details: RequestDetailsUi) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(Color.White)
            .padding(20.dp),
    ) {
        Text(
            text = "Room ${details.roomNumber}",
            color = BLACK,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = details.title,
            color = BLACK,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "${details.guestLabel} · ${details.priorityLabel}",
            color = TextMuted,
            fontSize = 15.sp,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = details.requestedMeta,
            color = TextMuted,
            fontSize = 15.sp,
        )
        Spacer(modifier = Modifier.height(14.dp))
        TaskStatusPill(status = details.statusDisplay)
    }
}

@Composable
private fun TaskStatusPill(status: RequestStatusDisplay) {
    val (background, textColor, label) = status.toPillStyle()
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(background)
            .padding(horizontal = 14.dp, vertical = 6.dp),
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

private fun RequestStatusDisplay.toPillStyle(): Triple<Color, Color, String> = when (this) {
    RequestStatusDisplay.NEW -> Triple(Color(0xFFFFF7E6), Color(0xFFD97706), "New")
    RequestStatusDisplay.ACCEPTED -> Triple(Color(0xFFE6F7F1), MenusTeal, "Accepted")
    RequestStatusDisplay.IN_PROGRESS -> Triple(Color(0xFFE8F1FF), MenusPrimary, "In Progress")
    RequestStatusDisplay.ON_HOLD -> Triple(Color(0xFFFFF4E8), ActionPauseBorder, "On Hold")
    RequestStatusDisplay.COMPLETED -> Triple(Color(0xFFFFE8E8), ActionRejectText, "Completed")
    RequestStatusDisplay.REJECTED -> Triple(Color(0xFFFFE8E8), ActionRejectText, "Rejected")
    RequestStatusDisplay.ESCALATED -> Triple(Color(0xFFFFE8E8), Color(0xFFDC2626), "Escalated")
    RequestStatusDisplay.UNKNOWN -> Triple(Color(0xFFF3F4F6), TextMuted, "Unknown")
}

@Composable
private fun DepartmentDurationCard(details: RequestDetailsUi) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(Color.White)
            .padding(horizontal = 20.dp, vertical = 18.dp),
    ) {
        InfoSplitRow(label = "Department", value = details.department)
        Spacer(modifier = Modifier.height(14.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color(0xFFE5E7EB)),
        )
        Spacer(modifier = Modifier.height(14.dp))
        InfoSplitRow(label = "Est. duration", value = details.estimatedDuration)
    }
}

@Composable
private fun InfoSplitRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = label, color = TextMuted, fontSize = 16.sp)
        Text(
            text = value,
            color = BLACK,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun RequestTimelineCard(steps: List<TimelineStepUi>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(Color.White)
            .padding(20.dp),
    ) {
        Text(
            text = "Request Timeline",
            color = BLACK,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(18.dp))
        steps.forEachIndexed { index, step ->
            TimelineStepRow(
                step = step,
                showConnector = index < steps.lastIndex,
            )
        }
    }
}

@Composable
private fun TimelineStepRow(
    step: TimelineStepUi,
    showConnector: Boolean,
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            TimelineStepIndicator(
                isCompleted = step.isCompleted,
                isCurrent = step.isCurrent,
            )
            if (showConnector) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(52.dp)
                        .background(TimelineLine),
                )
            }
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "${step.headline} — ${step.description}",
                color = BLACK,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = step.timeLabel,
                color = TextMuted,
                fontSize = 14.sp,
            )
            step.durationFromPrevious?.let { duration ->
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = duration,
                    color = DurationGreen,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
            if (showConnector) Spacer(modifier = Modifier.height(18.dp))
        }
    }
}

@Composable
private fun TimelineStepIndicator(
    isCompleted: Boolean,
    isCurrent: Boolean,
) {
    if (isCurrent && !isCompleted) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .border(2.dp, TimelineBlue, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(TimelineBlue),
            )
        }
    } else {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(TimelineBlue),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp),
            )
        }
    }
}

@Composable
private fun TaskActionsSection(
    actions: List<RequestAction>,
    isUpdating: Boolean,
    onAction: (RequestAction) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Choose action",
            color = BLACK,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(14.dp))

        if (isUpdating) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = MenusTeal, modifier = Modifier.size(28.dp))
            }
        }

        actions.filter { it == RequestAction.ACCEPT || it == RequestAction.START }.forEach { action ->
            PrimaryActionButton(action = action, enabled = !isUpdating, onClick = { onAction(action) })
            Spacer(modifier = Modifier.height(10.dp))
        }

        val secondary = actions.filter { it == RequestAction.PASS || it == RequestAction.PAUSE }
        if (secondary.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                secondary.forEach { action ->
                    SecondaryActionButton(
                        action = action,
                        enabled = !isUpdating,
                        onClick = { onAction(action) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        if (RequestAction.REJECT in actions) {
            RejectActionButton(
                enabled = !isUpdating,
                onClick = { onAction(RequestAction.REJECT) },
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(ActionBlueBg)
                    .padding(horizontal = 16.dp, vertical = 6.dp),
            ) {
                Text(
                    text = "STAFF SCREEN",
                    color = ActionBlue,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun PrimaryActionButton(
    action: RequestAction,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val (label, icon) = when (action) {
        RequestAction.ACCEPT -> "Accept Request" to Icons.Default.Check
        RequestAction.START -> "Start Service" to Icons.Default.PlayArrow
        else -> return
    }
    ActionButton(
        label = label,
        icon = icon,
        background = ActionGreen,
        contentColor = Color.White,
        borderColor = ActionGreen,
        enabled = enabled,
        onClick = onClick,
    )
}

@Composable
private fun SecondaryActionButton(
    action: RequestAction,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val (label, icon, bg, border, text) = when (action) {
        RequestAction.PASS -> Quintuple(
            "Pass Request",
            Icons.AutoMirrored.Filled.Reply,
            ActionBlueBg,
            ActionBlue,
            ActionBlue,
        )
        RequestAction.PAUSE -> Quintuple(
            "Pause Request",
            Icons.Default.Pause,
            ActionPauseBg,
            ActionPauseBorder,
            ActionPauseBorder,
        )
        else -> return
    }
    ActionButton(
        label = label,
        icon = icon,
        background = bg,
        contentColor = text,
        borderColor = border,
        enabled = enabled,
        onClick = onClick,
        modifier = modifier,
    )
}

@Composable
private fun RejectActionButton(
    enabled: Boolean,
    onClick: () -> Unit,
) {
    ActionButton(
        label = "Reject Request",
        icon = Icons.Default.Close,
        background = ActionRejectBg,
        contentColor = ActionRejectText,
        borderColor = ActionRejectText.copy(alpha = 0.35f),
        enabled = enabled,
        onClick = onClick,
    )
}

private data class Quintuple(
    val label: String,
    val icon: ImageVector,
    val bg: Color,
    val border: Color,
    val text: Color,
)

@Composable
private fun ActionButton(
    label: String,
    icon: ImageVector,
    background: Color,
    contentColor: Color,
    borderColor: Color,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(background)
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = contentColor, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label, color = contentColor, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}
