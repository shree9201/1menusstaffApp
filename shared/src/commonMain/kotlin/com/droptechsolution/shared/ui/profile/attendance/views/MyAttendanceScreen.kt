package com.droptechsolution.shared.ui.profile.attendance.views

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.droptechsolution.shared.ui.common.ToastMessageEffect
import com.droptechsolution.shared.ui.profile.attendance.models.AttendanceDayStatus
import com.droptechsolution.shared.ui.profile.attendance.models.AttendanceDayUi
import com.droptechsolution.shared.ui.profile.attendance.models.AttendanceMonthOption
import com.droptechsolution.shared.ui.profile.attendance.models.AttendanceSummaryUi
import com.droptechsolution.shared.ui.profile.attendance.presenter.MyAttendanceViewModel
import com.droptechsolution.shared.ui.profile.help.SupportScreenHeader
import com.droptechsolution.shared.ui.theme.AttendanceAbsent
import com.droptechsolution.shared.ui.theme.AttendanceAbsentBgEnd
import com.droptechsolution.shared.ui.theme.AttendanceAbsentBgStart
import com.droptechsolution.shared.ui.theme.AttendanceAbsentBorder
import com.droptechsolution.shared.ui.theme.AttendanceCellDefaultBg
import com.droptechsolution.shared.ui.theme.AttendanceHalf
import com.droptechsolution.shared.ui.theme.AttendanceHalfBgEnd
import com.droptechsolution.shared.ui.theme.AttendanceHalfBgStart
import com.droptechsolution.shared.ui.theme.AttendanceHalfBorder
import com.droptechsolution.shared.ui.theme.AttendancePresent
import com.droptechsolution.shared.ui.theme.AttendancePresentBgEnd
import com.droptechsolution.shared.ui.theme.AttendancePresentBgStart
import com.droptechsolution.shared.ui.theme.AttendancePresentBorder
import com.droptechsolution.shared.ui.theme.BG_LIGHT
import com.droptechsolution.shared.ui.theme.BG_WHITE
import com.droptechsolution.shared.ui.theme.BLACK
import com.droptechsolution.shared.ui.theme.MenusPrimary
import com.droptechsolution.shared.ui.theme.MenusTextStyles
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MyAttendanceScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    viewModel: MyAttendanceViewModel = koinViewModel(),
) {
    LaunchedEffect(Unit) {
        viewModel.loadAttendance()
    }

    val uiState by viewModel.uiState.collectAsState()

    ToastMessageEffect(messages = viewModel.toastEvents) {
        Box(modifier = modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BG_LIGHT)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                SupportScreenHeader(
                    title = "My Attendance",
                    onBack = onBack,
                )
                Spacer(modifier = Modifier.height(12.dp))

                AttendanceMonthSelector(
                    selectedLabel = uiState.selectedMonthLabel,
                    months = uiState.availableMonths,
                    onMonthSelected = viewModel::onMonthSelected,
                )
                Spacer(modifier = Modifier.height(12.dp))

                AttendanceSummaryRow(summary = uiState.summary)
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Attendance calendar",
                    style = MenusTextStyles.sectionTitle,
                )
                Spacer(modifier = Modifier.height(10.dp))
                AttendanceCalendarCard(days = uiState.calendarDays)
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Daily attendance",
                    style = MenusTextStyles.sectionTitle,
                )
                Spacer(modifier = Modifier.height(10.dp))

                if (uiState.dailyRecords.isEmpty() && !uiState.isLoading) {
                    AttendanceDailyCard(
                        title = "No records",
                        subtitle = "No attendance found for ${uiState.selectedMonthLabel}.",
                    )
                } else {
                    uiState.dailyRecords.forEach { day ->
                        AttendanceDailyCard(
                            title = "${day.dayNumber.toString().padStart(2, '0')} ${day.monthShortName}",
                            subtitle = "${day.statusLabel} · ${day.detail}",
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = MenusPrimary)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AttendanceMonthSelector(
    selectedLabel: String,
    months: List<AttendanceMonthOption>,
    onMonthSelected: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(BG_WHITE)
            .padding(16.dp),
    ) {
        Text(
            text = "Select month",
            style = MenusTextStyles.attendanceMonthLabel,
        )
        Spacer(modifier = Modifier.height(8.dp))
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
        ) {
            OutlinedTextField(
                value = selectedLabel,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = BG_WHITE,
                    unfocusedContainerColor = BG_WHITE,
                    focusedBorderColor = Color(0xFFD1D5DB),
                    unfocusedBorderColor = Color(0xFFD1D5DB),
                    focusedTextColor = BLACK,
                    unfocusedTextColor = BLACK,
                ),
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                months.forEach { month ->
                    DropdownMenuItem(
                        text = { Text(month.label) },
                        onClick = {
                            onMonthSelected(month.key)
                            expanded = false
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun AttendanceSummaryRow(summary: AttendanceSummaryUi) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        AttendanceSummaryCard(label = "Days Present", value = summary.daysPresent, modifier = Modifier.weight(1f))
        AttendanceSummaryCard(label = "Worked Hours", value = summary.workedHours, modifier = Modifier.weight(1f))
        AttendanceSummaryCard(label = "Late Arrivals", value = summary.lateArrivals, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun AttendanceSummaryCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(BG_WHITE)
            .padding(horizontal = 8.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = label, style = MenusTextStyles.attendanceSummaryLabel)
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = value, style = MenusTextStyles.attendanceSummaryValue)
    }
}

@Composable
private fun AttendanceCalendarCard(days: List<AttendanceDayUi>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(BG_WHITE)
            .padding(16.dp),
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat").forEach { label ->
                Text(
                    text = label,
                    style = MenusTextStyles.attendanceCalendarHeader,
                    modifier = Modifier.weight(1f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))

        if (days.isEmpty()) {
            Text(
                text = "No attendance days to display.",
                style = MenusTextStyles.attendanceDailySubtitle,
                modifier = Modifier.padding(vertical = 12.dp),
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                days.chunked(7).forEach { week ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        week.forEach { day ->
                            AttendanceCalendarCell(
                                day = day,
                                modifier = Modifier.weight(1f),
                            )
                        }
                        repeat(7 - week.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))
        AttendanceLegend()
    }
}

@Composable
private fun AttendanceCalendarCell(
    day: AttendanceDayUi,
    modifier: Modifier = Modifier,
) {
    val colors = attendanceCellColors(day.status)
    Column(
        modifier = modifier
            .height(80.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(colors.backgroundStart, colors.backgroundEnd),
                ),
            )
            .padding(10.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = day.dayNumber.toString().padStart(2, '0'),
                style = MenusTextStyles.attendanceCellDay,
            )
            Text(
                text = day.timeBadge,
                style = MenusTextStyles.attendanceCellDay,
            )
        }
        Text(
            text = if (day.status == AttendanceDayStatus.Absent) "Absent" else day.detail,
            style = MenusTextStyles.attendanceCellDetail,
            maxLines = 2,
        )
    }
}

@Composable
private fun AttendanceLegend() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        AttendanceLegendItem(color = AttendancePresent, label = "Present")
        AttendanceLegendItem(color = AttendanceHalf, label = "Half Day")
        AttendanceLegendItem(color = AttendanceAbsent, label = "Absent")
    }
}

@Composable
private fun AttendanceLegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color),
        )
        Spacer(modifier = Modifier.size(6.dp))
        Text(text = label, style = MenusTextStyles.attendanceLegend)
    }
}

@Composable
private fun AttendanceDailyCard(
    title: String,
    subtitle: String,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(BG_WHITE)
            .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        Text(text = title, style = MenusTextStyles.attendanceDailyTitle)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = subtitle, style = MenusTextStyles.attendanceDailySubtitle)
    }
}

private data class AttendanceCellColors(
    val backgroundStart: Color,
    val backgroundEnd: Color,
    val border: Color,
)

private fun attendanceCellColors(status: AttendanceDayStatus): AttendanceCellColors =
    when (status) {
        AttendanceDayStatus.Present -> AttendanceCellColors(
            AttendancePresentBgStart,
            AttendancePresentBgEnd,
            AttendancePresentBorder,
        )
        AttendanceDayStatus.HalfDay -> AttendanceCellColors(
            AttendanceHalfBgStart,
            AttendanceHalfBgEnd,
            AttendanceHalfBorder,
        )
        AttendanceDayStatus.Absent -> AttendanceCellColors(
            AttendanceAbsentBgStart,
            AttendanceAbsentBgEnd,
            AttendanceAbsentBorder,
        )
    }

@Preview
@Composable
private fun MyAttendanceScreenPreview() {
    MyAttendanceScreen()
}
