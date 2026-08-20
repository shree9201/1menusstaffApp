package com.droptechsolution.shared.ui.profile.attendance

import com.droptechsolution.shared.outletinfo.model.api.staff.StaffAttendanceDetail
import com.droptechsolution.shared.ui.profile.attendance.models.AttendanceDayStatus
import com.droptechsolution.shared.ui.profile.attendance.models.AttendanceDayUi
import com.droptechsolution.shared.ui.profile.attendance.models.AttendanceMonthOption
import com.droptechsolution.shared.ui.profile.attendance.models.AttendanceSummaryUi
import com.droptechsolution.shared.ui.profile.attendance.models.MyAttendanceUiState
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime

private const val HALF_DAY_MINUTES = 240
private const val LATE_HOUR = 9
private const val LATE_MINUTE = 0

private val MONTH_NAMES = listOf(
    "January", "February", "March", "April", "May", "June",
    "July", "August", "September", "October", "November", "December",
)

object MyAttendanceMapper {
    fun buildUiState(
        selectedMonthKey: String,
        records: List<StaffAttendanceDetail>,
        isLoading: Boolean,
    ): MyAttendanceUiState {
        val availableMonths = recentMonthOptions()
        val monthKey = selectedMonthKey.ifBlank { availableMonths.firstOrNull()?.key.orEmpty() }
        val monthRecords = records.filter { it.date.startsWith(monthKeyToDatePrefix(monthKey)) }
        val groupedDays = groupRecordsByDate(monthRecords)
        val calendarDays = groupedDays.map { it.toAttendanceDayUi(monthKey) }
            .sortedByDescending { it.dayNumber }
        val dailyRecords = calendarDays

        return MyAttendanceUiState(
            selectedMonthLabel = monthKeyToLabel(monthKey),
            availableMonths = availableMonths,
            summary = buildSummary(monthKey, groupedDays),
            calendarDays = calendarDays,
            dailyRecords = dailyRecords,
            isLoading = isLoading,
        )
    }

    fun monthKey(year: Int, month: Int): String {
        val monthString = month.toString().padStart(2, '0')
        return "$year-$monthString"
    }

    fun recentMonthOptions(count: Int = 4): List<AttendanceMonthOption> {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        return (0 until count).map { offset ->
            val date = today.minus(DatePeriod(months = offset))
            val key = monthKey(date.year, date.month.number)
            AttendanceMonthOption(
                key = key,
                label = monthKeyToLabel(key),
            )
        }
    }

    private fun monthKeyToLabel(key: String): String {
        val parts = key.split("-")
        if (parts.size != 2) return key
        val year = parts[0].toIntOrNull() ?: return key
        val month = parts[1].toIntOrNull() ?: return key
        return "${MONTH_NAMES.getOrNull(month - 1) ?: key} $year"
    }

    private fun monthKeyToDatePrefix(key: String): String = key

    private fun buildSummary(
        monthKey: String,
        groupedDays: List<GroupedAttendanceDay>,
    ): AttendanceSummaryUi {
        val parts = monthKey.split("-")
        val year = parts.getOrNull(0)?.toIntOrNull() ?: return AttendanceSummaryUi()
        val month = parts.getOrNull(1)?.toIntOrNull() ?: return AttendanceSummaryUi()
        val daysInMonth = daysInMonth(year, month)
        val presentDays = groupedDays.count { it.status != AttendanceDayStatus.Absent }
        val workedMinutes = groupedDays.sumOf { it.totalMinutes }
        val lateArrivals = groupedDays.count { it.isLate }

        return AttendanceSummaryUi(
            daysPresent = "$presentDays/$daysInMonth",
            workedHours = "${workedMinutes.toWholeHours()}h",
            lateArrivals = lateArrivals.toString(),
        )
    }

    private fun daysInMonth(year: Int, month: Int): Int {
        val monthEnum = runCatching { Month(month) }.getOrNull() ?: return 30
        return when (monthEnum) {
            Month.FEBRUARY -> if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) 29 else 28
            Month.APRIL, Month.JUNE, Month.SEPTEMBER, Month.NOVEMBER -> 30
            else -> 31
        }
    }

    private fun groupRecordsByDate(records: List<StaffAttendanceDetail>): List<GroupedAttendanceDay> =
        records.groupBy { it.date }
            .map { (date, dayRecords) ->
                val totalMinutes = dayRecords.sumOf { it.totalWorkingMinutes }
                val earliestLogin = dayRecords.minOfOrNull { it.login_date_time }.orEmpty()
                val latestLogout = dayRecords.maxOfOrNull { it.logout_date_time }.orEmpty()
                val isLate = isLateLogin(earliestLogin)
                val status = when {
                    totalMinutes <= 0 -> AttendanceDayStatus.Absent
                    totalMinutes < HALF_DAY_MINUTES -> AttendanceDayStatus.HalfDay
                    else -> AttendanceDayStatus.Present
                }
                GroupedAttendanceDay(
                    date = date,
                    status = status,
                    totalMinutes = totalMinutes,
                    loginTime = formatTime(earliestLogin),
                    logoutTime = formatTime(latestLogout),
                    isLate = isLate,
                )
            }

    private fun GroupedAttendanceDay.toAttendanceDayUi(monthKey: String): AttendanceDayUi {
        val date = parseDate(date)
        val monthShortName = MONTH_NAMES.getOrNull(date?.month?.number?.minus(1) ?: -1)
            ?.take(3)
            .orEmpty()
        val detail = when (status) {
            AttendanceDayStatus.Absent -> "Absent"
            AttendanceDayStatus.HalfDay -> "$loginTime – $logoutTime"
            AttendanceDayStatus.Present -> "$loginTime – $logoutTime"
        }
        val statusLabel = when (status) {
            AttendanceDayStatus.Absent -> "Absent"
            AttendanceDayStatus.HalfDay -> "Half Day"
            AttendanceDayStatus.Present -> if (isLate) "Present · Late" else "Present"
        }
        val timeBadge = when (status) {
            AttendanceDayStatus.HalfDay -> "½"
            AttendanceDayStatus.Absent -> "✕"
            AttendanceDayStatus.Present -> loginTime
        }
        return AttendanceDayUi(
            dayNumber = date?.dayOfMonth ?: 0,
            monthShortName = monthShortName,
            status = status,
            detail = detail,
            statusLabel = statusLabel,
            timeBadge = timeBadge,
        )
    }

    private fun parseDate(value: String): LocalDate? =
        runCatching {
            val parts = value.split("-")
            LocalDate(parts[0].toInt(), parts[1].toInt(), parts[2].toInt())
        }.getOrNull()

    private fun formatTime(dateTime: String): String {
        val timePart = dateTime.substringAfter(' ', missingDelimiterValue = dateTime)
        val pieces = timePart.split(":")
        if (pieces.size < 2) return timePart
        return "${pieces[0]}:${pieces[1]}"
    }

    private fun isLateLogin(dateTime: String): Boolean {
        val parsed = runCatching {
            LocalDateTime.parse(dateTime.replace(' ', 'T'))
        }.getOrNull() ?: return false
        return parsed.hour > LATE_HOUR || (parsed.hour == LATE_HOUR && parsed.minute > LATE_MINUTE)
    }

    private fun Double.toWholeHours(): Int = kotlin.math.floor(this / 60.0).toInt()

    private data class GroupedAttendanceDay(
        val date: String,
        val status: AttendanceDayStatus,
        val totalMinutes: Double,
        val loginTime: String,
        val logoutTime: String,
        val isLate: Boolean,
    )
}
