package me.onixdev.ircchat.command.impl

import me.onixdev.ircchat.Server
import me.onixdev.ircchat.base.entity.UserEntry
import me.onixdev.ircchat.command.api.Command
import me.onixdev.ircchat.impl.s2.impl.ChatBroadcastPacket

class BroadCastCommand : Command("broadcast", "Broadcast a system message", "/broadcast <message>") {
    override fun execute(args: Array<String?>) {
        val message = args.filterNotNull().joinToString(" ")
        if (message.isBlank()) {
            print("Usage: /broadcast <message>")
            return
        }
        val packet = ChatBroadcastPacket(UserEntry("SYSTEM", "admin"), message)
        Server.INSTANCE.getNetworkServer()?.broadcast(packet)
        println("Broadcast sent: $message")
    }
}