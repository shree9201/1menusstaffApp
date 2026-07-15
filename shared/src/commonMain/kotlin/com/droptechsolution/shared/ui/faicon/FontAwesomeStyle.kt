package com.droptechsolution.shared.ui.faicon

import _1menus.shared.generated.resources.Res
import _1menus.shared.generated.resources.fa_regular
import _1menus.shared.generated.resources.fa_solid
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import org.jetbrains.compose.resources.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
/**
 * Supported Font Awesome Style Families
 */
enum class FontAwesomeStyle {
    SOLID,
    REGULAR,
    LIGHT,
    BRANDS
}

/**
 * Creates Font Families referencing your project resources.
 * Make sure these match the files added to your commonMain/resources/font/ folder.
 */
@Composable
fun rememberFaSolidFamily(): FontFamily = FontFamily(
    Font(resource = Res.font.fa_solid, weight = FontWeight.Normal)
)

@Composable
fun rememberFaRegularFamily(): FontFamily = FontFamily(
    Font(resource = Res.font.fa_regular, weight = FontWeight.Normal)
)

/**
 * A highly reusable Composable that parses dynamic Font Awesome HTML classes
 * and outputs the correct icon instantly using Ligatures.
 *
 * @param htmlClass The raw HTML class sequence (e.g., "fal fa-luggage-cart", "fas fa-user")
 * @param modifier Custom styling adjustments
 * @param size Text sizing adjustments for the icon font
 * @param tint Colors applied directly onto the rendered vector icon glyph
 */
@Composable
fun FontAwesomeLigatureIcon(
    htmlClass: String,
    modifier: Modifier = Modifier,
    size: TextUnit = 24.sp,
    tint: Color = LocalContentColor.current
) {
    // 1. Sanitize input string and break it down
    val tokens = htmlClass.trim().split("\\s+".toRegex())
    if (tokens.isEmpty()) return

    val prefix = tokens.firstOrNull() ?: "fas"

    // 2. Find the class token containing "fa-" and drop the prefix part
    val rawIconToken = tokens.find { it.startsWith("fa-") } ?: return
    val iconName = rawIconToken.substringAfter("fa-") // Extracts "luggage-cart" from "fa-luggage-cart"

    // 3. Resolve style family directly from the web class prefix
    val fontFamily = when (prefix) {
        "fas" -> rememberFaSolidFamily()
        "far" -> rememberFaRegularFamily()
        // Connect additional families here if you imported light/brands files:
        // "fal" -> rememberFaLightFamily()
        // "fab" -> rememberFaBrandsFamily()
        else -> rememberFaSolidFamily() // Fallback safety
    }

    // 4. Render text directly. The OS font engine resolves "luggage-cart" as a single icon glyph.
    Text(
        text = iconName,
        fontFamily = fontFamily,
        fontSize = size,
        color = tint,
        modifier = modifier,
        maxLines = 1
    )
}