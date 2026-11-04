// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 OnixNine
package me.onixdev.ircchat.handler

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import me.onixdev.ircchat.impl.c2.impl.ClientChatMessagePacket
import me.onixdev.ircchat.impl.s2.impl.SystemMessagePacket
import me.onixdev.ircchat.manager.ConnectionDataManager
import me.onixdev.ircchat.service.moderation.Punishment
import me.onixdev.ircchat.service.moderation.PunishmentService
import me.onixdev.ircchat.service.moderation.PunishmentType
import org.junit.jupiter.api.Test
import ru.kseonyt.net.context.NetworkContext
import ru.kseonyt.net.packet.Packet

class ChatHandlerTest {

    private val connections = ConnectionDataManager()
    private val punishmentService: PunishmentService = mockk(relaxed = true)
    private val handler = ChatHandler(connections, punishmentService)

    @Test
    fun `a muted user's message is dropped and explained`() {
        val ctx = mockk<NetworkContext>(relaxed = true)
        connections.addConnection(ctx)
        connections.getConnection(ctx)!!.apply {
            userName = "alice"
            authed = true
        }
        every { punishmentService.activeMute("alice") } returns Punishment(
            id = 1,
            type = PunishmentType.MUTE,
            userName = "alice",
            reason = "flood",
            author = "CONSOLE",
            issuedAt = 0,
            expiresAt = null,
            revoked = false
        )

        handler.onChatMessage(ClientChatMessagePacket("fabric", "hi", "alice"), ctx)

        verify { ctx.send(match<SystemMessagePacket> { it.level() == "WARN" }) }
    }

    @Test
    fun `an unauthorised connection message is ignored`() {
        val ctx = mockk<NetworkContext>(relaxed = true)
        connections.addConnection(ctx)

        handler.onChatMessage(ClientChatMessagePacket("fabric", "hi", "alice"), ctx)

        verify(exactly = 0) { ctx.send(any<Packet>()) }
    }
}
