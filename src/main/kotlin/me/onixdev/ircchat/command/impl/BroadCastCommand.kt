package me.onixdev.ircchat.command.impl

import me.onixdev.ircchat.Server
import me.onixdev.ircchat.base.entity.UserEntry
import me.onixdev.ircchat.command.api.Command
import me.onixdev.ircchat.command.api.CommandSender
import me.onixdev.ircchat.impl.s2.impl.ChatBroadcastPacket

class BroadCastCommand : Command(
    "broadcast",
    "Send a message to every authenticated client",
    "/broadcast <message>",
    aliases = listOf("bc")
) {
    override fun execute(sender: CommandSender, args: List<String>) {
        val message = args.joinToString(" ").trim()
        if (message.isEmpty()) {
            sender.sendMessage("Usage: $usage")
            return
        }
        val packet = ChatBroadcastPacket(UserEntry("SYSTEM", "admin"), message)
        Server.INSTANCE.connectionDataManager
            ?.getAll()
            ?.filter { it.authed }
            ?.forEach { it.sendPacket(packet) }
        sender.sendMessage("Broadcast sent: $message")
    }
}
