package me.onixdev.ircchat.service.database

import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import java.io.File

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DataBaseServiceTest {

    private lateinit var service: DataBaseService
    private val dbFile = File("test_app.db")

    @BeforeAll
    fun setUp() {
        if (dbFile.exists()) dbFile.delete()
        service = DataBaseService()
    }

    @AfterAll
    fun tearDown() {
        dbFile.delete()
        File("app.db").delete()
    }

    @AfterEach
    fun cleanTable() {
        service.db.connection().prepareStatement("DELETE FROM users").executeUpdate()
    }

    @Test
    fun `create and findByUserName returns correct entity`() {
        service.create("testuser", "hashed_pass")
        val entity = service.findByUserName("testuser")

        assertEquals("testuser", entity.userName)
        assertEquals("hashed_pass", entity.passWord)
        assertEquals("user", entity.role)
        assertTrue(entity.beforeJoined)
        assertFalse(entity.banned)
    }

    @Test
    fun `findByUserName returns default entity for unknown user`() {
        val entity = service.findByUserName("nonexistent")
        assertEquals("invalid", entity.userName)
        assertFalse(entity.beforeJoined)
    }

    @Test
    fun `create multiple users`() {
        service.create("user1", "pass1")
        service.create("user2", "pass2")

        val e1 = service.findByUserName("user1")
        val e2 = service.findByUserName("user2")

        assertEquals("user1", e1.userName)
        assertEquals("user2", e2.userName)
    }

    @Test
    fun `updateRole changes role`() {
        service.create("roleuser", "pass")
        service.updateRole("roleuser", "admin")
        val entity = service.findByUserName("roleuser")

        assertEquals("admin", entity.role)
    }

    @Test
    fun `findById returns correct entity`() {
        service.create("findme", "pass")
        val all = service.db.select().from("users").where("username = ?", "findme").fetchFirst {
            it.getInt("id")
        }
        val entity = service.findById(all)

        assertEquals("findme", entity.userName)
    }

    @Test
    fun `create sets joined to true`() {
        service.create("joineduser", "pass")
        val entity = service.findByUserName("joineduser")
        assertTrue(entity.beforeJoined)
    }

    @Test
    fun `create sets banned to false`() {
        service.create("notbanned", "pass")
        val entity = service.findByUserName("notbanned")
        assertFalse(entity.banned)
    }
}
