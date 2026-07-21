/*package me.onixdev.ircchat.service.message

import dev.onix.EventHandler
import dev.onix.EventManager
import dev.onix.types.Listener
import dev.onix.types.Priority
import me.onixdev.ircchat.Server
import me.onixdev.ircchat.util.events.ClientMessageSendEvent

class BroadCastMessageService : Listener {
    init {
        EventManager.register(this)
    }
    @EventHandler(value = Priority.LOWEST)
    fun onMessage(event: ClientMessageSendEvent) {
        if (event.isCancelled) return
        println("BRD")
        Server.INSTANCE.connectionDataManager.getAll().forEach { entity->
            if (entity.authed) entity.sendMessage(event.message,event.author,event.role)
        }
    }
}*/