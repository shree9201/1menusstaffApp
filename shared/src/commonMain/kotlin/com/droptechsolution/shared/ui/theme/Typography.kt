package com.droptechsolution.shared.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp

object MenusTextStyles {
    val landingTitle = TextStyle(
        color = MenusText,
        fontSize = 26.sp,
        fontWeight = FontWeight.ExtraBold,
        textAlign = TextAlign.Center
    )

    val landingParagraph = TextStyle(
        color = MenusTextMuted,
        fontSize = 14.sp,
        lineHeight = 21.sp,
        textAlign = TextAlign.Start
    )

    val landingParagraphMuted = TextStyle(
        color = TextMuted,
        fontSize = 14.sp,
        lineHeight = 21.sp,
        textAlign = TextAlign.Start
    )

    val landingCenterParagraph = TextStyle(
        color = MenusTextMuted,
        fontSize = 14.sp,
        lineHeight = 21.sp,
        textAlign = TextAlign.Center
    )

    val loginTitle = TextStyle(
        color = MenusText,
        fontSize = 18.sp,
        fontWeight = FontWeight.ExtraBold
    )

    val loginTitleDark = TextStyle(
        color = BLACK,
        fontSize = 18.sp,
        fontWeight = FontWeight.ExtraBold
    )

    val formLabel = TextStyle(
        color = MenusText,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold
    )

    val formLabelDark = TextStyle(
        color = BLACK,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold
    )

    val sectionTitle = TextStyle(
        color = BLACK,
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
    )

    val attendanceSummaryLabel = TextStyle(
        color = TextMuted,
        fontSize = 9.sp,
    )

    val attendanceSummaryValue = TextStyle(
        color = BLACK,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
    )

    val attendanceMonthLabel = TextStyle(
        color = Color(0xFF374151),
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
    )

    val attendanceCalendarHeader = TextStyle(
        color = AttendanceCalendarHeader,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
    )

    val attendanceCellDay = TextStyle(
        color = Color(0xFF111827),
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
    )

    val attendanceCellDetail = TextStyle(
        color = AttendanceCellDetail,
        fontSize = 11.sp,
        lineHeight = 15.sp,
    )

    val attendanceLegend = TextStyle(
        color = AttendanceCellDetail,
        fontSize = 12.sp,
    )

    val attendanceDailyTitle = TextStyle(
        color = BLACK,
        fontSize = 15.sp,
        fontWeight = FontWeight.SemiBold,
    )

    val attendanceDailySubtitle = TextStyle(
        color = TextMuted,
        fontSize = 13.sp,
    )
}

val MenusTypography = Typography(
    headlineLarge = MenusTextStyles.landingTitle,
    bodyMedium = MenusTextStyles.landingParagraph,
    titleMedium = MenusTextStyles.loginTitle,
    labelMedium = MenusTextStyles.formLabel
)
