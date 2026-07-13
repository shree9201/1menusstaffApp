package com.droptechsolution.shared.services.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.droptechsolution.shared.services.models.DepartmentOverviewCategory
import com.droptechsolution.shared.services.models.DepartmentOverviewStatUi
import com.droptechsolution.shared.ui.theme.BLACK
import com.droptechsolution.shared.ui.theme.TextMuted
import org.jetbrains.compose.ui.tooling.preview.Preview

private val OverviewNavy = Color(0xFF001B3D)
private val StatRed = Color(0xFFE53935)
private val StatOrange = Color(0xFFFB8C00)
private val StatGreen = Color(0xFF43A047)

@Composable
fun DepartmentOverviewSection(
    stats: List<DepartmentOverviewStatUi>,
    modifier: Modifier = Modifier,
    onStatClick: (DepartmentOverviewStatUi) -> Unit = {},
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Department Overview",
            color = OverviewNavy,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            stats.forEach { stat ->
                DepartmentOverviewStatCard(
                    stat = stat,
                    modifier = Modifier.weight(1f),
                    onClick = { onStatClick(stat) },
                )
            }
        }
    }
}

@Composable
private fun DepartmentOverviewStatCard(
    stat: DepartmentOverviewStatUi,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .defaultMinSize(minHeight = 88.dp)
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(16.dp), clip = false)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stat.count.toString(),
            color = stat.category.toCountColor(),
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = stat.label,
            color = TextMuted,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            maxLines = 2,
        )
    }
}

private fun DepartmentOverviewCategory.toCountColor(): Color = when (this) {
    DepartmentOverviewCategory.PENDING -> StatRed
    DepartmentOverviewCategory.IN_PROGRESS -> StatOrange
    DepartmentOverviewCategory.COMPLETED -> StatGreen
    DepartmentOverviewCategory.DELAYED -> StatRed
}

@Preview
@Composable
private fun DepartmentOverviewSectionPreview() {
    DepartmentOverviewSection(
        stats = listOf(
            DepartmentOverviewStatUi(DepartmentOverviewCategory.PENDING, "Pending", 18, listOf("NEW")),
            DepartmentOverviewStatUi(DepartmentOverviewCategory.IN_PROGRESS, "In Progress", 12, listOf("ACCEPT", "START")),
            DepartmentOverviewStatUi(DepartmentOverviewCategory.COMPLETED, "Completed", 56, listOf("CLOSE")),
            DepartmentOverviewStatUi(DepartmentOverviewCategory.DELAYED, "Delayed", 3, listOf("ESCALATED")),
        ),
        modifier = Modifier.padding(16.dp).background(Color(0xFFEEF3F8)),
    )
}
