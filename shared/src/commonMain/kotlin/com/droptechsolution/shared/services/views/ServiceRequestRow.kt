package com.droptechsolution.shared.services.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.LocalLaundryService
import androidx.compose.material.icons.filled.RoomService
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.droptechsolution.shared.services.models.ServiceRequestRowUi
import com.droptechsolution.shared.services.models.TaskActionType
import com.droptechsolution.shared.services.models.TaskPriority
import com.droptechsolution.shared.services.models.TaskStatus
import com.droptechsolution.shared.services.models.toLabel
import com.droptechsolution.shared.ui.theme.BG_LIGHT
import com.droptechsolution.shared.ui.theme.BLACK
import com.droptechsolution.shared.ui.theme.MenusTeal
import com.droptechsolution.shared.ui.theme.TextMuted
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ServiceRequestRow(
    item: ServiceRequestRowUi,
    modifier: Modifier = Modifier,
    onActionClick: (ServiceRequestRowUi) -> Unit = {},
    onRowClick: (ServiceRequestRowUi) -> Unit = {},
) {
    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(18.dp), clip = false)
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White)
            .clickable { onRowClick(item) }
            .padding(horizontal = 16.dp, vertical = 14.dp),
    ) {
        val (iconRef, roomRef, titleRef, subtitleRef, rightColumnRef) = createRefs()

        TaskIconBox(
            icon = item.title.toTaskIcon(),
            modifier = Modifier.constrainAs(iconRef) {
                start.linkTo(parent.start)
                top.linkTo(parent.top)
                width = Dimension.value(46.dp)
                height = Dimension.value(46.dp)
            },
        )

        Text(
            text = item.roomNumber,
            color = TextMuted,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.constrainAs(roomRef) {
                top.linkTo(iconRef.bottom, margin = 6.dp)
                start.linkTo(iconRef.start)
                end.linkTo(iconRef.end)
                width = Dimension.wrapContent
            },
        )

        Column(
            modifier = Modifier.constrainAs(rightColumnRef) {
                end.linkTo(parent.end)
                top.linkTo(parent.top)
            },
            horizontalAlignment = Alignment.End,
        ) {
            TaskStatusBadge(status = item.taskStatus)
            TaskActionButton(
                action = item.action,
                onClick = { onActionClick(item) },
            )
        }

        Text(
            text = item.title,
            color = BLACK,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            modifier = Modifier.constrainAs(titleRef) {
                start.linkTo(iconRef.end, margin = 14.dp)
                end.linkTo(rightColumnRef.start, margin = 8.dp)
                top.linkTo(parent.top, margin = 2.dp)
                width = Dimension.fillToConstraints
            },
        )

        Text(
            text = item.subtitle,
            color = TextMuted,
            fontSize = 14.sp,
            maxLines = 1,
            modifier = Modifier.constrainAs(subtitleRef) {
                start.linkTo(titleRef.start)
                end.linkTo(rightColumnRef.start, margin = 8.dp)
                top.linkTo(titleRef.bottom, margin = 4.dp)
                width = Dimension.fillToConstraints
            },
        )
    }
}

@Composable
private fun TaskIconBox(
    icon: ImageVector,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(BG_LIGHT),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MenusTeal,
            modifier = Modifier.padding(10.dp),
        )
    }
}

@Composable
private fun TaskStatusBadge(
    status: TaskStatus,
    modifier: Modifier = Modifier,
) {
    if (status == TaskStatus.DEFAULT) return

    val (background, textColor, label) = status.toBadgeStyle()

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(background)
            .padding(horizontal = 10.dp, vertical = 3.dp),
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

private fun TaskStatus.toBadgeStyle(): Triple<Color, Color, String> = when (this) {
    TaskStatus.NEW -> Triple(Color(0xFFFFF7E6), Color(0xFFD97706), toLabel())
    TaskStatus.ACCEPT -> Triple(Color(0xFFE6F7F1), MenusTeal, toLabel())
    TaskStatus.ASSIGN -> Triple(Color(0xFFE8F1FF), Color(0xFF2563EB), toLabel())
    TaskStatus.START -> Triple(Color(0xFFE8F1FF), Color(0xFF2563EB), toLabel())
    TaskStatus.HOLD -> Triple(Color(0xFFFFF4E8), Color(0xFFF59E0B), toLabel())
    TaskStatus.END -> Triple(Color(0xFFE8F5E9), Color(0xFF43A047), toLabel())
    TaskStatus.DONE -> Triple(Color(0xFFE8F5E9), Color(0xFF43A047), toLabel())
    TaskStatus.CLOSE -> Triple(Color(0xFFFFEBEE), Color(0xFFE53935), "Done")
    TaskStatus.REJECT -> Triple(Color(0xFFFFE8E8), Color(0xFFE53935), toLabel())
    TaskStatus.REOPEN -> Triple(Color(0xFFFFF0E0), Color(0xFFF59E0B), toLabel())
    TaskStatus.DEFAULT -> Triple(Color.Transparent, Color.Transparent, "")
}

@Composable
private fun TaskActionButton(
    action: TaskActionType,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (action == TaskActionType.NONE) return

    val (label, containerColor) = when (action) {
        TaskActionType.ACCEPT -> "ACCEPT" to MenusTeal
        TaskActionType.START -> "VIEW" to Color(0xFF22C55E)
        TaskActionType.NONE -> return
    }

    Box(
        modifier = modifier
            .padding(top = 8.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(containerColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

private fun String.toTaskIcon(): ImageVector {
    val normalized = lowercase()
    return when {
        normalized.contains("towel") || normalized.contains("linen") -> Icons.Default.LocalLaundryService
        normalized.contains("clean") -> Icons.Default.CleaningServices
        else -> Icons.Default.RoomService
    }
}

@Preview
@Composable
private fun ServiceRequestRowPreview() {
    Box(
        modifier = Modifier
            .background(BG_LIGHT)
            .padding(16.dp),
    ) {
        ServiceRequestRow(
            item = ServiceRequestRowUi(
                id = "1",
                roomNumber = "204",
                title = "Towels Request",
                subtitle = "22:14",
                taskStatus = TaskStatus.CLOSE,
                priority = TaskPriority.HIGH,
                action = TaskActionType.NONE,
            ),
        )
    }
}

@Preview
@Composable
private fun ServiceRequestRowAcceptedPreview() {
    Box(
        modifier = Modifier
            .background(BG_LIGHT)
            .padding(16.dp),
    ) {
        ServiceRequestRow(
            item = ServiceRequestRowUi(
                id = "2",
                roomNumber = "305",
                title = "Room Cleaning",
                subtitle = "In Progress",
                taskStatus = TaskStatus.START,
                priority = TaskPriority.MEDIUM,
                action = TaskActionType.START,
            ),
        )
    }
}
