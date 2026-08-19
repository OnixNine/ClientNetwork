package me.onixdev.ircchat.handler

import io.github.oshai.kotlinlogging.KotlinLogging
import me.onixdev.ircchat.impl.c2.impl.AuthRequestPacket
import me.onixdev.ircchat.impl.s2.impl.AuthResultPacket
import me.onixdev.ircchat.manager.ConnectionDataManager
import me.onixdev.ircchat.service.auth.Hash
import me.onixdev.ircchat.service.database.DataBaseService
import ru.kseonyt.net.annotation.NetworkListener
import ru.kseonyt.net.annotation.PacketHandler
import ru.kseonyt.net.context.NetworkContext

@NetworkListener
class AuthHandler(
    private val hash: Hash,
    private val dataBaseService: DataBaseService,
    private val connectionDataManager: ConnectionDataManager
) {
    private val logger = KotlinLogging.logger("AuthHandler")

    @PacketHandler
    fun onAuthRequest(pkt: AuthRequestPacket, ctx: NetworkContext) {
        val entity = connectionDataManager.getConnection(ctx) ?: return

        val user = dataBaseService.findByUserName(pkt.username)
        if (!user.beforeJoined) {
            dataBaseService.create(pkt.username, hash.hashPassword(pkt.password))
            entity.userName = user.userName
            entity.role = "user"
            entity.authed = true
            ctx.send(AuthResultPacket(pkt.sender(), 101, "Registered", entity.role, pkt.username))
            logger.info { "User ${pkt.username} registered" }
        } else if (hash.verifyPassword(pkt.password, user.passWord)) {
            entity.userName = user.userName
            entity.role = user.role
            entity.authed = true
            ctx.send(AuthResultPacket(pkt.sender(), 100, "Auth Success!", entity.role, pkt.username))
            logger.info { "User ${pkt.username} authenticated" }
        } else {
            entity.authed = false
            ctx.send(AuthResultPacket(pkt.sender(), 201, "Invalid Password!", "user", pkt.username))
            logger.warn { "Failed auth attempt for ${pkt.username}" }
        }
    }
}
