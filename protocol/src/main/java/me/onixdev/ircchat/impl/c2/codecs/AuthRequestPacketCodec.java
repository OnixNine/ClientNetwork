package me.onixdev.ircchat.impl.c2.codecs;

import me.onixdev.ircchat.impl.c2.impl.AuthRequestPacket;
import ru.kseonyt.net.packet.PacketBuffer;
import ru.kseonyt.net.packet.PacketCodec;

public class AuthRequestPacketCodec implements PacketCodec<AuthRequestPacket> {
    @Override
    public void encode(AuthRequestPacket pkt, PacketBuffer buf) {
        buf.writeString(pkt.sender());
        buf.writeString(pkt.username());
        buf.writeString(pkt.password());
    }

    @Override
    public AuthRequestPacket decode(PacketBuffer buf) {
        return new AuthRequestPacket(
                buf.readString(),
                buf.readString(),
                buf.readString()
        );
    }
}