package me.onixdev.ircchat.impl.s2.impl;

import me.onixdev.ircchat.impl.s2.codecs.SystemMessagePacketCodec;
import ru.kseonyt.net.annotation.Codec;
import ru.kseonyt.net.annotation.PacketId;
import ru.kseonyt.net.packet.Packet;

/**
 * Generic server -> client notification (timeouts, kicks, admin notices, etc.).
 *
 * @param level   severity: "INFO", "WARN" or "ERROR"
 * @param message human readable text
 */
@PacketId(104)
@Codec(SystemMessagePacketCodec.class)
public record SystemMessagePacket(String level, String message) implements Packet {
}
