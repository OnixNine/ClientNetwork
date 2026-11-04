package me.onixdev.ircchat.handler

import me.onixdev.ircchat.impl.s2.impl.KeepAlivePacket
import me.onixdev.ircchat.service.session.SessionManager
import ru.kseonyt.net.annotation.NetworkListener
import ru.kseonyt.net.annotation.PacketHandler
import ru.kseonyt.net.context.NetworkContext

/**
 * Receives keep-alive replies from clients and feeds them to the
 * [SessionManager] so a hung connection can be detected and dropped.
 */
@NetworkListener
class KeepAliveHandler(
    private val sessionManager: SessionManager
) {
    @PacketHandler
    fun onKeepAlive(pkt: KeepAlivePacket, ctx: NetworkContext) {
        sessionManager.onKeepAlive(ctx, pkt.id())
    }
}
