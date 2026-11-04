package me.onixdev.ircchat.console

import me.onixdev.ircchat.Server
import java.util.Scanner

/**
 * Reads console lines and feeds them to the [me.onixdev.ircchat.command.api.CommandManager].
 * Runs on a daemon thread so the network server keeps running and the loop
 * never dies on EOF or a single bad command.
 */
object ConsoleManager {
    fun init() {
        val thread = Thread(::runLoop, "irc-console")
        thread.isDaemon = true
        thread.start()
    }

    private fun runLoop() {
        val scanner = Scanner(System.`in`)
        while (!Thread.currentThread().isInterrupted && scanner.hasNextLine()) {
            val line = scanner.nextLine().trim()
            if (line.isEmpty()) continue
            try {
                Server.INSTANCE.commandManager.handleCommand(line)
            } catch (e: Exception) {
                println("Error while handling '$line': ${e.message}")
            }
        }
    }
}
