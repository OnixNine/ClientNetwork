// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 OnixNine
package me.onixdev.ircchat.handler

import dev.onix.EventManager
import me.onixdev.ircchat.command.api.CommandManager
import me.onixdev.ircchat.command.api.PlayerSender
import me.onixdev.ircchat.impl.c2.impl.ClientChatMessagePacket
import me.onixdev.ircchat.manager.ConnectionDataManager
import me.onixdev.ircchat.service.moderation.PunishmentService
import me.onixdev.ircchat.util.events.ClientMessageSendEvent
import ru.kseonyt.net.annotation.NetworkListener
import ru.kseonyt.net.annotation.PacketHandler
import ru.kseonyt.net.context.NetworkContext

@NetworkListener
class ChatHandler(
    private val connectionDataManager: ConnectionDataManager,
    private val punishmentService: PunishmentService,
    private val commandManager: CommandManager
) {
    @PacketHandler
    fun onChatMessage(pkt: ClientChatMessagePacket, ctx: NetworkContext) {
        val entity = connectionDataManager.getConnection(ctx) ?: return
        if (!entity.authed) return

        val mute = punishmentService.activeMute(entity.userName)
        if (mute != null) {
            entity.sendSystem("WARN", "You are muted: ${mute.reason} (${mute.statusText()})")
            return
        }

        val line = pkt.message.trim()
        if (line.startsWith(COMMAND_PREFIX)) {
            commandManager.dispatch(PlayerSender(entity), line)
            return
        }

        val event = ClientMessageSendEvent(entity, pkt.message)
        EventManager.callEvent(event)
        if (event.isCancelled) return
    }

    companion object {
        const val COMMAND_PREFIX = "!"
    }
}
