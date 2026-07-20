package me.onixdev.ircchat.impl.c2.impl;

import me.onixdev.ircchat.impl.c2.codecs.AuthRequestPacketCodec;
import ru.kseonyt.net.annotation.Codec;
import ru.kseonyt.net.annotation.PacketId;
import ru.kseonyt.net.packet.Packet;

@PacketId(1)
@Codec(AuthRequestPacketCodec.class)
public record AuthRequestPacket(String sender, String username, String password) implements Packet {
}