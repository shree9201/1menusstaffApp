package com.droptechsolution.shared.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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

    val formLabel = TextStyle(
        color = MenusText,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold
    )
}

val MenusTypography = Typography(
    headlineLarge = MenusTextStyles.landingTitle,
    bodyMedium = MenusTextStyles.landingParagraph,
    titleMedium = MenusTextStyles.loginTitle,
    labelMedium = MenusTextStyles.formLabel
)
