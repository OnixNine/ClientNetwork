// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 OnixNine
package me.onixdev.ircchat.command.impl

import me.onixdev.ircchat.Server
import me.onixdev.ircchat.command.api.Command
import me.onixdev.ircchat.command.api.CommandSender

class RoleCommand : Command(
    "role",
    "Change a user's role (e.g. user, admin)",
    "/role <user> <role>",
    aliases = listOf("setrole")
) {
    override fun execute(sender: CommandSender, args: List<String>) {
        if (!requireArgs(sender, args, min = 2, max = 2)) return

        val target = args[0]
        val role = args[1]
        val dataBase = Server.INSTANCE.getDataBase()

        if (!dataBase.findByUserName(target).beforeJoined) {
            sender.sendMessage("Unknown user '$target'")
            return
        }

        dataBase.updateRole(target, role)
        Server.INSTANCE.connectionDataManager?.findAuthenticatedByUsername(target)?.role = role
        sender.sendMessage("Role of '$target' updated to '$role'")
    }
}
