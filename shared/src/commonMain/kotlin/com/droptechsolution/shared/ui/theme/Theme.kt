package com.droptechsolution.shared.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val LightMenusColorScheme = lightColorScheme(
    primary = MenusPrimary,
    onPrimary = MenusOnMain,
    primaryContainer = Color(0xFFDBEAFE),
    onPrimaryContainer = Color(0xFF0F172A),
    secondary = MenusSecondary,
    onSecondary = MenusOnMain,
    secondaryContainer = Color(0xFFDCFCE7),
    onSecondaryContainer = Color(0xFF052E16),
    tertiary = MenusTertiary,
    onTertiary = MenusOnMain,
    tertiaryContainer = Color(0xFFFEF3C7),
    onTertiaryContainer = Color(0xFF451A03),
    error = MenusError,
    onError = MenusOnMain,
    background = Color(0xFFFFFFFF),
    onBackground = Color(0xFF111827),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF111827),
    surfaceVariant = Color(0xFFF3F4F6),
    onSurfaceVariant = Color(0xFF374151),
    outline = Color(0xFFD1D5DB)
)

val DarkMenusColorScheme = darkColorScheme(
    primary = MenusPrimary,
    onPrimary = MenusOnMain,
    primaryContainer = MenusTeal,
    onPrimaryContainer = MenusOnMain,
    secondary = MenusSecondary,
    onSecondary = MenusOnMain,
    tertiary = MenusTertiary,
    onTertiary = MenusOnMain,
    error = MenusError,
    onError = MenusOnMain,
    background = Color(0xFF0F172A),
    onBackground = Color(0xFFF8FAFC),
    surface = Color(0xFF111827),
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = Color(0xFF1F2937),
    onSurfaceVariant = Color(0xFFD1D5DB),
    outline = Color(0xFF4B5563)
)

object MenusExtendedColors {
    val success = MenusSuccess
    val onSuccess = MenusOnMain
    val warning = MenusWarning
    val onWarning = MenusOnMain
    val teal = MenusTeal
    val onTeal = MenusOnMain
}

@Composable
fun MenusTheme(
    darkTheme: Boolean = false,
    colorScheme: ColorScheme = if (darkTheme) DarkMenusColorScheme else LightMenusColorScheme,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = colorScheme,
        typography = MenusTypography,
        content = content
    )
}
