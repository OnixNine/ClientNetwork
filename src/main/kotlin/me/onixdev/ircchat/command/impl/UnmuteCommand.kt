package me.onixdev.ircchat.command.impl

import me.onixdev.ircchat.Server
import me.onixdev.ircchat.command.api.Command
import me.onixdev.ircchat.command.api.CommandSender

class UnmuteCommand : Command(
    "unmute",
    "Lift a mute",
    "/unmute <user>"
) {
    override fun execute(sender: CommandSender, args: List<String>) {
        if (!requireArgs(sender, args, min = 1, max = 1)) return

        val service = Server.INSTANCE.punishmentService
        if (service == null) {
            sender.sendMessage("Moderation service is not available")
            return
        }

        val target = args[0]
        if (service.unmute(target)) {
            sender.sendMessage("Unmuted '$target'")
        } else {
            sender.sendMessage("'$target' has no active mute")
        }
    }
}
