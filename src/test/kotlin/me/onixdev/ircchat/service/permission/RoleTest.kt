// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 OnixNine
package me.onixdev.ircchat.service.permission

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class RoleTest {

    @Test
    fun `user can only chat`() {
        assertTrue(Role.USER.has(Permission.CHAT))
        assertFalse(Role.USER.has(Permission.MUTE))
        assertFalse(Role.USER.has(Permission.BAN))
    }

    @Test
    fun `moderator can moderate but not ban`() {
        assertTrue(Role.MODERATOR.has(Permission.MUTE))
        assertTrue(Role.MODERATOR.has(Permission.UNMUTE))
        assertTrue(Role.MODERATOR.has(Permission.KICK))
        assertTrue(Role.MODERATOR.has(Permission.HISTORY))
        assertTrue(Role.MODERATOR.has(Permission.PLIST))
        assertFalse(Role.MODERATOR.has(Permission.BAN))
        assertFalse(Role.MODERATOR.has(Permission.ROLE))
        assertFalse(Role.MODERATOR.has(Permission.STOP))
    }

    @Test
    fun `admin has every permission`() {
        Permission.entries.forEach { assertTrue(Role.ADMIN.has(it), "admin should have $it") }
    }

    @Test
    fun `parse accepts aliases case insensitively`() {
        assertSame(Role.ADMIN, Role.parse("Admin"))
        assertSame(Role.MODERATOR, Role.parse("moder"))
        assertSame(Role.MODERATOR, Role.parse("MOD"))
        assertSame(Role.USER, Role.parse(" player "))
    }

    @Test
    fun `parse returns null for unknown values`() {
        assertNull(Role.parse("superuser"))
        assertNull(Role.parse(null))
    }

    @Test
    fun `fromString falls back to user`() {
        assertSame(Role.USER, Role.fromString("nonsense"))
        assertSame(Role.ADMIN, Role.fromString("admin"))
    }

    @Test
    fun `display names are canonical`() {
        assertEquals("user", Role.USER.display)
        assertEquals("moderator", Role.MODERATOR.display)
        assertEquals("admin", Role.ADMIN.display)
    }
}
