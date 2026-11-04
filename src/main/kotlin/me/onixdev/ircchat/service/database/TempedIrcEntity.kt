// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 OnixNine
package me.onixdev.ircchat.service.database

data class TempedIrcEntity(
    val userName: String,
    val passWord: String,
    val role: String,
    val beforeJoined: Boolean,
    val banned: Boolean
)