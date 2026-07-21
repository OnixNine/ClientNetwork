/*package me.onixdev.ircchat.service.message.validation

import dev.onix.EventHandler
import dev.onix.EventManager
import dev.onix.types.Listener
import dev.onix.types.Priority
import me.onixdev.ircchat.service.packet.PacketFactory
import me.onixdev.ircchat.util.events.ClientMessageSendEvent

class MessageLengthLimit : Listener {
    init {
        EventManager.register(this)
    }
    @EventHandler(value = Priority.LOWEST)
    fun onMessage(event: ClientMessageSendEvent) {
        if (event.message.length > 256) {
            event.user.connection.send(PacketFactory.disconnectPacket("", "Ваше сообщение слишком длинное"))
            event.cancel()
        }
    }

}*/