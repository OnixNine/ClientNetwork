package me.onixdev.ircchat.impl.s2.codecs;

import me.onixdev.ircchat.impl.s2.impl.AuthResultPacket;
import ru.kseonyt.net.exception.PacketDecodeException;
import ru.kseonyt.net.exception.PacketEncodeException;
import ru.kseonyt.net.packet.PacketBuffer;
import ru.kseonyt.net.packet.PacketCodec;

public class AuthResultPacketCodec implements PacketCodec<AuthResultPacket> {
    @Override
    public void encode(AuthResultPacket authResultPacket, PacketBuffer packetBuffer) throws PacketEncodeException {
        packetBuffer.writeString(authResultPacket.sender());
        packetBuffer.writeVarInt(authResultPacket.code());
        packetBuffer.writeString(authResultPacket.r());
        packetBuffer.writeString(authResultPacket.username());
    }

    @Override
    public AuthResultPacket decode( PacketBuffer packetBuffer) throws PacketDecodeException {
        return new AuthResultPacket(packetBuffer.readString(),packetBuffer.readVarInt(),packetBuffer.readString(),packetBuffer.readString(),packetBuffer.readString());
    }
}
