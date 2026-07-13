package com.droptechsolution.shared.services.views

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.droptechsolution.shared.services.sla.ServiceSlaProgressUi
import com.droptechsolution.shared.ui.theme.BLACK
import com.droptechsolution.shared.ui.theme.MenusPrimary
import com.droptechsolution.shared.ui.theme.TextMuted

private val SlaCardShape = RoundedCornerShape(20.dp)
private val SlaBadgeBg = Color(0xFFE8F1FF)
private val SlaRingTrack = Color(0xFFE5E7EB)
private val SlaMetricBg = Color(0xFFF8FAFC)
private val SlaNavy = Color(0xFF001B3D)

@Composable
fun SlaProgressCard(
    progress: ServiceSlaProgressUi,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(SlaCardShape)
            .background(Color.White)
            .padding(20.dp),
    ) {
        Text(
            text = progress.title,
            color = SlaNavy,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(10.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(SlaBadgeBg)
                .padding(horizontal = 12.dp, vertical = 6.dp),
        ) {
            Text(
                text = progress.slaLabel,
                color = MenusPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        SlaCircularProgress(
            progressFraction = progress.progressFraction,
            elapsedLabel = progress.elapsedLabel,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            SlaMetricBox(
                value = progress.remainingLabel,
                label = "Remaining",
                modifier = Modifier.weight(1f),
            )
            SlaMetricBox(
                value = "${progress.progressPercent}%",
                label = "Progress",
                modifier = Modifier.weight(1f),
            )
            SlaMetricBox(
                value = progress.priorityLabel,
                label = "Priority",
                modifier = Modifier.weight(1f),
            )
        }
        Spacer(modifier = Modifier.height(18.dp))
        LinearProgressIndicator(
            progress = { progress.progressFraction },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(999.dp)),
            color = MenusPrimary,
            trackColor = SlaRingTrack,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Progress toward SLA completion",
            color = TextMuted,
            fontSize = 13.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun SlaCircularProgress(
    progressFraction: Float,
    elapsedLabel: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.size(180.dp),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(180.dp)) {
            val strokeWidth = 14.dp.toPx()
            drawArc(
                color = SlaRingTrack,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
            )
            drawArc(
                color = MenusPrimary,
                startAngle = -90f,
                sweepAngle = 360f * progressFraction.coerceIn(0f, 1f),
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = elapsedLabel,
                color = BLACK,
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Elapsed",
                color = TextMuted,
                fontSize = 15.sp,
            )
        }
    }
}

@Composable
private fun SlaMetricBox(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(SlaMetricBg)
            .padding(vertical = 14.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = value,
            color = BLACK,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            color = TextMuted,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
        )
    }
}
