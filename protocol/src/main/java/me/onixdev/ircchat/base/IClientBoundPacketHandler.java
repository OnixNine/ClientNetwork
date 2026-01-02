package me.onixdev.ircchat.base;

import me.onixdev.ircchat.impl.s2.AuthFinishS2Packet;
import me.onixdev.ircchat.impl.s2.ChatMessageS2packet;
import me.onixdev.ircchat.impl.s2.SystemMessageS2Packet;


public interface IClientBoundPacketHandler {
    void handle(ChatMessageS2packet packet);
    void handle(AuthFinishS2Packet packet);
    void handle(SystemMessageS2Packet packet);
}
