package com.droptechsolution.shared.ui.profile.attendance.models

data class MyAttendanceUiState(
    val selectedMonthLabel: String = "",
    val availableMonths: List<AttendanceMonthOption> = emptyList(),
    val summary: AttendanceSummaryUi = AttendanceSummaryUi(),
    val calendarDays: List<AttendanceDayUi> = emptyList(),
    val dailyRecords: List<AttendanceDayUi> = emptyList(),
    val isLoading: Boolean = false,
)

data class AttendanceMonthOption(
    val key: String,
    val label: String,
)

data class AttendanceSummaryUi(
    val daysPresent: String = "—",
    val workedHours: String = "—",
    val lateArrivals: String = "—",
)

enum class AttendanceDayStatus {
    Present,
    HalfDay,
    Absent,
}

data class AttendanceDayUi(
    val dayNumber: Int,
    val monthShortName: String,
    val status: AttendanceDayStatus,
    val detail: String,
    val statusLabel: String,
    val timeBadge: String,
)
