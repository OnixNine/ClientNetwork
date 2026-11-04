package me.onixdev.ircchat.command.impl

import me.onixdev.ircchat.Server
import me.onixdev.ircchat.command.api.Command
import me.onixdev.ircchat.command.api.CommandSender

class KickCommand : Command(
    "kick",
    "Disconnect an online user without banning them",
    "/kick <user> [reason]"
) {
    override fun execute(sender: CommandSender, args: List<String>) {
        if (!requireArgs(sender, args, min = 1)) return

        val target = args[0]
        val reason = args.drop(1).joinToString(" ").ifBlank { "Kicked by console" }

        val entity = Server.INSTANCE.connectionDataManager?.findAuthenticatedByUsername(target)
        if (entity == null) {
            sender.sendMessage("User '$target' is not online")
            return
        }

        entity.sendSystem("WARN", "You were kicked: $reason")
        entity.ctx.disconnect("Kicked: $reason")
        sender.sendMessage("Kicked '$target' ($reason)")
    }
}
