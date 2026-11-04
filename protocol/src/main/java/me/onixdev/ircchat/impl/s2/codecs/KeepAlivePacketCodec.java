package me.onixdev.ircchat.impl.s2.codecs;

import me.onixdev.ircchat.impl.s2.impl.KeepAlivePacket;
import ru.kseonyt.net.packet.PacketBuffer;
import ru.kseonyt.net.packet.PacketCodec;

public class KeepAlivePacketCodec implements PacketCodec<KeepAlivePacket> {
    @Override
    public void encode(KeepAlivePacket pkt, PacketBuffer buf) {
        buf.writeLong(pkt.id());
    }

    @Override
    public KeepAlivePacket decode(PacketBuffer buf) {
        return new KeepAlivePacket(buf.readLong());
    }
}
