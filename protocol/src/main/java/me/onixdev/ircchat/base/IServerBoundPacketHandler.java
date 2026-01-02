package me.onixdev.ircchat.base;

import me.onixdev.ircchat.impl.c2.ChatMessageC2Packet;

public interface IServerBoundPacketHandler {
    void handle(ChatMessageC2Packet packet);
}
