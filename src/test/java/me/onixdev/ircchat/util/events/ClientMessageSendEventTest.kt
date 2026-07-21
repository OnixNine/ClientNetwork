package me.onixdev.ircchat.util.events

import io.mockk.*
import me.onixdev.ircchat.entity.IrcEntity
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ClientMessageSendEventTest {

    private fun createEvent(
        message: String = "hello",
        author: String = "user1",
        role: String = "admin"
    ): ClientMessageSendEvent {
        val entity = mockk<IrcEntity>(relaxed = true)
        return ClientMessageSendEvent(entity, message, author, role)
    }

    @Test
    fun `event is not cancelled by default`() {
        val event = createEvent()
        assertFalse(event.isCancelled)
    }

    @Test
    fun `cancel sets isCancelled to true`() {
        val event = createEvent()
        event.cancel()
        assertTrue(event.isCancelled)
    }

    @Test
    fun `event stores message`() {
        val event = createEvent(message = "test message")
        assertEquals("test message", event.message)
    }

    @Test
    fun `event stores author`() {
        val event = createEvent(author = "admin_user")
        assertEquals("admin_user", event.author)
    }

    @Test
    fun `event stores role`() {
        val event = createEvent(role = "moderator")
        assertEquals("moderator", event.role)
    }

    @Test
    fun `event stores user reference`() {
        val entity = mockk<IrcEntity>()
        val event = ClientMessageSendEvent(entity, "msg", "author", "role")
        assertSame(entity, event.user)
    }

    @Test
    fun `cancel is idempotent`() {
        val event = createEvent()
        event.cancel()
        event.cancel()
        assertTrue(event.isCancelled)
    }
}
