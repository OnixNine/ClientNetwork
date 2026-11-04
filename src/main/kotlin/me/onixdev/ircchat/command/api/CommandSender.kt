package me.onixdev.ircchat.command.api

interface CommandSender {
    val name: String
    fun sendMessage(message: String)
}

object ConsoleSender : CommandSender {
    override val name: String = "CONSOLE"
    override fun sendMessage(message: String) = println(message)
}
