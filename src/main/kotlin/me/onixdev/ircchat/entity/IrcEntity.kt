package me.onixdev.ircchat.entity

import ru.kseonyt.net.context.NetworkContext

class IrcEntity(
    val ctx: NetworkContext
) {
    var role: String = "user"
    var userName: String = ""
    var authed: Boolean = false

    fun sendPacket(packet: ru.kseonyt.net.packet.Packet) {
        ctx.send(packet)
    }

    fun sendMessage(message: String, author: String, role: String) {
     //   sendPacket(ChatBroadcastPacket(author, message, role))
    }

    fun init() {
    }
}