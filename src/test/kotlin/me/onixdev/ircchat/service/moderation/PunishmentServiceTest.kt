package me.onixdev.ircchat.service.moderation

import io.mockk.mockk
import io.mockk.verify
import me.onixdev.ircchat.manager.ConnectionDataManager
import me.onixdev.ircchat.service.database.DataBaseService
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import ru.kseonyt.net.context.NetworkContext
import java.io.File

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PunishmentServiceTest {

    private lateinit var dataBase: DataBaseService
    private lateinit var connections: ConnectionDataManager
    private lateinit var service: PunishmentService
    private var now = 1_000_000L
    private val dbFile = File("test_punishments.db")

    @BeforeAll
    fun setUpAll() {
        if (dbFile.exists()) dbFile.delete()
        dataBase = DataBaseService(dbFile.name)
    }

    @AfterAll
    fun tearDownAll() {
        dbFile.delete()
    }

    @BeforeEach
    fun setUp() {
        now = 1_000_000L
        connections = ConnectionDataManager()
        service = PunishmentService(dataBase, connections) { now }
        dataBase.db.connection().prepareStatement("DELETE FROM punishments").executeUpdate()
    }

    @AfterEach
    fun clearTable() {
        dataBase.db.connection().prepareStatement("DELETE FROM punishments").executeUpdate()
    }

    @Test
    fun `permanent ban is active`() {
        service.ban("alice", "CONSOLE", "spam", null)

        assertTrue(service.isBanned("alice"))
        assertNotNull(service.activeBan("alice"))
    }

    @Test
    fun `temporary ban expires`() {
        service.ban("alice", "CONSOLE", "spam", 60_000)

        assertTrue(service.isBanned("alice"))
        now += 60_001
        assertFalse(service.isBanned("alice"))
    }

    @Test
    fun `unban revokes exactly once`() {
        service.ban("alice", "CONSOLE", "spam", null)

        assertTrue(service.unban("alice"))
        assertFalse(service.isBanned("alice"))
        assertFalse(service.unban("alice"))
    }

    @Test
    fun `mute can be lifted`() {
        service.mute("bob", "CONSOLE", "flood", null)
        assertTrue(service.isMuted("bob"))

        assertTrue(service.unmute("bob"))
        assertFalse(service.isMuted("bob"))
    }

    @Test
    fun `bans and mutes do not interfere`() {
        service.ban("carol", "CONSOLE", "ban", null)
        service.mute("carol", "CONSOLE", "mute", null)
        service.unban("carol")

        assertFalse(service.isBanned("carol"))
        assertTrue(service.isMuted("carol"))
    }

    @Test
    fun `history is newest first`() {
        service.ban("dave", "CONSOLE", "one", null)
        now += 1_000
        service.mute("dave", "CONSOLE", "two", null)

        val history = service.history("dave")

        assertEquals(2, history.size)
        assertEquals(PunishmentType.MUTE, history[0].type)
        assertEquals(PunishmentType.BAN, history[1].type)
    }

    @Test
    fun `active list excludes expired and revoked`() {
        service.ban("erin", "CONSOLE", "perm", null)
        service.mute("frank", "CONSOLE", "short", 1_000)
        service.ban("gina", "CONSOLE", "lifted", null)
        service.unban("gina")
        now += 1_001

        val active = service.activePunishments()

        assertTrue(active.any { it.userName == "erin" })
        assertFalse(active.any { it.userName == "frank" })
        assertFalse(active.any { it.userName == "gina" })
    }

    @Test
    fun `banning an online user disconnects them`() {
        val ctx = mockk<NetworkContext>(relaxed = true)
        connections.addConnection(ctx)
        connections.getConnection(ctx)!!.apply {
            userName = "henry"
            authed = true
        }

        service.ban("henry", "CONSOLE", "bye", null)

        verify { ctx.disconnect(match { it.startsWith("Banned") }) }
    }
}
