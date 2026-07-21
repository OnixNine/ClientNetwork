package me.onixdev.ircchat.service.auth.impl

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class NoneHashTest {

    private val hash = NoneHash()

    @Test
    fun `hashPassword returns input unchanged`() {
        assertEquals("mypassword", hash.hashPassword("mypassword"))
    }

    @Test
    fun `hashPassword with empty string`() {
        assertEquals("", hash.hashPassword(""))
    }

    @Test
    fun `hashPassword with special characters`() {
        assertEquals("p@ss!#\$%^&*()", hash.hashPassword("p@ss!#\$%^&*()"))
    }

    @Test
    fun `verifyPassword returns true for matching strings`() {
        assertTrue(hash.verifyPassword("test", "test"))
    }

    @Test
    fun `verifyPassword returns false for non-matching strings`() {
        assertFalse(hash.verifyPassword("test", "wrong"))
    }

    @Test
    fun `verifyPassword with empty strings`() {
        assertTrue(hash.verifyPassword("", ""))
    }

    @Test
    fun `verifyPassword is symmetric with hashPassword`() {
        val password = "hello123"
        val hashed = hash.hashPassword(password)
        assertTrue(hash.verifyPassword(password, hashed))
    }
}
