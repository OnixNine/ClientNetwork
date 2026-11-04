// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 OnixNine
package me.onixdev.ircchat.command.api

interface CommandSender {
    val name: String
    fun sendMessage(message: String)
}

object ConsoleSender : CommandSender {
    override val name: String = "CONSOLE"
    override fun sendMessage(message: String) = println(message)
}
