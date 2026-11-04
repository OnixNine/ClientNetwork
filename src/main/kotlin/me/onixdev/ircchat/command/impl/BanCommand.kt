package me.onixdev.ircchat.command.impl

import me.onixdev.ircchat.Server
import me.onixdev.ircchat.command.api.Command
import me.onixdev.ircchat.command.api.CommandSender
import me.onixdev.ircchat.service.moderation.ParsedDuration
import me.onixdev.ircchat.service.moderation.PunishmentArguments

class BanCommand : Command(
    "ban",
    "Ban a user permanently or for a duration",
    "/ban <user> [duration] [reason]"
) {
    override fun execute(sender: CommandSender, args: List<String>) {
        val service = Server.INSTANCE.punishmentService
        if (service == null) {
            sender.sendMessage("Moderation service is not available")
            return
        }

        val parsed = PunishmentArguments.parse(args, defaultReason = "Banned by console")
        if (parsed == null) {
            sender.sendMessage("Usage: $usage")
            return
        }

        val durationMillis = (parsed.duration as? ParsedDuration.Temporary)?.millis
        val punishment = service.ban(parsed.target, sender.name, parsed.reason, durationMillis)
        sender.sendMessage("Banned '${parsed.target}' — ${punishment.describe()}")
    }
}
