package me.onixdev.ircchat.command.impl

import me.onixdev.ircchat.Server
import me.onixdev.ircchat.command.api.Command
import me.onixdev.ircchat.command.api.CommandSender

class UnbanCommand : Command(
    "unban",
    "Lift a ban so the user can authenticate again",
    "/unban <user>"
) {
    override fun execute(sender: CommandSender, args: List<String>) {
        if (!requireArgs(sender, args, min = 1, max = 1)) return

        val target = args[0]
        val dataBase = Server.INSTANCE.getDataBase()

        if (!dataBase.findByUserName(target).beforeJoined) {
            sender.sendMessage("Unknown user '$target'")
            return
        }

        dataBase.setBanned(target, false)
        sender.sendMessage("Unbanned '$target'")
    }
}
