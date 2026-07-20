package me.onixdev.ircchat.impl.s2.impl;

import me.onixdev.ircchat.impl.c2.codecs.AuthRequestPacketCodec;
import ru.kseonyt.net.annotation.Codec;
import ru.kseonyt.net.annotation.PacketId;
import ru.kseonyt.net.packet.Packet;

@PacketId(101)
@Codec(AuthRequestPacketCodec.class)
public record AuthResultPacket(String sender,int code,String msg,String r,String username) implements Packet {
}
