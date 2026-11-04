// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 OnixNine
package me.onixdev.ircchat.command.impl

import me.onixdev.ircchat.Server
import me.onixdev.ircchat.command.api.Command
import me.onixdev.ircchat.command.api.CommandSender
import me.onixdev.ircchat.service.permission.Permission

class StopCommand : Command(
    "stop",
    "Shut the server down",
    "/stop",
    permission = Permission.STOP
) {
    override fun execute(sender: CommandSender, args: List<String>) {
        sender.sendMessage("Stopping server...")
        Server.INSTANCE.stop()
    }
}
