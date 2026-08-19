package me.onixdev.ircchat.impl.s2.codecs;

import me.onixdev.ircchat.impl.s2.impl.KeyExchangePacket;
import ru.kseonyt.net.packet.PacketBuffer;
import ru.kseonyt.net.packet.PacketCodec;

public class KeyExchangePacketCodec implements PacketCodec<KeyExchangePacket> {
    @Override
    public void encode(KeyExchangePacket pkt, PacketBuffer buf) {
        buf.writeString(pkt.sender());
        buf.writeString(pkt.aesKeyBase64());
    }

    @Override
    public KeyExchangePacket decode(PacketBuffer buf) {
        return new KeyExchangePacket(buf.readString(), buf.readString());
    }
}
