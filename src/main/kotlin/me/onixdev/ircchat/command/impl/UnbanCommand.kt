// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 OnixNine
package me.onixdev.ircchat.command.impl

import me.onixdev.ircchat.Server
import me.onixdev.ircchat.command.api.Command
import me.onixdev.ircchat.command.api.CommandSender
import me.onixdev.ircchat.service.permission.Permission

class UnbanCommand : Command(
    "unban",
    "Lift a ban so the user can authenticate again",
    "/unban <user>",
    permission = Permission.UNBAN
) {
    override fun execute(sender: CommandSender, args: List<String>) {
        if (!requireArgs(sender, args, min = 1, max = 1)) return

        val service = Server.INSTANCE.punishmentService
        if (service == null) {
            sender.sendMessage("Moderation service is not available")
            return
        }

        val target = args[0]
        if (service.unban(target)) {
            sender.sendMessage("Unbanned '$target'")
        } else {
            sender.sendMessage("'$target' has no active ban")
        }
    }
}
