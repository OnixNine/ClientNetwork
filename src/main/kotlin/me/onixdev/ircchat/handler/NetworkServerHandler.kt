package me.onixdev.ircchat.handler

import dev.onix.EventManager
import io.github.oshai.kotlinlogging.KotlinLogging
import me.onixdev.ircchat.impl.c2.impl.AuthRequestPacket
import me.onixdev.ircchat.impl.c2.impl.ClientChatMessagePacket
import me.onixdev.ircchat.impl.s2.impl.AuthResultPacket
import me.onixdev.ircchat.manager.ConnectionDataManager
import me.onixdev.ircchat.service.database.DataBaseService
import me.onixdev.ircchat.util.config.BaseConfig
import me.onixdev.ircchat.util.events.ClientMessageSendEvent
import ru.kseonyt.net.annotation.PacketHandler
import ru.kseonyt.net.context.NetworkContext

class NetworkServerHandler(
    private val config: BaseConfig,
    private val dataBaseService: DataBaseService,
    private val connectionDataManager: ConnectionDataManager
) {
    private val logger = KotlinLogging.logger("ServerHandler")

    @PacketHandler
    fun onAuthRequest(pkt: AuthRequestPacket, ctx: NetworkContext) {
        val entity = connectionDataManager.getConnection(ctx) ?: return

        val user = dataBaseService.findByUserName(pkt.username)
        if (!user.beforeJoined) {
            dataBaseService.create(pkt.username, config.hash.hashPassword(pkt.password))
            entity.userName = user.userName
            entity.role = "user"
            entity.authed = true
            ctx.send(AuthResultPacket(pkt.sender(), 101, "Registered", entity.role, pkt.username))
            logger.info{"User ${pkt.username} registered"}
        } else if (config.hash.verifyPassword(pkt.password, user.passWord)) {
            entity.userName = user.userName
            entity.role = user.role
            entity.authed = true
            ctx.send(AuthResultPacket(pkt.sender(),100, "Auth Success!", entity.role, pkt.username))
            logger.info{"User ${pkt.username} authenticated"}
        } else {
            entity.authed = false
            ctx.send(AuthResultPacket(pkt.sender(),201, "Invalid Password!", "user", pkt.username))
            logger.warn{"Failed auth attempt for ${pkt.username}"}
        }
    }

    @PacketHandler
    fun onChatMessage(pkt: ClientChatMessagePacket, ctx: NetworkContext) {
        val entity = connectionDataManager.getConnection(ctx) ?: return
        if (!entity.authed) return

        val event = ClientMessageSendEvent(entity, pkt.message)
        EventManager.callEvent(event)
        if (event.isCancelled) return
    }

    fun onConnect(ctx: NetworkContext) {
        connectionDataManager.addConnection(ctx)
        logger.info {"New connection from ${ctx.remoteAddress}"}
    }

    fun onDisconnect(ctx: NetworkContext) {
        connectionDataManager.removeConnection(ctx)
        logger.info {"Connection closed from ${ctx.remoteAddress}"}
    }
}