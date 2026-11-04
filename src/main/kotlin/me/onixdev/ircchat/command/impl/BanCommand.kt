package me.onixdev.ircchat.command.impl

import me.onixdev.ircchat.Server
import me.onixdev.ircchat.command.api.Command
import me.onixdev.ircchat.command.api.CommandSender

class BanCommand : Command(
    "ban",
    "Ban a user and drop their session if online",
    "/ban <user> [reason]"
) {
    override fun execute(sender: CommandSender, args: List<String>) {
        if (!requireArgs(sender, args, min = 1)) return

        val target = args[0]
        val reason = args.drop(1).joinToString(" ").ifBlank { "Banned by console" }
        val dataBase = Server.INSTANCE.getDataBase()

        if (!dataBase.findByUserName(target).beforeJoined) {
            sender.sendMessage("Unknown user '$target'")
            return
        }

        dataBase.setBanned(target, true)
        Server.INSTANCE.connectionDataManager?.findAuthenticatedByUsername(target)?.let { entity ->
            entity.sendSystem("ERROR", "You were banned: $reason")
            entity.ctx.disconnect("Banned: $reason")
        }
        sender.sendMessage("Banned '$target' ($reason)")
    }
}
