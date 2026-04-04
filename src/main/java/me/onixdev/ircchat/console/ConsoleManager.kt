package me.onixdev.ircchat.console

import me.onixdev.ircchat.Server
import java.util.*

object ConsoleManager {
    fun init() {
        for (i in 0..999) {
            var scanner = Scanner(System.`in`)
            val string = scanner.nextLine()
            try {
                Server.INSTANCE.commandManager.handleCommand(string)
            } catch (e: Exception) {
                println("Error while handling: " + string + " " + e.message)
                e.printStackTrace()
                scanner = Scanner(System.`in`)
            }
        }
    }
}