// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 OnixNine
package me.onixdev.ircchat.service.permission

enum class Role(val display: String, val permissions: Set<Permission>) {
    USER(
        "user",
        setOf(Permission.CHAT)
    ),
    MODERATOR(
        "moderator",
        setOf(
            Permission.CHAT,
            Permission.KICK,
            Permission.MUTE,
            Permission.UNMUTE,
            Permission.HISTORY,
            Permission.PLIST
        )
    ),
    ADMIN(
        "admin",
        Permission.entries.toSet()
    );

    fun has(permission: Permission): Boolean = permission in permissions

    companion object {
        private val ALIASES = mapOf(
            "user" to USER,
            "player" to USER,
            "moder" to MODERATOR,
            "mod" to MODERATOR,
            "moderator" to MODERATOR,
            "admin" to ADMIN,
            "administrator" to ADMIN
        )

        fun parse(value: String?): Role? = ALIASES[value?.trim()?.lowercase()]

        fun fromString(value: String?): Role = parse(value) ?: USER
    }
}
