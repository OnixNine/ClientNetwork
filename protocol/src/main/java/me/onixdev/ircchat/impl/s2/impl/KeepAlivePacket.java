// SPDX-License-Identifier: MIT
// Copyright (C) 2026 OnixNine
package me.onixdev.ircchat.impl.s2.impl;

import me.onixdev.ircchat.impl.s2.codecs.KeepAlivePacketCodec;
import ru.kseonyt.net.annotation.Codec;
import ru.kseonyt.net.annotation.PacketId;
import ru.kseonyt.net.packet.Packet;

@PacketId(105)
@Codec(KeepAlivePacketCodec.class)
public record KeepAlivePacket(long id) implements Packet {
}
