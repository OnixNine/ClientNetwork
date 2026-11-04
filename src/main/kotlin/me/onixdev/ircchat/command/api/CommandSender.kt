package me.onixdev.ircchat.command.api

/**
 * Whoever issued a command. Right now only the console can, but keeping this
 * abstraction means network senders (IRC clients, RCON, ...) can be added later
 * without touching a single command.
 */
interface CommandSender {
    val name: String
    fun sendMessage(message: String)
}

/** The server operator sitting at the physical console. */
object ConsoleSender : CommandSender {
    override val name: String = "CONSOLE"
    override fun sendMessage(message: String) = println(message)
}
