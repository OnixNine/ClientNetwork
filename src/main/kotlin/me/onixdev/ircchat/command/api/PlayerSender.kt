// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 OnixNine
package me.onixdev.ircchat.command.api

import me.onixdev.ircchat.entity.IrcEntity
import me.onixdev.ircchat.service.permission.Role

class PlayerSender(private val entity: IrcEntity) : CommandSender {
    override val name: String get() = entity.userName
    override val role: Role get() = entity.roleEnum
    override fun sendMessage(message: String) = entity.sendSystem("INFO", message)
}
