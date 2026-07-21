package me.onixdev.ircchat.command.api

import me.onixdev.ircchat.command.impl.BroadCastCommand
import me.onixdev.ircchat.command.impl.StopCommand
import me.onixdev.ircchat.command.impl.UpdateRoleCommand


class CommandManager {
    val commands: MutableList<Command> = ArrayList<Command>()
    var prefix: String? = "."

    init {
        register(UpdateRoleCommand())
        register(StopCommand())
        register(BroadCastCommand())
    }

    private fun register(command: Command?) {
        commands.add(command!!)
    }

    fun handleCommand(input: String) {
        val split: Array<String?> = input.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        if (split.isEmpty()) return

        val commandName = split[0]
        val args = arrayOfNulls<String>(split.size - 1)
        System.arraycopy(split, 1, args, 0, args.size)

        for (cmd in commands) {
            if (cmd.name.equals(commandName)) {
                try {
                    cmd.execute(args)
                } catch (e: Exception) {
                    throw RuntimeException(e)
                }
                return
            }
        }
    }
}
