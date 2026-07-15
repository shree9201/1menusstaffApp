package com.droptechsolution.shared.ui.faicon

import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font
import _1menus.shared.generated.resources.Res
import _1menus.shared.generated.resources.fa_regular
import _1menus.shared.generated.resources.fa_solid

@Composable
fun rememberFaSolidFamily(): FontFamily {
    val font = Font(resource = Res.font.fa_solid, weight = FontWeight.Black)
    return remember(font) { FontFamily(font) }
}

@Composable
fun rememberFaRegularFamily(): FontFamily {
    val font = Font(resource = Res.font.fa_regular, weight = FontWeight.Normal)
    return remember(font) { FontFamily(font) }
}

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

    val prefix = tokens.firstOrNull().orEmpty()
    val iconName = tokens
        .find { it.startsWith("fa-") }
        ?.substringAfter("fa-")
        ?: return false

    val iconChar = FontAwesomeUnicodeStore.resolve(iconName) ?: return false
    val fontFamily = when (prefix) {
        "far", "fal" -> rememberFaRegularFamily()
        else -> rememberFaSolidFamily()
    }
    val iconStr = iconChar.toString()
    println("icon to displayed ${iconStr}")

    Text(
        text = iconChar.toString(),
        modifier = modifier.size(size),
        style = TextStyle(
            fontFamily = fontFamily,
            fontSize = (size.value).sp,
            fontWeight = if (prefix == "far" || prefix == "fal") FontWeight.Normal else FontWeight.Black,
            color = tint,
            textAlign = TextAlign.Center,
        ),
        maxLines = 1,
        softWrap = false,
    )
    return true
}
