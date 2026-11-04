// SPDX-License-Identifier: MIT
// Copyright (C) 2026 OnixNine
package me.onixdev.ircchat.impl.s2.codecs;

import me.onixdev.ircchat.impl.s2.impl.SystemMessagePacket;
import ru.kseonyt.net.packet.PacketBuffer;
import ru.kseonyt.net.packet.PacketCodec;

public class SystemMessagePacketCodec implements PacketCodec<SystemMessagePacket> {
    @Override
    public void encode(SystemMessagePacket pkt, PacketBuffer buf) {
        buf.writeString(pkt.level());
        buf.writeString(pkt.message());
    }

    @Override
    public SystemMessagePacket decode(PacketBuffer buf) {
        return new SystemMessagePacket(buf.readString(), buf.readString());
    }
}
