package com.droptechsolution.shared.ui.faicon

private val FONT_AWESOME_CLASS_REGEX = Regex("""class=["']([^"']+)["']""", RegexOption.IGNORE_CASE)

/**
 * Converts API icon values to the class string expected by [FontAwesomeLigatureIcon].
 *
 * Examples:
 * - `<i class="fal fa-luggage-cart"></i>` -> `fal fa-luggage-cart`
 * - `fas fa-rocket` -> `fas fa-rocket`
 */
fun String?.toFontAwesomeHtmlClass(): String? {
    if (isNullOrBlank()) return null

    val trimmed = trim()
    if (!trimmed.contains('<')) return trimmed

    return FONT_AWESOME_CLASS_REGEX
        .find(trimmed)
        ?.groupValues
        ?.getOrNull(1)
        ?.trim()
        ?.takeIf { token -> token.contains("fa-") }
}

/**
 * Parses API unicode values like `uf59d` into a Font Awesome code point.
 */
fun String?.toFontAwesomeCodePoint(): Int? {
    if (isNullOrBlank()) return null

    val normalized = trim()
        .lowercase()
        .removePrefix("\\u")
        .removePrefix("0x")

    val hex = when {
        normalized.startsWith("u+") -> normalized.drop(2)
        normalized.startsWith("u") && normalized.length > 1 -> normalized.drop(1)
        else -> normalized
    }

    val codePoint = hex.toIntOrNull(16) ?: return null
    if (codePoint <= 0 || codePoint > 0xFFFF) return null
    return codePoint
}

fun String?.toFontAwesomeUnicodeChar(): Char? =
    toFontAwesomeCodePoint()?.let(::Char)

/**
 * Converts API unicode values like `uf59d` to a Text glyph (same as `"\uf59d"`).
 */
fun String?.toFontAwesomeGlyphText(): String? =
    toFontAwesomeCodePoint()?.let { codePoint -> Char(codePoint).toString() }
