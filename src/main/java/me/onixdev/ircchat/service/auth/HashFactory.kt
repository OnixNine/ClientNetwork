package me.onixdev.ircchat.service.auth

import me.onixdev.ircchat.service.auth.impl.NoneHash
import me.onixdev.ircchat.service.auth.impl.Sha256Hash


object HashFactory {
    fun create(hashType: String): Hash {
        return when (hashType) {
            "none" -> NoneHash()
            "Sha256" -> Sha256Hash()
            else -> NoneHash()
        }
    }
}