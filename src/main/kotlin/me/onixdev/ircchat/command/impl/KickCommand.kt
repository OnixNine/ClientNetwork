// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 OnixNine
package me.onixdev.ircchat.command.impl

import me.onixdev.ircchat.Server
import me.onixdev.ircchat.command.api.Command
import me.onixdev.ircchat.command.api.CommandSender
import me.onixdev.ircchat.service.permission.Permission

class KickCommand : Command(
    "kick",
    "Disconnect an online user without banning them",
    "/kick <user> [reason]",
    permission = Permission.KICK
) {
    override fun execute(sender: CommandSender, args: List<String>) {
        if (!requireArgs(sender, args, min = 1)) return

        val target = args[0]
        val reason = args.drop(1).joinToString(" ").ifBlank { "Kicked by ${sender.name}" }

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
