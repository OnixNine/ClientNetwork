// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 OnixNine
package me.onixdev.ircchat.service.moderation

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test

class PunishmentArgumentsTest {

    @Test
    fun `user only falls back to the default reason`() {
        val parsed = PunishmentArguments.parse(listOf("bob"), "default")!!

        assertEquals("bob", parsed.target)
        assertSame(ParsedDuration.Permanent, parsed.duration)
        assertEquals("default", parsed.reason)
    }

    @Test
    fun `a duration token is consumed`() {
        val parsed = PunishmentArguments.parse(listOf("bob", "1d", "spamming"), "default")!!

        assertEquals(ParsedDuration.Temporary(86_400_000), parsed.duration)
        assertEquals("spamming", parsed.reason)
    }

    @Test
    fun `a non-duration second token becomes part of the reason`() {
        val parsed = PunishmentArguments.parse(listOf("bob", "was", "rude"), "default")!!

        assertSame(ParsedDuration.Permanent, parsed.duration)
        assertEquals("was rude", parsed.reason)
    }

    @Test
    fun `perm keyword is parsed as permanent`() {
        val parsed = PunishmentArguments.parse(listOf("bob", "perm", "spam"), "default")!!

        assertSame(ParsedDuration.Permanent, parsed.duration)
        assertEquals("spam", parsed.reason)
    }

    @Test
    fun `empty input is rejected`() {
        assertNull(PunishmentArguments.parse(emptyList(), "default"))
    }
}
