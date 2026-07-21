package me.onixdev.ircchat.base.entity;

import ru.kseonyt.net.packet.Packet;

public record UserEntry(String username,
                        String role) implements Packet {
}
