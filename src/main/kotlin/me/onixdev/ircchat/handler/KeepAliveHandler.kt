package me.onixdev.ircchat.handler

import me.onixdev.ircchat.impl.s2.impl.KeepAlivePacket
import me.onixdev.ircchat.service.session.SessionManager
import ru.kseonyt.net.annotation.NetworkListener
import ru.kseonyt.net.annotation.PacketHandler
import ru.kseonyt.net.context.NetworkContext

@NetworkListener
class KeepAliveHandler(
    private val sessionManager: SessionManager
) {
    @PacketHandler
    fun onKeepAlive(pkt: KeepAlivePacket, ctx: NetworkContext) {
        sessionManager.onKeepAlive(ctx, pkt.id())
    }
}
