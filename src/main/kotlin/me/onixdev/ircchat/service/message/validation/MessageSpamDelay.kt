package me.onixdev.ircchat.service.message.validation

import dev.onix.EventHandler
import dev.onix.EventManager
import dev.onix.types.Listener
import dev.onix.types.Priority
import me.onixdev.ircchat.util.events.ClientMessageSendEvent

class MessageSpamDelay : Listener {

    private val DELAY_MS = 500L

    init {
        EventManager.register(this)
    }

    @EventHandler(value = Priority.HIGHEST)
    fun onMessage(event: ClientMessageSendEvent) {
        if (event.isCancelled) return
        val now = System.currentTimeMillis()
        if (now - event.user.lastMessageTime < DELAY_MS) {
            event.cancel()
            return
        }
        event.user.lastMessageTime = now
    }
}
