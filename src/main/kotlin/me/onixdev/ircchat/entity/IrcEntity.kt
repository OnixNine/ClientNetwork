// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 OnixNine
package me.onixdev.ircchat.entity

import me.onixdev.ircchat.base.entity.UserEntry
import me.onixdev.ircchat.impl.s2.impl.ChatBroadcastPacket
import me.onixdev.ircchat.impl.s2.impl.SystemMessagePacket
import ru.kseonyt.net.context.NetworkContext

class IrcEntity(
    val ctx: NetworkContext
) {
    var role: String = "user"
    var userName: String = ""
    var authed: Boolean = false
    var lastMessageTime: Long = 0
    var failedAuthAttempts: Int = 0

    fun sendPacket(packet: ru.kseonyt.net.packet.Packet) {
        ctx.send(packet)
    }

    fun sendMessage(author: UserEntry, message: String) {
        sendPacket(ChatBroadcastPacket(author, message))
    }

    fun sendSystem(level: String, message: String) {
        sendPacket(SystemMessagePacket(level, message))
    }

    fun toUserEntry(): UserEntry {
        return UserEntry(userName, role)
    }
}