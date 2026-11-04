package me.onixdev.ircchat.test;

import me.onixdev.ircchat.impl.s2.impl.AuthResultPacket;
import me.onixdev.ircchat.impl.s2.impl.ChatBroadcastPacket;
import me.onixdev.ircchat.impl.s2.impl.KeyExchangePacket;
import ru.kseonyt.net.annotation.NetworkListener;
import ru.kseonyt.net.annotation.PacketHandler;
import ru.kseonyt.net.context.NetworkContext;

/**
 * Packet listeners for the packets the server can push to a client.
 */
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
}
