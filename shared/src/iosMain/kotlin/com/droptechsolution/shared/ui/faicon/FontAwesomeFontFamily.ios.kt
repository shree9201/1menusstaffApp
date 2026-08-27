package com.droptechsolution.shared.ui.faicon

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import org.jetbrains.compose.resources.Font
import _1menus.shared.generated.resources.Res
import _1menus.shared.generated.resources.fa_solid

@Composable
actual fun rememberFaSolidFamily(): FontFamily {
    val font = Font(resource = Res.font.fa_solid, weight = FontWeight.Normal)
    return remember(font) { FontFamily(font) }
}
