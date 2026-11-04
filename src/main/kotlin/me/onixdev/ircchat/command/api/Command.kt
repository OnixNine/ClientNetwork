package me.onixdev.ircchat.command.api

abstract class Command(
    val name: String,
    val description: String = "",
    usageOrNull: String? = null,
    val aliases: List<String> = emptyList()
) {
    /** How the command is invoked in help output, e.g. "/kick <user> [reason]". */
    val usage: String = usageOrNull ?: "/$name"

    abstract fun execute(sender: CommandSender, args: List<String>)

    /**
     * Validates the argument count and prints [usage] when it is wrong.
     * Returns true when execution may continue.
     */
    protected fun requireArgs(
        sender: CommandSender,
        args: List<String>,
        min: Int,
        max: Int = Int.MAX_VALUE
    ): Boolean {
        if (args.size < min || args.size > max) {
            sender.sendMessage("Usage: $usage")
            return false
        }
        return true
    }

    override fun toString(): String = name
}
