package me.onixdev.ircchat.impl.s2.impl;

import me.onixdev.ircchat.base.entity.UserEntry;
import me.onixdev.ircchat.impl.s2.codecs.ChatBroadCastPacketCodec;
import ru.kseonyt.net.annotation.Codec;
import ru.kseonyt.net.annotation.PacketId;
import ru.kseonyt.net.packet.Packet;

@PacketId(102)
@Codec(ChatBroadCastPacketCodec.class)
public record ChatBroadcastPacket(
        UserEntry sender,
        String message
) implements Packet {
}