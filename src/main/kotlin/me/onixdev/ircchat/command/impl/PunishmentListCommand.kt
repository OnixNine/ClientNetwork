// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 OnixNine
package me.onixdev.ircchat.command.impl

import me.onixdev.ircchat.Server
import me.onixdev.ircchat.command.api.Command
import me.onixdev.ircchat.command.api.CommandSender
import me.onixdev.ircchat.service.permission.Permission

class PunishmentListCommand : Command(
    "plist",
    "List all currently active bans and mutes",
    "/plist",
    aliases = listOf("punishments", "active"),
    permission = Permission.PLIST
) {
    override fun execute(sender: CommandSender, args: List<String>) {
        val service = Server.INSTANCE.punishmentService
        if (service == null) {
            sender.sendMessage("Moderation service is not available")
            return
        }

        val active = service.activePunishments()
        if (active.isEmpty()) {
            sender.sendMessage("No active punishments")
            return
        }

        sender.sendMessage("Active punishments (${active.size}):")
        active.forEach { punishment ->
            sender.sendMessage(
                "  ${punishment.type.name.lowercase()} ${punishment.userName} " +
                    "— ${punishment.reason} (${punishment.statusText()})"
            )
        }
    }
}
