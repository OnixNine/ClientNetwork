package me.onixdev.ircchat.command.impl

import dev.onix.EventManager
import me.onixdev.ircchat.Server
import me.onixdev.ircchat.command.api.Command
import me.onixdev.ircchat.util.events.ClientMessageSendEvent

class BroadCastCommand : Command("broadcast", "СОсао", "") {
    override fun execute(args: Array<String?>) {
        val message = args[0]
        for (entity in Server.INSTANCE.connectionDataManager!!.getAll()) {
            if (entity.authed) {
                val clonedEntity = entity
                clonedEntity.userName = ""
                clonedEntity.role = "SYSTEM"
                val event = ClientMessageSendEvent(clonedEntity, message.toString())
                EventManager.callEvent(event)
            }
        }
    }

}