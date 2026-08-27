package com.droptechsolution.shared.ui.faicon

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily

private const val FONT_AWESOME_SOLID_PATH = "fonts/fontawesome_solid.ttf"

@Composable
actual fun rememberFaSolidFamily(): FontFamily {
    LocalFontAwesomeSolid.current?.let { return it }

    val assetManager = LocalContext.current.assets
    return remember(assetManager) {
        FontFamily(
            Font(FONT_AWESOME_SOLID_PATH, assetManager),
        )
    }
}
