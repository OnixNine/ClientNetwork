package me.onixdev.ircchat.impl.s2.codecs;

import me.onixdev.ircchat.base.entity.UserEntry;
import me.onixdev.ircchat.base.entity.UserEntryCodec;
import me.onixdev.ircchat.impl.s2.impl.ChatBroadcastPacket;
import ru.kseonyt.net.packet.PacketBuffer;
import ru.kseonyt.net.packet.PacketCodec;

public class ChatBroadCastPacketCodec implements PacketCodec<ChatBroadcastPacket> {
    private final UserEntryCodec userCodec = new UserEntryCodec();

    @Override
    public void encode(ChatBroadcastPacket pkt, PacketBuffer buf) {
        userCodec.encode(pkt.sender(), buf);
        buf.writeString(pkt.message());
    }

    @Override
    public ChatBroadcastPacket decode(PacketBuffer buf) {
        UserEntry sender = userCodec.decode(buf);
        String message = buf.readString();
        return new ChatBroadcastPacket(sender, message);
    }
}
