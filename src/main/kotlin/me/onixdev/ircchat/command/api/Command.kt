// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 OnixNine
package me.onixdev.ircchat.command.api

import me.onixdev.ircchat.service.permission.Permission

abstract class Command(
    val name: String,
    val description: String = "",
    usageOrNull: String? = null,
    val aliases: List<String> = emptyList(),
    val permission: Permission = Permission.CHAT
) {
    val usage: String = usageOrNull ?: "/$name"

    abstract fun execute(sender: CommandSender, args: List<String>)

    protected fun requireArgs(
        sender: CommandSender,
        args: List<String>,
        min: Int,
        max: Int = Int.MAX_VALUE
    ): Boolean {
        if (args.size < min || args.size > max) {
            sender.sendMessage("Usage: $usage")
            return false
        }
        return true
    }

    override fun toString(): String = name
}
