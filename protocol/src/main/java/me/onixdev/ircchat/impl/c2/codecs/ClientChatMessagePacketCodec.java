package me.onixdev.ircchat.impl.c2.codecs;

import me.onixdev.ircchat.impl.c2.impl.AuthRequestPacket;
import me.onixdev.ircchat.impl.c2.impl.ClientChatMessagePacket;
import ru.kseonyt.net.packet.PacketBuffer;
import ru.kseonyt.net.packet.PacketCodec;

public class ClientChatMessagePacketCodec implements PacketCodec<ClientChatMessagePacket> {
    @Override
    public void encode(ClientChatMessagePacket pkt, PacketBuffer buf) {
        buf.writeString(pkt.sender());
        buf.writeString(pkt.author());
        buf.writeString(pkt.message());
    }

    @Override
    public ClientChatMessagePacket decode(PacketBuffer buf) {
        return new ClientChatMessagePacket(
                buf.readString(),
                buf.readString(),
                buf.readString()
        );
    }
}