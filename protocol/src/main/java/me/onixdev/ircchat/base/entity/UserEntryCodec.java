package me.onixdev.ircchat.base.entity;

import ru.kseonyt.net.exception.PacketDecodeException;
import ru.kseonyt.net.exception.PacketEncodeException;
import ru.kseonyt.net.packet.PacketBuffer;
import ru.kseonyt.net.packet.PacketCodec;

public class UserEntryCodec implements PacketCodec<UserEntry> {
    @Override
    public void encode(UserEntry userEntry, PacketBuffer packetBuffer) throws PacketEncodeException {
        packetBuffer.writeString(userEntry.username());
        packetBuffer.writeString(userEntry.role());
    }

    @Override
    public UserEntry decode(PacketBuffer packetBuffer) throws PacketDecodeException {
        return new UserEntry(packetBuffer.readString(), packetBuffer.readString());
    }
}
