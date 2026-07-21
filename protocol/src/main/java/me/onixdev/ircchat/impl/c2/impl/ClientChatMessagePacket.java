package me.onixdev.ircchat.impl.c2.impl;

import me.onixdev.ircchat.impl.c2.codecs.ClientChatMessagePacketCodec;
import ru.kseonyt.net.annotation.Codec;
import ru.kseonyt.net.annotation.PacketId;
import ru.kseonyt.net.packet.Packet;


@PacketId(2)
@Codec(ClientChatMessagePacketCodec.class)
public record ClientChatMessagePacket(String sender,String message,String author) implements Packet {
}
