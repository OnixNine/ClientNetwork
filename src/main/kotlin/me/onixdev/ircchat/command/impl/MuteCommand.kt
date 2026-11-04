// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 OnixNine
package me.onixdev.ircchat.command.impl

import me.onixdev.ircchat.Server
import me.onixdev.ircchat.command.api.Command
import me.onixdev.ircchat.command.api.CommandSender
import me.onixdev.ircchat.service.moderation.ParsedDuration
import me.onixdev.ircchat.service.moderation.PunishmentArguments

class MuteCommand : Command(
    "mute",
    "Mute a user permanently or for a duration",
    "/mute <user> [duration] [reason]"
) {
    override fun execute(sender: CommandSender, args: List<String>) {
        val service = Server.INSTANCE.punishmentService
        if (service == null) {
            sender.sendMessage("Moderation service is not available")
            return
        }

        val parsed = PunishmentArguments.parse(args, defaultReason = "Muted by console")
        if (parsed == null) {
            sender.sendMessage("Usage: $usage")
            return
        }

        val durationMillis = (parsed.duration as? ParsedDuration.Temporary)?.millis
        val punishment = service.mute(parsed.target, sender.name, parsed.reason, durationMillis)
        sender.sendMessage("Muted '${parsed.target}' — ${punishment.describe()}")
    }
}
