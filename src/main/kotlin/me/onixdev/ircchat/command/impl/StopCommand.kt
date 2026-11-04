package me.onixdev.ircchat.command.impl

import me.onixdev.ircchat.Server
import me.onixdev.ircchat.command.api.Command
import me.onixdev.ircchat.command.api.CommandSender

class StopCommand : Command("stop", "Shut the server down", "/stop") {
    override fun execute(sender: CommandSender, args: List<String>) {
        sender.sendMessage("Stopping server...")
        Server.INSTANCE.stop()
    }
}
