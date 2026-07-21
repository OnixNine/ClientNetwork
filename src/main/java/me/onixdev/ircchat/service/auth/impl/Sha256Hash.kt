package me.onixdev.ircchat.service.auth.impl

import dev.whyoleg.cryptography.CryptographyProvider
import dev.whyoleg.cryptography.algorithms.SHA3_512
import me.onixdev.ircchat.service.auth.Hash

class Sha256Hash : Hash {
    override fun hashPassword(password: String): String {
        val provider = CryptographyProvider.Default.get(SHA3_512)
        val hash = provider.hasher().hashBlocking(password.toByteArray())
        return hash.contentToString()
            .replace("[", "")
            .replace("]", "")
    }

    override fun verifyPassword(password: String, hashedPassword: String): Boolean {
        val provider = CryptographyProvider.Default.get(SHA3_512)
        val hashOfInput = provider.hasher().hashBlocking(password.toByteArray())
        val hashOfInputString = hashOfInput.contentToString()
            .replace("[", "")
            .replace("]", "")
        return hashOfInputString == hashedPassword
    }
}