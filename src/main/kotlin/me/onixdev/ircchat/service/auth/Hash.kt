// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 OnixNine
package me.onixdev.ircchat.service.auth

interface Hash {
    fun hashPassword(password: String): String

    fun verifyPassword(password: String, hashedPassword: String): Boolean
}