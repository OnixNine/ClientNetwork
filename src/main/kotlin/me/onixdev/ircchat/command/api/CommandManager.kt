package me.onixdev.ircchat.command.api

import me.onixdev.ircchat.command.impl.BanCommand
import me.onixdev.ircchat.command.impl.BroadCastCommand
import me.onixdev.ircchat.command.impl.HistoryCommand
import me.onixdev.ircchat.command.impl.KickCommand
import me.onixdev.ircchat.command.impl.MuteCommand
import me.onixdev.ircchat.command.impl.PunishmentListCommand
import me.onixdev.ircchat.command.impl.RoleCommand
import me.onixdev.ircchat.command.impl.StopCommand
import me.onixdev.ircchat.command.impl.UnbanCommand
import me.onixdev.ircchat.command.impl.UnmuteCommand

class CommandManager {

    private val registered = LinkedHashMap<String, Command>()
    private val labels = HashMap<String, Command>()

    var prefix: String = ""

    init {
        register(RoleCommand())
        register(KickCommand())
        register(BanCommand())
        register(UnbanCommand())
        register(MuteCommand())
        register(UnmuteCommand())
        register(HistoryCommand())
        register(PunishmentListCommand())
        register(BroadCastCommand())
        register(StopCommand())
    }

    val commands: Collection<Command>
        get() = registered.values

    fun register(command: Command) {
        val key = command.name.lowercase()
        require(!registered.containsKey(key)) { "Command '${command.name}' is already registered" }
        registered[key] = command
        (listOf(command.name) + command.aliases).forEach { labels[it.lowercase()] = command }
    }

    fun unregister(name: String) {
        val command = registered.remove(name.lowercase()) ?: return
        labels.entries.removeIf { it.value === command }
    }

    fun handleCommand(input: String) = dispatch(ConsoleSender, input)

    fun dispatch(sender: CommandSender, input: String): Boolean {
        var line = input.trim()
        if (prefix.isNotEmpty() && line.startsWith(prefix)) {
            line = line.substring(prefix.length).trim()
        }
        if (line.isEmpty()) return false

        val parts = line.split(WHITESPACE)
        val label = parts[0]
        val args = parts.drop(1)

        val command = labels[label.lowercase()]
        if (command == null) {
            sender.sendMessage("Unknown command: $label")
            printHelp(sender)
            return false
        }

        return try {
            command.execute(sender, args)
            true
        } catch (e: Exception) {
            sender.sendMessage("Error executing '${command.name}': ${e.message}")
            false
        }
    }

    fun printHelp(sender: CommandSender) {
        sender.sendMessage("Available commands:")
        registered.values.forEach { sender.sendMessage("  ${it.usage} - ${it.description}") }
    }

    private companion object {
        val WHITESPACE = "\\s+".toRegex()
    }
}
