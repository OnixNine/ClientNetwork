package me.onixdev.ircchat.service.message.validation

import dev.onix.EventHandler
import dev.onix.EventManager
import dev.onix.types.Listener
import dev.onix.types.Priority
import me.onixdev.ircchat.util.events.ClientMessageSendEvent

class MessageValidationPattern : Listener {

    private val ALLOWED_PATTERN = Regex("^[\\p{IsCyrillic}\\p{IsLatin}\\d\\s.,!?\\-:;()\\[\\]]+$")

    init {
        EventManager.register(this)
    }

    @EventHandler(value = Priority.HIGHEST)
    fun onMessage(event: ClientMessageSendEvent) {
        if (event.isCancelled) return
        val message = event.message
        if (message.isBlank() || !ALLOWED_PATTERN.matches(message)) {
            event.cancel()
        }
    }
}
