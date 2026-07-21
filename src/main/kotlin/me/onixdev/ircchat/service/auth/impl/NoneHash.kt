package me.onixdev.ircchat.service.auth.impl

import me.onixdev.ircchat.service.auth.Hash

class NoneHash : Hash {
    override fun hashPassword(password: String): String {
        return password
    }

    override fun verifyPassword(password: String, hashedPassword: String): Boolean {
        return password == hashedPassword
    }
}