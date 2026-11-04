// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 OnixNine
package me.onixdev.ircchat.command.api

import me.onixdev.ircchat.command.api.CommandSender
import me.onixdev.ircchat.service.permission.Permission
import me.onixdev.ircchat.service.permission.Role
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class CommandManagerTest {

    private class RecordingSender(
        override val role: Role = Role.USER
    ) : CommandSender {
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
    fun `default commands declare their permissions`() {
        val byPermission = CommandManager().commands.associate { it.name.lowercase() to it.permission }

        assertEquals(Permission.ROLE, byPermission["role"])
        assertEquals(Permission.KICK, byPermission["kick"])
        assertEquals(Permission.BAN, byPermission["ban"])
        assertEquals(Permission.UNBAN, byPermission["unban"])
        assertEquals(Permission.MUTE, byPermission["mute"])
        assertEquals(Permission.UNMUTE, byPermission["unmute"])
        assertEquals(Permission.HISTORY, byPermission["history"])
        assertEquals(Permission.PLIST, byPermission["plist"])
        assertEquals(Permission.BROADCAST, byPermission["broadcast"])
        assertEquals(Permission.STOP, byPermission["stop"])
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

    @Test
    fun `dispatch denies a command without the required permission`() {
        val command = object : Command("ban", "b", "/ban", permission = Permission.BAN) {
            var ran = false
            override fun execute(sender: CommandSender, args: List<String>) {
                ran = true
            }
        }
        val manager = isolated(command)
        val sender = RecordingSender(Role.USER)

        assertFalse(manager.dispatch(sender, "ban bob"))
        assertFalse(command.ran)
        assertTrue(sender.messages.any { it.contains("permission") })
    }

    @Test
    fun `dispatch allows a command with the required permission`() {
        val command = object : Command("ban", "b", "/ban", permission = Permission.BAN) {
            var ran = false
            override fun execute(sender: CommandSender, args: List<String>) {
                ran = true
            }
        }
        val manager = isolated(command)

        assertTrue(manager.dispatch(RecordingSender(Role.ADMIN), "ban bob"))
        assertTrue(command.ran)
    }

    @Test
    fun `moderator may kick but not ban`() {
        val kick = FakeCommandWithPermission("kick", Permission.KICK)
        val ban = FakeCommandWithPermission("ban", Permission.BAN)
        val manager = isolated(kick, ban)
        val sender = RecordingSender(Role.MODERATOR)

        assertTrue(manager.dispatch(sender, "kick bob"))
        assertFalse(manager.dispatch(sender, "ban bob"))
        assertTrue(kick.calls > 0)
        assertEquals(0, ban.calls)
    }

    private class FakeCommandWithPermission(
        name: String,
        permission: Permission
    ) : Command(name, "fake", "/$name", permission = permission) {
        var calls = 0
        override fun execute(sender: CommandSender, args: List<String>) {
            calls++
        }
    }
}
