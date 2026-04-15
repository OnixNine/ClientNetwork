package me.onixdev.ircchat.service.auth

interface Hash {
    fun hashPassword(password: String): String

    fun verifyPassword(password: String, hashedPassword: String): Boolean
}