// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 OnixNine
package me.onixdev.ircchat.manager

import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import ru.kseonyt.net.context.NetworkContext

class ConnectionDataManagerTest {

    private fun ctx(): NetworkContext = mockk(relaxed = true)

    @Test
    fun `findAuthenticatedByUsername ignores unauthenticated sessions`() {
        val manager = ConnectionDataManager()
        val c = ctx()
        manager.addConnection(c)
        manager.getConnection(c)!!.userName = "alice"

        assertNull(manager.findAuthenticatedByUsername("alice"))
        assertFalse(manager.isOnline("alice"))
    }

    @Test
    fun `findAuthenticatedByUsername returns authenticated session ignoring case`() {
        val manager = ConnectionDataManager()
        val c = ctx()
        manager.addConnection(c)
        manager.getConnection(c)!!.apply {
            userName = "Alice"
            authed = true
        }

        assertNotNull(manager.findAuthenticatedByUsername("alice"))
        assertTrue(manager.isOnline("ALICE"))
    }

    @Test
    fun `removeConnection drops the session`() {
        val manager = ConnectionDataManager()
        val c = ctx()
        manager.addConnection(c)
        manager.getConnection(c)!!.apply {
            userName = "alice"
            authed = true
        }

        manager.removeConnection(c)

        assertNull(manager.getConnection(c))
        assertFalse(manager.isOnline("alice"))
        assertEquals(0, manager.getAll().size)
    }
}
