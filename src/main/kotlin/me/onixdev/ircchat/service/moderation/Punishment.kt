package me.onixdev.ircchat.service.moderation

/**
 * A single moderation entry. [expiresAt] is null for permanent punishments and
 * [revoked] marks ones that were lifted manually.
 */
data class Punishment(
    val id: Int,
    val type: PunishmentType,
    val userName: String,
    val reason: String,
    val author: String,
    val issuedAt: Long,
    val expiresAt: Long?,
    val revoked: Boolean
) {
    fun isPermanent(): Boolean = expiresAt == null

    fun isExpired(now: Long = System.currentTimeMillis()): Boolean =
        expiresAt != null && expiresAt <= now

    fun isActive(now: Long = System.currentTimeMillis()): Boolean =
        !revoked && !isExpired(now)

    fun remainingMillis(now: Long = System.currentTimeMillis()): Long? =
        expiresAt?.let { (it - now).coerceAtLeast(0) }

    fun remainingText(now: Long = System.currentTimeMillis()): String =
        remainingMillis(now)?.let { DurationParser.format(it) } ?: "permanent"

    fun statusText(now: Long = System.currentTimeMillis()): String = when {
        revoked -> "revoked"
        isExpired(now) -> "expired"
        isPermanent() -> "permanent"
        else -> "expires in ${DurationParser.format(remainingMillis(now)!!)}"
    }

    fun describe(now: Long = System.currentTimeMillis()): String =
        "${type.name.lowercase()}: $reason by $author (${statusText(now)})"

    fun banMessage(now: Long = System.currentTimeMillis()): String =
        "You are banned: $reason by $author (${statusText(now)})"
}
