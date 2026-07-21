package me.onixdev.ircchat.util.events

import io.mockk.*
import me.onixdev.ircchat.entity.IrcEntity
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import ru.kseonyt.net.context.NetworkContext

class ClientMessageSendEventTest {

    private fun createEvent(
        message: String = "hello",
        author: String = "user1",
        role: String = "admin"
    ): ClientMessageSendEvent {
        val ctx = mockk<NetworkContext>(relaxed = true)
        val entity = IrcEntity(ctx).apply {
            userName = author
            this.role = role
        }
        return ClientMessageSendEvent(entity, message)
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
    fun `event stores role`() {
        val event = createEvent(role = "moderator")
        val user = event.user
        assertEquals("moderator", user.role)
    }

    @Test
    fun `cancel is idempotent`() {
        val event = createEvent()
        event.cancel()
        event.cancel()
        assertTrue(event.isCancelled)
    }
}
