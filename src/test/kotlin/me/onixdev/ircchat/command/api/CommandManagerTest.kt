package me.onixdev.ircchat.command.api

import io.mockk.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CommandManagerTest {

    private lateinit var manager: CommandManager

    @BeforeEach
    fun setUp() {
        manager = CommandManager()
        manager.commands.clear()
    }

    @Test
    fun `commands list has defaults before clear`() {
        val m = CommandManager()
        assertTrue(m.commands.isNotEmpty())
    }

    @Test
    fun `handleCommand dispatches to matching command`() {
        val testCommand = mockk<Command>(relaxed = true) {
            every { name } returns "test"
        }
        manager.commands.add(testCommand)

        manager.handleCommand("test arg1 arg2")

        verify { testCommand.execute(match { it.size == 2 && it[0] == "arg1" && it[1] == "arg2" }) }
    }

    @Test
    fun `handleCommand does nothing for unknown command`() {
        val testCommand = mockk<Command>(relaxed = true) {
            every { name } returns "known"
        }
        manager.commands.add(testCommand)

        manager.handleCommand("unknown args")

        verify(exactly = 0) { testCommand.execute(any()) }
    }

    @Test
    fun `handleCommand with no args`() {
        val testCommand = mockk<Command>(relaxed = true) {
            every { name } returns "cmd"
        }
        manager.commands.add(testCommand)

        manager.handleCommand("cmd")

        verify { testCommand.execute(match { it.isEmpty() }) }
    }

    @Test
    fun `handleCommand parses args correctly`() {
        val testCommand = mockk<Command>(relaxed = true) {
            every { name } returns "mycmd"
        }
        manager.commands.add(testCommand)

        manager.handleCommand("mycmd set admin user123")

        verify {
            testCommand.execute(match {
                it.size == 3 && it[0] == "set" && it[1] == "admin" && it[2] == "user123"
            })
        }
    }

    @Test
    fun `prefix can be changed`() {
        assertEquals(".", manager.prefix)
        manager.prefix = "!"
        assertEquals("!", manager.prefix)
    }

    @Test
    fun `handleCommand is case sensitive`() {
        val testCommand = mockk<Command>(relaxed = true) {
            every { name } returns "mycmd"
        }
        manager.commands.add(testCommand)

        manager.handleCommand("MYCMD")

        verify(exactly = 0) { testCommand.execute(any()) }
    }
}
