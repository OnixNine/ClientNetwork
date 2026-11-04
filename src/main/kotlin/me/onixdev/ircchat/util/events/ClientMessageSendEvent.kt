// SPDX-License-Identifier: GPL-3.0-or-later
// Copyright (C) 2026 OnixNine
package me.onixdev.ircchat.util.events

import dev.onix.events.Cancellable
import dev.onix.events.Event
import me.onixdev.ircchat.entity.IrcEntity

class ClientMessageSendEvent(val user: IrcEntity,val message: String) : Event , Cancellable {
    private var canceled: Boolean = false
    override fun isCancelled(): Boolean {
        return canceled
    }

    override fun cancel() {
        canceled = true
    }
}