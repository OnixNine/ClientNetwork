package me.onixdev.ircchat.command.impl

import me.onixdev.ircchat.Server
import me.onixdev.ircchat.command.api.Command

class StopCommand : Command("stop","stop Server","stop") {
    override fun execute(args: Array<String?>) {
        Server.INSTANCE.stop()
    }
}