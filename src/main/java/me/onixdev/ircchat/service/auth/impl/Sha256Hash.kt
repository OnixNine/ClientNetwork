package me.onixdev.ircchat.service.auth.impl

import dev.whyoleg.cryptography.CryptographyProvider
import dev.whyoleg.cryptography.algorithms.SHA3_512
import me.onixdev.ircchat.service.auth.Hash
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom

class Sha256Hash : Hash {
    override fun hashPassword(password: String): String {
        val provider = CryptographyProvider.Default.get(SHA3_512)
        val hash=   provider.hasher().hashBlocking(password.toByteArray())
        val strings = hash.contentToString().replace("[","").replace("]","")
        return strings
    }

    override fun verifyPassword(password: String, hashedPassword: String): Boolean {
        val provider = CryptographyProvider.Default.get(SHA3_512)
        val hash=   provider.hasher().hashBlocking(password.toByteArray())
        val hash2 = provider.hasher().hashBlocking(hashedPassword.toByteArray())
        return hash.contentEquals(hash2)
    }

}