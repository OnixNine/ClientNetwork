package me.onixdev.ircchat.util.string

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll

class NameGenTest {

    @BeforeEach
    fun setUp() {
        NameGen.resetCounter()
    }

    @Test
    fun `getRandomString returns correct length`() {
        assertAll(
            { assertEquals(5, NameGen.getRandomString(5).length) },
            { assertEquals(1, NameGen.getRandomString(1).length) },
            { assertEquals(100, NameGen.getRandomString(100).length) }
        )
    }

    @Test
    fun `getRandomString first char is letter or special`() {
        val firstChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_\$"
        repeat(50) {
            val s = NameGen.getRandomString(10)
            assertTrue(s[0] in firstChars, "First char '${s[0]}' not in allowed set")
        }
    }

    @Test
    fun `getRandomString with range respects bounds`() {
        repeat(50) {
            val s = NameGen.getRandomString(3, 8)
            assertTrue(s.length in 3..8, "Length ${s.length} out of range [3, 8]")
        }
    }

    @Test
    fun `getRandomLowercase contains only lowercase`() {
        val s = NameGen.getRandomLowercase(20)
        assertEquals(20, s.length)
        assertTrue(s.all { it in 'a'..'z' })
    }

    @Test
    fun `getRandomUppercase contains only uppercase`() {
        val s = NameGen.getRandomUppercase(20)
        assertEquals(20, s.length)
        assertTrue(s.all { it in 'A'..'Z' })
    }

    @Test
    fun `getRandomDigits contains only digits`() {
        val s = NameGen.getRandomDigits(20)
        assertEquals(20, s.length)
        assertTrue(s.all { it in '0'..'9' })
    }

    @Test
    fun `getConfusingString uses only confusing chars`() {
        val confusing = "lI1oO0"
        val s = NameGen.getConfusingString(15)
        assertEquals(15, s.length)
        assertTrue(s.all { it in confusing })
    }

    @Test
    fun `getRandomCamelCase has correct length`() {
        val s = NameGen.getRandomCamelCase(5, 15)
        assertTrue(s.length in 5..15)
    }

    @Test
    fun `getRandomCamelCase starts with uppercase`() {
        val s = NameGen.getRandomCamelCase(3, 10)
        assertTrue(s[0] in 'A'..'Z')
    }

    @Test
    fun `getUniqueString increments`() {
        assertEquals("test0", NameGen.getUniqueString("test"))
        assertEquals("test1", NameGen.getUniqueString("test"))
        assertEquals("test2", NameGen.getUniqueString("test"))
    }

    @Test
    fun `uniqueString property increments`() {
        assertEquals("a0", NameGen.uniqueString)
        assertEquals("a1", NameGen.uniqueString)
        assertEquals("a2", NameGen.uniqueString)
    }

    @Test
    fun `resetCounter resets unique string counter`() {
        NameGen.getUniqueString("x")
        NameGen.getUniqueString("x")
        NameGen.resetCounter()
        assertEquals("x0", NameGen.getUniqueString("x"))
    }

    @Test
    fun `getRandomInvisible contains only invisible chars`() {
        val invisible = setOf('\u200B', '\u200C', '\u200D', '\u2060', '\uFEFF')
        val s = NameGen.getRandomInvisible(10)
        assertEquals(10, s.length)
        assertTrue(s.all { it in invisible })
    }

    @Test
    fun `getRandomStringWithPrefix has prefix`() {
        val s = NameGen.getRandomStringWithPrefix("user_", 5)
        assertTrue(s.startsWith("user_"))
        assertEquals(10, s.length)
    }

    @Test
    fun `getRandomStringWithSuffix has suffix`() {
        val s = NameGen.getRandomStringWithSuffix(5, "_bot")
        assertTrue(s.endsWith("_bot"))
        assertEquals(9, s.length)
    }

    @Test
    fun `getRandomUnicode index overload has min length 5`() {
        val s = NameGen.getRandomUnicode(0)
        assertTrue(s.length >= 5)
    }

    @Test
    fun `getRandomUnicode range overload respects bounds`() {
        val s = NameGen.getRandomUnicode(3, 10)
        assertTrue(s.length in 3..10)
    }

    @Test
    fun `getRandomUnicode1 respects length`() {
        val s = NameGen.getRandomUnicode1(15)
        assertEquals(15, s.length)
    }

    @Test
    fun `getRandomUnicode1 zero becomes 1`() {
        val s = NameGen.getRandomUnicode1(0)
        assertEquals(1, s.length)
    }

    @Test
    fun `getRandomIdentifier is lowercase and digits`() {
        val s = NameGen.getRandomIdentifier(5, 15)
        assertTrue(s.length in 5..15)
        assertTrue(s.all { it in 'a'..'z' || it in '0'..'9' })
    }
}
