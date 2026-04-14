package me.onixdev.ircchat.service.message.validation

import dev.onix.EventHandler
import dev.onix.EventManager
import dev.onix.types.Listener
import dev.onix.types.Priority
import me.onixdev.ircchat.service.packet.PacketFactory
import me.onixdev.ircchat.util.events.ClientMessageSendEvent
import java.nio.charset.StandardCharsets

class MessageValidationPattern : Listener {

    private val MAX_MESSAGE_BYTES = 1024

    private val ALLOWED_PATTERN = Regex("^[\\p{IsCyrillic}\\p{IsLatin}\\d\\s.,!?\\-()]+$")

    init {
        EventManager.register(this)
        println("Registered")
    }

    @EventHandler(value = Priority.LOWEST)
    fun onMessage(event: ClientMessageSendEvent) {
        val message = event.message
        println("message $message")
        if (message.isBlank()) {
            event.cancel()
            return
        }

        if (!ALLOWED_PATTERN.matches(message)) {
            event.user.connection.send(PacketFactory.disconnectPacket("", "Сообщение содержит запрещенные символы. Используйте только русский, английский язык или цифры."))
            event.cancel()
            return
        }

        if (message.length > 512) {
            event.user.connection.send(PacketFactory.disconnectPacket("", "Сообщение слишком длинное."))
            event.cancel()
            return
        }

        val byteSize = message.toByteArray(StandardCharsets.UTF_8).size
        if (byteSize > MAX_MESSAGE_BYTES) {
            event.user.connection.send(PacketFactory.disconnectPacket("", "Сообщение превышает лимит размера."))
            event.cancel()
            return
        }
    }
}