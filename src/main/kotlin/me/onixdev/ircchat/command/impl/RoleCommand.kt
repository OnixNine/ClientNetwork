// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 OnixNine
package me.onixdev.ircchat.command.impl

import me.onixdev.ircchat.Server
import me.onixdev.ircchat.command.api.Command
import me.onixdev.ircchat.command.api.CommandSender
import me.onixdev.ircchat.service.permission.Permission
import me.onixdev.ircchat.service.permission.Role

class RoleCommand : Command(
    "role",
    "Change a user's role (user, moderator, admin)",
    "/role <user> <role>",
    aliases = listOf("setrole"),
    permission = Permission.ROLE
) {
    override fun execute(sender: CommandSender, args: List<String>) {
        if (!requireArgs(sender, args, min = 2, max = 2)) return

        val target = args[0]
        val role = Role.parse(args[1])
        if (role == null) {
            sender.sendMessage(
                "Unknown role '${args[1]}'. Available: " +
                    Role.entries.joinToString(", ") { it.display }
            )
            return
        }

        val dataBase = Server.INSTANCE.getDataBase()
        if (!dataBase.findByUserName(target).beforeJoined) {
            sender.sendMessage("Unknown user '$target'")
            return
        }

        dataBase.updateRole(target, role.display)
        Server.INSTANCE.connectionDataManager?.findAuthenticatedByUsername(target)?.role = role.display
        sender.sendMessage("Role of '$target' updated to '${role.display}'")
    }
}
