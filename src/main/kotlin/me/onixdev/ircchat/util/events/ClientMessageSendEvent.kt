package me.onixdev.ircchat.util.events

import dev.onix.events.Cancellable
import dev.onix.events.Event
import me.onixdev.ircchat.entity.IrcEntity

class ClientMessageSendEvent(val user: IrcEntity,val message: String,val author: String,val role: String) : Event , Cancellable {
    private var canceled: Boolean = false
    override fun isCancelled(): Boolean {
        return canceled
    }

    override fun cancel() {
        canceled = true
    }
}