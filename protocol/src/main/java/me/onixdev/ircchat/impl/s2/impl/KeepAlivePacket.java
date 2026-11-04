package me.onixdev.ircchat.impl.s2.impl;

import me.onixdev.ircchat.impl.s2.codecs.KeepAlivePacketCodec;
import ru.kseonyt.net.annotation.Codec;
import ru.kseonyt.net.annotation.PacketId;
import ru.kseonyt.net.packet.Packet;

/**
 * Keep-alive ping. The server sends it and the client must answer with the
 * exact same {@code id}; if no answer arrives before the timeout the server
 * drops the connection (hung / stalled clients).
 */
@PacketId(105)
@Codec(KeepAlivePacketCodec.class)
public record KeepAlivePacket(long id) implements Packet {
}
