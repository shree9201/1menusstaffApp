package com.droptechsolution.shared.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset

object MenusGradients {
    val appBackground = Brush.linearGradient(
        colors = listOf(
            Color(0xFF0F766E),
            MenusPrimary
        ),
        start = Offset.Zero,
        end = Offset.Infinite
    )

    val contentPanel = Brush.linearGradient(
        colors = listOf(
            Color(0xFF146D78),
            Color(0xFF1E5A9B),
            Color(0xFF1D4ED8)
        )
    )

    val actionButton = Brush.linearGradient(
        colors = listOf(
            Color(0xFF477DB2),
            Color(0xFF4369C4)
        )
    )
}
