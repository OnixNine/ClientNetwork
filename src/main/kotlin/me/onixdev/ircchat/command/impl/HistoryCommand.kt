// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 OnixNine
package me.onixdev.ircchat.command.impl

import me.onixdev.ircchat.Server
import me.onixdev.ircchat.command.api.Command
import me.onixdev.ircchat.command.api.CommandSender

class HistoryCommand : Command(
    "history",
    "Show every punishment a user has received",
    "/history <user>"
) {
    override fun execute(sender: CommandSender, args: List<String>) {
        if (!requireArgs(sender, args, min = 1, max = 1)) return

        val service = Server.INSTANCE.punishmentService
        if (service == null) {
            sender.sendMessage("Moderation service is not available")
            return
        }

        val target = args[0]
        val history = service.history(target)
        if (history.isEmpty()) {
            sender.sendMessage("No punishment history for '$target'")
            return
        }

        sender.sendMessage("History for '$target' (${history.size}):")
        history.forEach { punishment ->
            sender.sendMessage(
                "  #${punishment.id} ${punishment.type.name.lowercase()} " +
                    "by ${punishment.author} — ${punishment.reason} [${punishment.statusText()}]"
            )
        }
    }
}
