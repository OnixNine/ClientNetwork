package me.onixdev.ircchat.service.session

import io.github.oshai.kotlinlogging.KotlinLogging
import me.onixdev.ircchat.impl.s2.impl.KeepAlivePacket
import me.onixdev.ircchat.manager.ConnectionDataManager
import ru.kseonyt.net.context.NetworkContext
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ThreadLocalRandom

/**
 * Owns the lifetime rules of a connection:
 *
 *  - a connection that never authenticates is dropped after [authTimeoutMillis];
 *  - authenticated connections are pinged with [KeepAlivePacket] every
 *    [keepAliveIntervalMillis] and dropped if they do not answer within
 *    [keepAliveTimeoutMillis] (hung / stalled / half-open clients).
 *
 * The manager is passive: the caller runs [tick] on a timer. This keeps it free
 * of threading concerns and makes it trivial to unit-test with a fake clock.
 */
class SessionManager(
    private val connectionDataManager: ConnectionDataManager,
    private val authTimeoutMillis: Long = DEFAULT_AUTH_TIMEOUT_MILLIS,
    private val keepAliveIntervalMillis: Long = DEFAULT_KEEP_ALIVE_INTERVAL_MILLIS,
    private val keepAliveTimeoutMillis: Long = DEFAULT_KEEP_ALIVE_TIMEOUT_MILLIS,
    private val clock: () -> Long = System::currentTimeMillis,
    private val idGenerator: () -> Long = { ThreadLocalRandom.current().nextLong() }
) {
    private val logger = KotlinLogging.logger("SessionManager")

    private data class Session(
        var keepAliveSentAt: Long = 0,
        var awaitingKeepAlive: Boolean = false,
        var pendingId: Long = 0
    )

    private val sessions = ConcurrentHashMap<NetworkContext, Session>()
    private val connectedAt = ConcurrentHashMap<NetworkContext, Long>()

    /** Connections already kicked by this manager, until onDisconnected clears them. */
    private val closed = ConcurrentHashMap.newKeySet<NetworkContext>()

    companion object {
        /** A connection must authenticate within this window. */
        const val DEFAULT_AUTH_TIMEOUT_MILLIS = 30_000L

        /** How often an authenticated connection is pinged. */
        const val DEFAULT_KEEP_ALIVE_INTERVAL_MILLIS = 10_000L

        /** How long an authenticated connection may stay silent after a ping. */
        const val DEFAULT_KEEP_ALIVE_TIMEOUT_MILLIS = 30_000L
    }

    fun onConnected(ctx: NetworkContext) {
        val now = clock()
        connectedAt[ctx] = now
        sessions[ctx] = Session(keepAliveSentAt = now)
    }

    fun onAuthenticated(ctx: NetworkContext) {
        // Reset the keep-alive baseline so the first ping is a full interval away.
        sessions[ctx] = Session(keepAliveSentAt = clock())
    }

    fun onDisconnected(ctx: NetworkContext) {
        sessions.remove(ctx)
        connectedAt.remove(ctx)
        closed.remove(ctx)
    }

    /**
     * Handles a keep-alive reply. Returns true when [id] matches the ping that
     * is currently outstanding for [ctx]; stale or forged ids are ignored.
     */
    fun onKeepAlive(ctx: NetworkContext, id: Long): Boolean {
        val session = sessions[ctx] ?: return false
        if (!session.awaitingKeepAlive || session.pendingId != id) return false
        session.awaitingKeepAlive = false
        return true
    }

    /** Called periodically (see the timer in [me.onixdev.ircchat.Server]). */
    fun tick() {
        val now = clock()
        for (entity in connectionDataManager.getAll()) {
            val ctx = entity.ctx
            if (closed.contains(ctx)) continue

            if (!entity.authed) {
                val since = connectedAt[ctx] ?: now
                if (now - since >= authTimeoutMillis) {
                    logger.warn { "Authentication timeout for ${ctx.remoteAddress}, closing connection" }
                    entity.sendSystem("WARN", "Authentication timeout")
                    ctx.disconnect("Authentication timeout")
                    closed.add(ctx)
                }
                continue
            }

            val session = sessions.computeIfAbsent(ctx) { Session(keepAliveSentAt = now) }

            if (session.awaitingKeepAlive) {
                if (now - session.keepAliveSentAt >= keepAliveTimeoutMillis) {
                    logger.warn { "Keep-alive timeout for ${ctx.remoteAddress} (${entity.userName}), closing connection" }
                    entity.sendSystem("ERROR", "Keep-alive timeout")
                    ctx.disconnect("Keep-alive timeout")
                    closed.add(ctx)
                }
            } else if (now - session.keepAliveSentAt >= keepAliveIntervalMillis) {
                val id = idGenerator()
                session.pendingId = id
                session.awaitingKeepAlive = true
                session.keepAliveSentAt = now
                entity.sendPacket(KeepAlivePacket(id))
            }
        }
    }
}
