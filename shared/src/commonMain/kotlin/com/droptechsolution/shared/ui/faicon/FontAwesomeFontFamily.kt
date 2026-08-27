package com.droptechsolution.shared.ui.faicon

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily

@Composable
expect fun rememberFaSolidFamily(): FontFamily

@Composable
fun FontAwesomeSolid(): FontFamily = rememberFaSolidFamily()

/**
 * API `uf1ea` -> `\uf1ea` -> same character as compile-time `"\uf1ea"`.
 */
fun String?.toFontAwesomeSolidText(): String? {
    if (isNullOrBlank()) return null

    val normalized = trim().lowercase()
    val escaped = when {
        normalized.startsWith("\\u") -> normalized
        normalized.startsWith("u") -> "\\u${normalized.drop(1)}"
        else -> "\\u$normalized"
    }

    val hex = escaped.removePrefix("\\u")
    if (hex.length != 4) return null

    return hex.toIntOrNull(16)?.toChar()?.toString()
}
