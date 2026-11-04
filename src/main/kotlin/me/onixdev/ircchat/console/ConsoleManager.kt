package me.onixdev.ircchat.console

import me.onixdev.ircchat.Server
import java.util.Scanner

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
