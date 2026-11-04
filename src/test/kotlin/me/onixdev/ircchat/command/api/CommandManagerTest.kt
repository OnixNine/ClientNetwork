package me.onixdev.ircchat.command.api

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class CommandManagerTest {

    private class RecordingSender : CommandSender {
        override val name: String = "test"
        val messages = mutableListOf<String>()
        override fun sendMessage(message: String) {
            messages.add(message)
        }
    }

    private class FakeCommand(
        name: String,
        aliases: List<String> = emptyList()
    ) : Command(name, "fake command", "/$name", aliases) {
        val calls = mutableListOf<List<String>>()
        override fun execute(sender: CommandSender, args: List<String>) {
            calls.add(args)
        }
    }

    private fun isolated(vararg commands: Command): CommandManager {
        val manager = CommandManager()
        manager.commands.toList().forEach { manager.unregister(it.name) }
        commands.forEach { manager.register(it) }
        return manager
    }

    @Test
    fun `defaults are registered`() {
        val names = CommandManager().commands.map { it.name.lowercase() }
        assertTrue(
            names.containsAll(
                listOf(
                    "kick", "ban", "unban", "mute", "unmute",
                    "role", "history", "plist", "broadcast", "stop"
                )
            )
        )
    }

    @Test
    fun `dispatch passes parsed args to the command`() {
        val command = FakeCommand("test")
        val manager = isolated(command)

        manager.dispatch(RecordingSender(), "test arg1 arg2")

        assertEquals(listOf("arg1", "arg2"), command.calls.single())
    }

    @Test
    fun `dispatch with no args passes an empty list`() {
        val command = FakeCommand("cmd")
        val manager = isolated(command)

        manager.dispatch(RecordingSender(), "cmd")

        assertTrue(command.calls.single().isEmpty())
    }

    @Test
    fun `dispatch is case insensitive on the label`() {
        val command = FakeCommand("mycmd")
        val manager = isolated(command)

        assertTrue(manager.dispatch(RecordingSender(), "MYCMD"))
        assertEquals(1, command.calls.size)
    }

    @Test
    fun `dispatch resolves aliases`() {
        val command = FakeCommand("broadcast", aliases = listOf("bc"))
        val manager = isolated(command)

        assertTrue(manager.dispatch(RecordingSender(), "bc hello world"))
        assertEquals(listOf("hello", "world"), command.calls.single())
    }

    @Test
    fun `unknown command reports and returns false`() {
        val manager = isolated(FakeCommand("known"))
        val sender = RecordingSender()

        assertFalse(manager.dispatch(sender, "unknown args"))
        assertTrue(sender.messages.any { it.contains("Unknown command") })
    }

    @Test
    fun `command exception is caught and reported`() {
        val command = object : Command("boom") {
            override fun execute(sender: CommandSender, args: List<String>) {
                throw IllegalStateException("kaboom")
            }
        }
        val manager = isolated(command)
        val sender = RecordingSender()

        assertFalse(manager.dispatch(sender, "boom"))
        assertTrue(sender.messages.any { it.contains("kaboom") })
    }

    @Test
    fun `prefix is stripped when present`() {
        val command = FakeCommand("ping")
        val manager = isolated(command)
        manager.prefix = "!"

        assertTrue(manager.dispatch(RecordingSender(), "!ping"))
        assertEquals(1, command.calls.size)
    }

    @Test
    fun `register rejects duplicate names`() {
        val manager = isolated(FakeCommand("dup"))

        assertThrows(IllegalArgumentException::class.java) {
            manager.register(FakeCommand("dup"))
        }
    }

    @Test
    fun `unregister removes the command and its aliases`() {
        val command = FakeCommand("bc", aliases = listOf("broadcast"))
        val manager = isolated(command)
        manager.unregister("bc")
        val sender = RecordingSender()

        assertFalse(manager.dispatch(sender, "broadcast hi"))
        assertTrue(sender.messages.any { it.contains("Unknown command") })
    }
}
