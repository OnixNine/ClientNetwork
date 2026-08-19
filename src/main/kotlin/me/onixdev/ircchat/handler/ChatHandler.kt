package me.onixdev.ircchat.handler

import dev.onix.EventManager
import me.onixdev.ircchat.impl.c2.impl.ClientChatMessagePacket
import me.onixdev.ircchat.manager.ConnectionDataManager
import me.onixdev.ircchat.util.events.ClientMessageSendEvent
import ru.kseonyt.net.annotation.PacketHandler
import ru.kseonyt.net.context.NetworkContext

class ChatHandler(
    private val connectionDataManager: ConnectionDataManager
) {
    @PacketHandler
    fun onChatMessage(pkt: ClientChatMessagePacket, ctx: NetworkContext) {
        val entity = connectionDataManager.getConnection(ctx) ?: return
        if (!entity.authed) return

        val event = ClientMessageSendEvent(entity, pkt.message)
        EventManager.callEvent(event)
        if (event.isCancelled) return
    }
}
