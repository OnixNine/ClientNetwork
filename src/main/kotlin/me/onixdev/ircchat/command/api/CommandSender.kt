// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 OnixNine
package me.onixdev.ircchat.command.api

import me.onixdev.ircchat.service.permission.Permission
import me.onixdev.ircchat.service.permission.Role

interface CommandSender {
    val name: String
    val role: Role
    fun sendMessage(message: String)
    fun hasPermission(permission: Permission): Boolean = role.has(permission)
}

object ConsoleSender : CommandSender {
    override val name: String = "CONSOLE"
    override val role: Role = Role.ADMIN
    override fun sendMessage(message: String) = println(message)
}
