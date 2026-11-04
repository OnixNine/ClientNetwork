// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 OnixNine
package me.onixdev.ircchat.service.moderation

import io.github.oshai.kotlinlogging.KotlinLogging
import me.onixdev.ircchat.manager.ConnectionDataManager
import me.onixdev.ircchat.service.database.DataBaseService
import java.sql.ResultSet

class PunishmentService(
    private val dataBaseService: DataBaseService,
    private val connectionDataManager: ConnectionDataManager,
    private val clock: () -> Long = System::currentTimeMillis
) {
    private val logger = KotlinLogging.logger("PunishmentService")
    private val db get() = dataBaseService.db

    fun ban(userName: String, author: String, reason: String, durationMillis: Long?): Punishment =
        apply(PunishmentType.BAN, userName, author, reason, durationMillis)

    fun mute(userName: String, author: String, reason: String, durationMillis: Long?): Punishment =
        apply(PunishmentType.MUTE, userName, author, reason, durationMillis)

    fun unban(userName: String): Boolean = revoke(PunishmentType.BAN, userName)

    fun unmute(userName: String): Boolean {
        val removed = revoke(PunishmentType.MUTE, userName)
        if (removed) {
            connectionDataManager.findAuthenticatedByUsername(userName)
                ?.sendSystem("INFO", "You were unmuted")
        }
        return removed
    }

    fun activeBan(userName: String): Punishment? = active(PunishmentType.BAN, userName)

    fun activeMute(userName: String): Punishment? = active(PunishmentType.MUTE, userName)

    fun isBanned(userName: String): Boolean = activeBan(userName) != null

    fun isMuted(userName: String): Boolean = activeMute(userName) != null

    fun history(userName: String): List<Punishment> =
        db.select().from("punishments")
            .where("username = ?", userName)
            .desc("id")
            .fetch { map(it) }

    fun activePunishments(): List<Punishment> {
        val now = clock()
        return db.select().from("punishments")
            .where("revoked = 0")
            .and("(expires_at IS NULL OR expires_at > ?)", now)
            .desc("id")
            .fetch { map(it) }
    }

    private fun apply(
        type: PunishmentType,
        userName: String,
        author: String,
        reason: String,
        durationMillis: Long?
    ): Punishment {
        val now = clock()
        val expiresAt = durationMillis?.let { now + it }

        var step = db.insert("punishments")
            .value("type", type.name)
            .value("username", userName)
            .value("reason", reason)
            .value("author", author)
            .value("issued_at", now)
            .value("revoked", 0)
        if (expiresAt != null) {
            step = step.value("expires_at", expiresAt)
        }
        step.execute()

        val punishment = Punishment(0, type, userName, reason, author, now, expiresAt, revoked = false)
        logger.info { "Applied ${type.name} to '$userName' by '$author' (${punishment.statusText(now)})" }
        notifyTarget(punishment, now)
        return punishment
    }

    private fun revoke(type: PunishmentType, userName: String): Boolean {
        val affected = db.update("punishments")
            .set("revoked", 1)
            .where("username = ? AND type = ? AND revoked = 0", userName, type.name)
            .execute()
        if (affected > 0) {
            logger.info { "Revoked ${type.name} for '$userName'" }
        }
        return affected > 0
    }

    private fun active(type: PunishmentType, userName: String): Punishment? {
        val now = clock()
        return db.select().from("punishments")
            .where("username = ?", userName)
            .and("type = ?", type.name)
            .and("revoked = 0")
            .and("(expires_at IS NULL OR expires_at > ?)", now)
            .desc("id")
            .fetchFirst { map(it) }
    }

    private fun notifyTarget(punishment: Punishment, now: Long) {
        val entity = connectionDataManager.findAuthenticatedByUsername(punishment.userName) ?: return
        when (punishment.type) {
            PunishmentType.BAN -> {
                entity.sendSystem("ERROR", "You were banned: ${punishment.reason} (${punishment.statusText(now)})")
                entity.ctx.disconnect("Banned: ${punishment.reason}")
            }

            PunishmentType.MUTE -> {
                entity.sendSystem("WARN", "You were muted: ${punishment.reason} (${punishment.statusText(now)})")
            }
        }
    }

    private fun map(rs: ResultSet): Punishment = Punishment(
        id = rs.getInt("id"),
        type = PunishmentType.valueOf(rs.getString("type")),
        userName = rs.getString("username"),
        reason = rs.getString("reason"),
        author = rs.getString("author"),
        issuedAt = rs.getLong("issued_at"),
        expiresAt = rs.getObject("expires_at")?.let { rs.getLong("expires_at") },
        revoked = rs.getInt("revoked") == 1
    )
}
