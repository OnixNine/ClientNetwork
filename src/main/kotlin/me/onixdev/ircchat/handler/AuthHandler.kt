package me.onixdev.ircchat.handler

import io.github.oshai.kotlinlogging.KotlinLogging
import me.onixdev.ircchat.Server
import me.onixdev.ircchat.impl.c2.impl.AuthRequestPacket
import me.onixdev.ircchat.impl.s2.impl.AuthResultPacket
import me.onixdev.ircchat.impl.s2.impl.KeyExchangePacket
import me.onixdev.ircchat.manager.ConnectionDataManager
import me.onixdev.ircchat.service.auth.Hash
import me.onixdev.ircchat.service.database.DataBaseService
import me.onixdev.ircchat.service.moderation.PunishmentService
import ru.kseonyt.net.annotation.PacketHandler
import ru.kseonyt.net.context.NetworkContext
import java.util.Base64
import javax.crypto.SecretKey

class AuthHandler(
    private val hash: Hash,
    private val dataBaseService: DataBaseService,
    private val connectionDataManager: ConnectionDataManager,
    private val punishmentService: PunishmentService,
    private val aesKeyProvider: () -> SecretKey = { Server.INSTANCE.getAesKey() },
    private val onAuthenticated: (NetworkContext) -> Unit = {}
) {
    private val logger = KotlinLogging.logger("AuthHandler")
    private val authLock = Any()

    companion object {
        const val LOGIN_SUCCESS = 100
        const val REGISTERED = 101
        const val INVALID_PASSWORD = 201
        const val ALREADY_LOGGED_IN = 202
        const val BANNED = 203

        const val MAX_AUTH_ATTEMPTS = 3
    }

    @PacketHandler
    fun onAuthRequest(pkt: AuthRequestPacket, ctx: NetworkContext) {
        val entity = connectionDataManager.getConnection(ctx) ?: return

        synchronized(authLock) {
            if (entity.authed) {
                logger.warn {
                    "Ignoring re-auth from ${ctx.remoteAddress} (already authenticated as ${entity.userName})"
                }
                return
            }

            val active = connectionDataManager.findAuthenticatedByUsername(pkt.username)
            if (active != null && active.ctx !== ctx) {
                logger.warn { "Rejected login for ${pkt.username} from ${ctx.remoteAddress}: session already active" }
                ctx.send(AuthResultPacket(pkt.sender, ALREADY_LOGGED_IN, "Session already active", "user", pkt.username))
                ctx.disconnect("Session already active")
                return
            }

            val user = dataBaseService.findByUserName(pkt.username)

            val activeBan = punishmentService.activeBan(pkt.username)
            if (activeBan != null) {
                logger.warn { "Banned user ${pkt.username} tried to authenticate from ${ctx.remoteAddress}" }
                ctx.send(AuthResultPacket(pkt.sender, BANNED, activeBan.banMessage(), "user", pkt.username))
                ctx.disconnect("Banned")
                return
            }

            if (!user.beforeJoined) {
                dataBaseService.create(pkt.username, hash.hashPassword(pkt.password))
                entity.userName = pkt.username
                entity.role = "user"
                entity.authed = true
                entity.failedAuthAttempts = 0
                ctx.send(AuthResultPacket(pkt.sender, REGISTERED, "Registered", entity.role, pkt.username))
                sendKeyExchange(ctx)
                onAuthenticated(ctx)
                logger.info { "User ${pkt.username} registered" }
            } else if (hash.verifyPassword(pkt.password, user.passWord)) {
                entity.userName = user.userName
                entity.role = user.role
                entity.authed = true
                entity.failedAuthAttempts = 0
                ctx.send(AuthResultPacket(pkt.sender, LOGIN_SUCCESS, "Auth Success!", entity.role, pkt.username))
                sendKeyExchange(ctx)
                onAuthenticated(ctx)
                logger.info { "User ${pkt.username} authenticated" }
            } else {
                entity.authed = false
                entity.failedAuthAttempts++
                ctx.send(AuthResultPacket(pkt.sender, INVALID_PASSWORD, "Invalid Password!", "user", pkt.username))
                logger.warn {
                    "Failed auth attempt for ${pkt.username} " +
                        "(${entity.failedAuthAttempts}/$MAX_AUTH_ATTEMPTS) from ${ctx.remoteAddress}"
                }
                if (entity.failedAuthAttempts >= MAX_AUTH_ATTEMPTS) {
                    logger.warn { "Too many failed auth attempts from ${ctx.remoteAddress}, closing connection" }
                    ctx.disconnect("Too many failed auth attempts")
                }
            }
        }
    }

    private fun sendKeyExchange(ctx: NetworkContext) {
        val key = aesKeyProvider()
        val keyBase64 = Base64.getEncoder().encodeToString(key.encoded)
        ctx.send(KeyExchangePacket("SERVER", keyBase64))
    }
}
