package me.onixdev.ircchat.service.message.validation

import dev.onix.EventHandler
import dev.onix.EventManager
import dev.onix.types.Listener
import dev.onix.types.Priority
import me.onixdev.ircchat.util.events.ClientMessageSendEvent
import java.nio.charset.StandardCharsets

class MessageLengthLimit : Listener {

    private val MAX_LENGTH = 512
    private val MAX_BYTES = 1024

    init {
        EventManager.register(this)
    }

    @EventHandler(value = Priority.HIGHEST)
    fun onMessage(event: ClientMessageSendEvent) {
        if (event.isCancelled) return
        if (event.message.length > MAX_LENGTH) {
            event.cancel()
            return
        }
        if (event.message.toByteArray(StandardCharsets.UTF_8).size > MAX_BYTES) {
            event.cancel()
        }
    }
}
