package me.onixdev.ircchat.service.packet

import me.onixdev.ircchat.base.BasePacket
import me.onixdev.ircchat.base.Encrypting
import me.onixdev.ircchat.impl.c2.AuthC2Packet
import me.onixdev.ircchat.impl.c2.ChatMessageC2Packet
import me.onixdev.ircchat.impl.c2.ClientDisconnectC2Packet
import me.onixdev.ircchat.impl.c2.KeepAliveC2Packet
import me.onixdev.ircchat.impl.s2.DisconnectS2Packet
import org.json.JSONObject

object PacketFactory {
    fun getPacketById(json: JSONObject): BasePacket {
        return when (json.getInt("id")) {
            0 -> {
                AuthC2Packet(json)
            }

            1 -> ChatMessageC2Packet(json)
            7 -> KeepAliveC2Packet()
            8 -> ClientDisconnectC2Packet()
            else -> null
        }!!
    }

    fun disconnectPacket(reason: String, sender: String): String {
        return Encrypting.INSTANCE.encrypt(DisconnectS2Packet(reason, sender).export())
    }
}