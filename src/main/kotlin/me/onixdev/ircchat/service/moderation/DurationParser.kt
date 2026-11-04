package me.onixdev.ircchat.service.moderation

/** Result of parsing a duration token such as "1d", "30m" or "perm". */
sealed interface ParsedDuration {
    data class Temporary(val millis: Long) : ParsedDuration
    data object Permanent : ParsedDuration
    data object NotADuration : ParsedDuration
}

/**
 * Parses and formats punishment durations.
 *
 * Accepted forms: `30s`, `10m`, `2h`, `3d`, `1w`, a bare number (minutes),
 * and `perm` / `permanent` / `forever` / `never` / `-1` for a permanent one.
 */
object DurationParser {

    private val UNITS = mapOf(
        "s" to 1_000L,
        "m" to 60_000L,
        "h" to 3_600_000L,
        "d" to 86_400_000L,
        "w" to 604_800_000L
    )

    private val PERMANENT = setOf("perm", "permanent", "forever", "never", "-1")

    private val PATTERN = Regex("^(\\d+)([smhdw])$")

    fun parse(token: String): ParsedDuration {
        val value = token.trim().lowercase()
        if (value.isEmpty()) return ParsedDuration.NotADuration
        if (value in PERMANENT) return ParsedDuration.Permanent

        PATTERN.matchEntire(value)?.let { match ->
            val amount = match.groupValues[1].toLong()
            return ParsedDuration.Temporary(amount * UNITS.getValue(match.groupValues[2]))
        }

        if (value.all { it.isDigit() }) {
            return ParsedDuration.Temporary(value.toLong() * 60_000L)
        }
        return ParsedDuration.NotADuration
    }

    /** Human readable form, e.g. "1d 2h 3m". */
    fun format(millis: Long): String {
        var seconds = millis / 1_000
        if (seconds <= 0) return "0s"

        val days = seconds / 86_400; seconds %= 86_400
        val hours = seconds / 3_600; seconds %= 3_600
        val minutes = seconds / 60; val secs = seconds % 60

        val parts = mutableListOf<String>()
        if (days > 0) parts += "${days}d"
        if (hours > 0) parts += "${hours}h"
        if (minutes > 0) parts += "${minutes}m"
        if (secs > 0 && days == 0L) parts += "${secs}s"
        return parts.joinToString(" ").ifEmpty { "0s" }
    }
}
