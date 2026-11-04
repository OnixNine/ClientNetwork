package me.onixdev.ircchat.handler

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import me.onixdev.ircchat.impl.c2.impl.AuthRequestPacket
import me.onixdev.ircchat.impl.s2.impl.AuthResultPacket
import me.onixdev.ircchat.manager.ConnectionDataManager
import me.onixdev.ircchat.service.auth.Hash
import me.onixdev.ircchat.service.database.DataBaseService
import me.onixdev.ircchat.service.database.TempedIrcEntity
import me.onixdev.ircchat.service.moderation.Punishment
import me.onixdev.ircchat.service.moderation.PunishmentService
import me.onixdev.ircchat.service.moderation.PunishmentType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import ru.kseonyt.net.context.NetworkContext
import ru.kseonyt.net.packet.Packet
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

class AuthHandlerTest {

    private val hash: Hash = mockk()
    private val dataBaseService: DataBaseService = mockk()
    private val manager = ConnectionDataManager()
    private val punishmentService: PunishmentService = mockk(relaxed = true)
    private val keyProvider: () -> SecretKey = { SecretKeySpec(ByteArray(16), "AES") }
    private val handler = AuthHandler(hash, dataBaseService, manager, punishmentService, keyProvider)

    init {
        every { punishmentService.activeBan(any()) } returns null
    }

    private fun ctx(): NetworkContext = mockk(relaxed = true)

    private fun registeredUser(passwordHash: String = "hash"): TempedIrcEntity =
        TempedIrcEntity("alice", passwordHash, "user", beforeJoined = true, banned = false)

    @Test
    fun `repeated auth on an authenticated connection is ignored`() {
        val c = ctx()
        manager.addConnection(c)
        manager.getConnection(c)!!.apply {
            userName = "alice"
            authed = true
        }

        handler.onAuthRequest(AuthRequestPacket("cli", "alice", "pw"), c)

        verify(exactly = 0) { c.send(any<Packet>()) }
        verify(exactly = 0) { c.disconnect(any()) }
        verify(exactly = 0) { dataBaseService.findByUserName(any()) }
        assertTrue(manager.getConnection(c)!!.authed)
    }

    @Test
    fun `second session for the same account is rejected`() {
        val first = ctx()
        manager.addConnection(first)
        manager.getConnection(first)!!.apply {
            userName = "alice"
            authed = true
        }

        val second = ctx()
        manager.addConnection(second)

        handler.onAuthRequest(AuthRequestPacket("cli", "alice", "pw"), second)

        verify { second.send(match<AuthResultPacket> { it.code() == AuthHandler.ALREADY_LOGGED_IN }) }
        verify { second.disconnect("Session already active") }
        assertFalse(manager.getConnection(second)!!.authed)
        verify(exactly = 0) { dataBaseService.findByUserName(any()) }
    }

    @Test
    fun `banned account is rejected`() {
        val c = ctx()
        manager.addConnection(c)
        every { dataBaseService.findByUserName("alice") } returns registeredUser()
        every { punishmentService.activeBan("alice") } returns Punishment(
            id = 1,
            type = PunishmentType.BAN,
            userName = "alice",
            reason = "spam",
            author = "CONSOLE",
            issuedAt = 0,
            expiresAt = null,
            revoked = false
        )

        handler.onAuthRequest(AuthRequestPacket("cli", "alice", "pw"), c)

        verify { c.send(match<AuthResultPacket> { it.code() == AuthHandler.BANNED }) }
        verify { c.disconnect("Banned") }
        assertFalse(manager.getConnection(c)!!.authed)
    }

    @Test
    fun `wrong password is rejected and counted`() {
        val c = ctx()
        manager.addConnection(c)
        every { dataBaseService.findByUserName("alice") } returns registeredUser()
        every { hash.verifyPassword("wrong", "hash") } returns false

        handler.onAuthRequest(AuthRequestPacket("cli", "alice", "wrong"), c)

        verify { c.send(match<AuthResultPacket> { it.code() == AuthHandler.INVALID_PASSWORD }) }
        assertEquals(1, manager.getConnection(c)!!.failedAuthAttempts)
        verify(exactly = 0) { c.disconnect(any()) }
    }

    @Test
    fun `connection is closed after too many failed attempts`() {
        val c = ctx()
        manager.addConnection(c)
        every { dataBaseService.findByUserName("alice") } returns registeredUser()
        every { hash.verifyPassword(any(), any()) } returns false

        repeat(AuthHandler.MAX_AUTH_ATTEMPTS) {
            handler.onAuthRequest(AuthRequestPacket("cli", "alice", "wrong"), c)
        }

        verify { c.disconnect("Too many failed auth attempts") }
        assertEquals(AuthHandler.MAX_AUTH_ATTEMPTS, manager.getConnection(c)!!.failedAuthAttempts)
    }

    @Test
    fun `successful login marks the session authenticated`() {
        val c = ctx()
        manager.addConnection(c)
        every { dataBaseService.findByUserName("alice") } returns registeredUser()
        every { hash.verifyPassword("pw", "hash") } returns true
        every { hash.hashPassword(any()) } returns "hashed"

        handler.onAuthRequest(AuthRequestPacket("cli", "alice", "pw"), c)

        verify { c.send(match<AuthResultPacket> { it.code() == AuthHandler.LOGIN_SUCCESS }) }
        val entity = manager.getConnection(c)!!
        assertTrue(entity.authed)
        assertEquals("alice", entity.userName)
        assertNotNull(manager.findAuthenticatedByUsername("alice"))
    }
}
