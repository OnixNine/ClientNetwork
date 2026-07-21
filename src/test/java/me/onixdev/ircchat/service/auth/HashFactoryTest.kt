package me.onixdev.ircchat.service.auth

import me.onixdev.ircchat.service.auth.impl.NoneHash
import me.onixdev.ircchat.service.auth.impl.Sha256Hash
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class HashFactoryTest {

    @Test
    fun `create returns Sha256Hash for Sha256`() {
        val hash = HashFactory.create("Sha256")
        assertInstanceOf(Sha256Hash::class.java, hash)
    }

    @Test
    fun `create returns NoneHash for none`() {
        val hash = HashFactory.create("none")
        assertInstanceOf(NoneHash::class.java, hash)
    }

    @Test
    fun `create returns NoneHash for unknown type`() {
        val hash = HashFactory.create("md5")
        assertInstanceOf(NoneHash::class.java, hash)
    }

    @Test
    fun `create returns NoneHash for empty string`() {
        val hash = HashFactory.create("")
        assertInstanceOf(NoneHash::class.java, hash)
    }

    @Test
    fun `create is case sensitive`() {
        val hash = HashFactory.create("sha256")
        assertInstanceOf(NoneHash::class.java, hash)
    }

    @Test
    fun `created Sha256Hash actually works`() {
        val hash = HashFactory.create("Sha256")
        val hashed = hash.hashPassword("test")
        assertTrue(hash.verifyPassword("test", hashed))
        assertFalse(hash.verifyPassword("wrong", hashed))
    }

    @Test
    fun `created NoneHash actually works`() {
        val hash = HashFactory.create("none")
        assertEquals("test", hash.hashPassword("test"))
        assertTrue(hash.verifyPassword("test", "test"))
        assertFalse(hash.verifyPassword("test", "wrong"))
    }
}
