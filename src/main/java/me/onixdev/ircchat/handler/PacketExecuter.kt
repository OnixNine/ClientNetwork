package me.onixdev.ircchat.handler

import me.onixdev.ircchat.base.IServerBoundPacketHandler
import me.onixdev.ircchat.impl.c2.ChatMessageC2Packet

class PacketExecuter : IServerBoundPacketHandler {
    override fun handle(packet: ChatMessageC2Packet?) {
        println("msg: " + packet!!.message)
    }
}