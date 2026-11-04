// SPDX-License-Identifier: MIT
// Copyright (C) 2026 OnixNine
package me.onixdev.ircchat.impl.s2.impl;

import me.onixdev.ircchat.impl.s2.codecs.KeyExchangePacketCodec;
import ru.kseonyt.net.annotation.Codec;
import ru.kseonyt.net.annotation.PacketId;
import ru.kseonyt.net.packet.Packet;

@PacketId(103)
@Codec(KeyExchangePacketCodec.class)
public record KeyExchangePacket(String sender, String aesKeyBase64) implements Packet {
}
