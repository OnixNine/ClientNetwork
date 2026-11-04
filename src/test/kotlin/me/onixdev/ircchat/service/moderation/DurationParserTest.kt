// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 OnixNine
package me.onixdev.ircchat.service.moderation

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test

class DurationParserTest {

    @Test
    fun `unit suffixes are parsed`() {
        assertEquals(ParsedDuration.Temporary(1_000), DurationParser.parse("1s"))
        assertEquals(ParsedDuration.Temporary(60_000), DurationParser.parse("1m"))
        assertEquals(ParsedDuration.Temporary(3_600_000), DurationParser.parse("1h"))
        assertEquals(ParsedDuration.Temporary(86_400_000), DurationParser.parse("1d"))
        assertEquals(ParsedDuration.Temporary(604_800_000), DurationParser.parse("1w"))
        assertEquals(ParsedDuration.Temporary(90_000), DurationParser.parse("90s"))
    }

    @Test
    fun `a bare number means minutes`() {
        assertEquals(ParsedDuration.Temporary(120_000), DurationParser.parse("2"))
    }

    @Test
    fun `permanent keywords`() {
        assertSame(ParsedDuration.Permanent, DurationParser.parse("perm"))
        assertSame(ParsedDuration.Permanent, DurationParser.parse("PERMANENT"))
        assertSame(ParsedDuration.Permanent, DurationParser.parse("forever"))
    }

    @Test
    fun `invalid tokens are reported`() {
        assertSame(ParsedDuration.NotADuration, DurationParser.parse("hello"))
        assertSame(ParsedDuration.NotADuration, DurationParser.parse("1x"))
        assertSame(ParsedDuration.NotADuration, DurationParser.parse(""))
    }

    @Test
    fun `format renders the largest units`() {
        val oneDay = 86_400_000L
        assertEquals("1d 2h 3m", DurationParser.format(oneDay + 2 * 3_600_000L + 3 * 60_000L))
        assertEquals("45s", DurationParser.format(45_000))
        assertEquals("0s", DurationParser.format(0))
    }
}
