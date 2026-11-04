// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 OnixNine
package me.onixdev.ircchat.manager

import ru.kseonyt.net.context.NetworkContext
import me.onixdev.ircchat.entity.IrcEntity
import java.util.concurrent.ConcurrentHashMap

class ConnectionDataManager {
    private val connections = ConcurrentHashMap<NetworkContext, IrcEntity>()

    fun addConnection(ctx: NetworkContext) {
        connections[ctx] = IrcEntity(ctx)
    }

    fun removeConnection(ctx: NetworkContext) {
        connections.remove(ctx)
    }

    fun getConnection(ctx: NetworkContext): IrcEntity? {
        return connections[ctx]
    }

    fun findAuthenticatedByUsername(username: String): IrcEntity? {
        return connections.values.firstOrNull {
            it.authed && it.userName.equals(username, ignoreCase = true)
        }
    }

    fun isOnline(username: String): Boolean = findAuthenticatedByUsername(username) != null

    fun getAll(): Collection<IrcEntity> {
        return connections.values
    }
}