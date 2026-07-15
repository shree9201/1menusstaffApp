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
