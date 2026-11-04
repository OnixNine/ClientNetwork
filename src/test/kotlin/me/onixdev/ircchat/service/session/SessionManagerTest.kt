package me.onixdev.ircchat.service.session

import io.mockk.mockk
import io.mockk.verify
import me.onixdev.ircchat.impl.s2.impl.KeepAlivePacket
import me.onixdev.ircchat.impl.s2.impl.SystemMessagePacket
import me.onixdev.ircchat.manager.ConnectionDataManager
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import ru.kseonyt.net.context.NetworkContext

class SessionManagerTest {

    private val connections = ConnectionDataManager()
    private var now = 0L
    private val manager = SessionManager(
        connections,
        clock = { now },
        idGenerator = { 42L }
    )

    private fun ctx(): NetworkContext = mockk(relaxed = true)

    private fun connect(authed: Boolean): NetworkContext {
        val c = ctx()
        connections.addConnection(c)
        connections.getConnection(c)!!.authed = authed
        manager.onConnected(c)
        return c
    }

    @Test
    fun `unauthenticated connection is dropped after the auth timeout`() {
        val c = connect(authed = false)

        now = SessionManager.DEFAULT_AUTH_TIMEOUT_MILLIS - 1
        manager.tick()
        verify(exactly = 0) { c.disconnect(any()) }

        now = SessionManager.DEFAULT_AUTH_TIMEOUT_MILLIS
        manager.tick()
        verify { c.send(match<SystemMessagePacket> { it.level() == "WARN" }) }
        verify { c.disconnect("Authentication timeout") }
    }

    @Test
    fun `authenticated connection is pinged after the keep-alive interval`() {
        val c = connect(authed = true)

        now = SessionManager.DEFAULT_KEEP_ALIVE_INTERVAL_MILLIS - 1
        manager.tick()
        verify(exactly = 0) { c.send(any<KeepAlivePacket>()) }

        now = SessionManager.DEFAULT_KEEP_ALIVE_INTERVAL_MILLIS
        manager.tick()
        verify { c.send(match<KeepAlivePacket> { it.id() == 42L }) }
    }

    @Test
    fun `keep-alive reply clears the outstanding ping`() {
        val c = connect(authed = true)
        now = SessionManager.DEFAULT_KEEP_ALIVE_INTERVAL_MILLIS
        manager.tick()

        assertTrue(manager.onKeepAlive(c, 42L))

        // Only the reply time has moved: the next ping is a full interval away.
        now += SessionManager.DEFAULT_KEEP_ALIVE_INTERVAL_MILLIS / 2
        manager.tick()
        verify(exactly = 1) { c.send(any<KeepAlivePacket>()) }
        verify(exactly = 0) { c.disconnect(any()) }
    }

    @Test
    fun `a forged keep-alive id is ignored`() {
        val c = connect(authed = true)
        now = SessionManager.DEFAULT_KEEP_ALIVE_INTERVAL_MILLIS
        manager.tick()

        assertFalse(manager.onKeepAlive(c, 99L))
    }

    @Test
    fun `a hung client is dropped after the keep-alive timeout`() {
        val c = connect(authed = true)

        now = SessionManager.DEFAULT_KEEP_ALIVE_INTERVAL_MILLIS
        manager.tick() // ping sent, now awaiting a reply

        now += SessionManager.DEFAULT_KEEP_ALIVE_TIMEOUT_MILLIS - 1
        manager.tick()
        verify(exactly = 0) { c.disconnect(any()) }

        now += 1
        manager.tick()
        verify { c.send(match<SystemMessagePacket> { it.level() == "ERROR" }) }
        verify { c.disconnect("Keep-alive timeout") }
    }

    @Test
    fun `a timed-out connection is not disconnected again on the next tick`() {
        val c = connect(authed = false)
        now = SessionManager.DEFAULT_AUTH_TIMEOUT_MILLIS
        manager.tick()
        manager.tick()

        verify(exactly = 1) { c.disconnect("Authentication timeout") }
    }

    @Test
    fun `keep-alive reply after disconnect is rejected`() {
        val c = connect(authed = true)
        now = SessionManager.DEFAULT_KEEP_ALIVE_INTERVAL_MILLIS
        manager.tick()

        manager.onDisconnected(c)

        assertFalse(manager.onKeepAlive(c, 42L))
    }
}
