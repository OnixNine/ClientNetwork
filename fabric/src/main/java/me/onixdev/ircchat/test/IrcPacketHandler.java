package me.onixdev.ircchat.test;

import me.onixdev.ircchat.impl.s2.impl.AuthResultPacket;
import me.onixdev.ircchat.impl.s2.impl.ChatBroadcastPacket;
import me.onixdev.ircchat.impl.s2.impl.KeepAlivePacket;
import me.onixdev.ircchat.impl.s2.impl.KeyExchangePacket;
import me.onixdev.ircchat.impl.s2.impl.SystemMessagePacket;
import ru.kseonyt.net.annotation.NetworkListener;
import ru.kseonyt.net.annotation.PacketHandler;
import ru.kseonyt.net.context.NetworkContext;

@NetworkListener
public final class IrcPacketHandler {

    private final IrcConnection connection;

    public IrcPacketHandler(IrcConnection connection) {
        this.connection = connection;
    }

    @PacketHandler
    public void onAuthResult(AuthResultPacket packet, NetworkContext ctx) {
        connection.handleAuthResult(packet);
    }

    @PacketHandler
    public void onKeyExchange(KeyExchangePacket packet, NetworkContext ctx) {
        connection.handleKeyExchange(packet);
    }

    @PacketHandler
    public void onChatBroadcast(ChatBroadcastPacket packet, NetworkContext ctx) {
        connection.handleBroadcast(packet);
    }

    @PacketHandler
    public void onSystemMessage(SystemMessagePacket packet, NetworkContext ctx) {
        connection.handleSystemMessage(packet);
    }

    @PacketHandler
    public void onKeepAlive(KeepAlivePacket packet, NetworkContext ctx) {
        connection.handleKeepAlive(packet);
    }
}
