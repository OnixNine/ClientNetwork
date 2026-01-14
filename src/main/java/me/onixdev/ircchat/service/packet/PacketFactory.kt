package me.onixdev.ircchat.service.packet

import me.onixdev.ircchat.base.BasePacket
import me.onixdev.ircchat.impl.c2.AuthC2Packet
import me.onixdev.ircchat.impl.c2.ChatMessageC2Packet
import me.onixdev.ircchat.impl.s2.DisconnectS2Packet
import me.onixdev.ircchat.security.Encrypting
import org.json.JSONObject

object PacketFactory {
    fun getPacketById(json: JSONObject) : BasePacket {
        return when (json.getInt("id")) {
            0 -> {
                AuthC2Packet(json)
            }

            1 -> ChatMessageC2Packet(json)

            else -> null
        }!!
    }
    fun disconnectPacket(reason: String,sender: String) : String {
        return Encrypting.encrypt(DisconnectS2Packet(reason,sender).export())
    }
}