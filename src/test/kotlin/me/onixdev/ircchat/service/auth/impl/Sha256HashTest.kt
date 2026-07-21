package me.onixdev.ircchat.service.auth.impl

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class Sha256HashTest {

    private val hash = Sha256Hash()

    @Test
    fun `hashPassword returns non-empty string`() {
        val result = hash.hashPassword("test")
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun `hashPassword is deterministic`() {
        val h1 = hash.hashPassword("mypassword")
        val h2 = hash.hashPassword("mypassword")
        assertEquals(h1, h2)
    }

    @Test
    fun `different passwords produce different hashes`() {
        val h1 = hash.hashPassword("password1")
        val h2 = hash.hashPassword("password2")
        assertNotEquals(h1, h2)
    }

    @Test
    fun `verifyPassword returns true for correct password`() {
        val hashed = hash.hashPassword("correct")
        assertTrue(hash.verifyPassword("correct", hashed))
    }

    @Test
    fun `verifyPassword returns false for wrong password`() {
        val hashed = hash.hashPassword("correct")
        assertFalse(hash.verifyPassword("wrong", hashed))
    }

    @Test
    fun `hashPassword with empty string`() {
        val result = hash.hashPassword("")
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun `hashPassword with unicode`() {
        val result = hash.hashPassword("пароль123")
        assertTrue(result.isNotEmpty())
        assertTrue(hash.verifyPassword("пароль123", result))
    }

    @Test
    fun `hashPassword output is consistent format`() {
        val result = hash.hashPassword("test")
        assertTrue(result.isNotEmpty())
        assertFalse(result.contains("["))
        assertFalse(result.contains("]"))
    }
}
