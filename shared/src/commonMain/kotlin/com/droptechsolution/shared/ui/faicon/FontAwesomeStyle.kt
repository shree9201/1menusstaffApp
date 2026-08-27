package com.droptechsolution.shared.ui.faicon

import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Renders a Font Awesome icon from HTML/class strings like
 * `<i class="fal fa-luggage-cart"></i>`.
 *
 * @return true when an icon glyph was rendered, false when lookup failed.
 */
@Composable
fun FontAwesomeLigatureIcon(
    htmlClass: String,
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    tint: Color = LocalContentColor.current,
): Boolean {
    val tokens = htmlClass.trim().split("\\s+".toRegex())
    if (tokens.isEmpty()) return false

    val iconName = tokens
        .find { it.startsWith("fa-") }
        ?.substringAfter("fa-")
        ?: return false

    val glyph = FontAwesomeUnicodeStore.resolve(iconName)?.toString() ?: return false
    FaGlyphText(
        glyph = glyph,
        modifier = modifier,
        size = size,
        tint = tint,
    )
    return true
}

/**
 * Renders a Font Awesome icon directly from API unicode values like `uf59d`.
 *
 * @return true when an icon glyph was rendered, false when parsing failed.
 */
@Composable
fun FontAwesomeUnicodeIcon(
    iconUnicode: String?,
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    tint: Color = LocalContentColor.current,
): Boolean {
    val glyph = iconUnicode.toFontAwesomeGlyphText() ?: return false
    FaGlyphText(
        glyph = glyph,
        modifier = modifier,
        size = size,
        tint = tint,
    )
    return true
}

@Composable
private fun FaGlyphText(
    glyph: String,
    modifier: Modifier,
    size: Dp,
    tint: Color,
) {
    Text(
        text = glyph,
        modifier = modifier,
        fontFamily = rememberFaSolidFamily(),
        fontSize = size.value.sp,
        color = tint,
    )
}
