package me.onixdev.ircchat.test;

import me.onixdev.ircchat.base.entity.UserEntry;
import me.onixdev.ircchat.impl.c2.codecs.AuthRequestPacketCodec;
import me.onixdev.ircchat.impl.c2.codecs.ClientChatMessagePacketCodec;
import me.onixdev.ircchat.impl.c2.impl.AuthRequestPacket;
import me.onixdev.ircchat.impl.c2.impl.ClientChatMessagePacket;
import me.onixdev.ircchat.impl.s2.codecs.AuthResultPacketCodec;
import me.onixdev.ircchat.impl.s2.codecs.ChatBroadCastPacketCodec;
import me.onixdev.ircchat.impl.s2.codecs.KeepAlivePacketCodec;
import me.onixdev.ircchat.impl.s2.codecs.KeyExchangePacketCodec;
import me.onixdev.ircchat.impl.s2.codecs.SystemMessagePacketCodec;
import me.onixdev.ircchat.impl.s2.impl.AuthResultPacket;
import me.onixdev.ircchat.impl.s2.impl.ChatBroadcastPacket;
import me.onixdev.ircchat.impl.s2.impl.KeepAlivePacket;
import me.onixdev.ircchat.impl.s2.impl.KeyExchangePacket;
import me.onixdev.ircchat.impl.s2.impl.SystemMessagePacket;
import ru.kseonyt.net.Net;
import ru.kseonyt.net.client.NetworkClient;
import ru.kseonyt.net.client.NetworkClientBuilder;
import ru.kseonyt.net.packet.DefaultPacketRegistry;
import ru.kseonyt.net.packet.PacketRegistry;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;

public final class IrcConnection {

    private IrcClientConfig config;
    private NetworkClient client;
    private volatile boolean authenticated;

    public IrcConnection(IrcClientConfig config) {
        this.config = config;
    }

    public void setConfig(IrcClientConfig config) {
        this.config = config;
    }

    public boolean isConnected() {
        return client != null && client.isConnected();
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    private static PacketRegistry createRegistry() {
        DefaultPacketRegistry registry = new DefaultPacketRegistry();
        registry.register(0, AuthRequestPacket.class, new AuthRequestPacketCodec());
        registry.register(2, ClientChatMessagePacket.class, new ClientChatMessagePacketCodec());
        registry.register(101, AuthResultPacket.class, new AuthResultPacketCodec());
        registry.register(102, ChatBroadcastPacket.class, new ChatBroadCastPacketCodec());
        registry.register(103, KeyExchangePacket.class, new KeyExchangePacketCodec());
        registry.register(104, SystemMessagePacket.class, new SystemMessagePacketCodec());
        registry.register(105, KeepAlivePacket.class, new KeepAlivePacketCodec());
        return registry;
    }

    private SecretKey resolveKey() {
        if (config.aesKeyBase64 == null || config.aesKeyBase64.isBlank()) {
            return null;
        }
        byte[] keyBytes = Base64.getDecoder().decode(config.aesKeyBase64.trim());
        return new SecretKeySpec(keyBytes, "AES");
    }

    public void connect() {
        if (isConnected()) {
            IrcTestMod.chat("§e[IRC] Already connected.");
            return;
        }

        NetworkClientBuilder builder = Net.client()
                .host(config.host)
                .port(config.port)
                .codec(createRegistry())
                .compress(config.compressThreshold)
                .listener(new IrcPacketHandler(this));

        SecretKey key = resolveKey();
        if (key != null) {
            builder.encrypt(key);
        }

        IrcTestMod.chat("§7[IRC] Connecting to " + config.host + ":" + config.port + " ...");
        CompletableFuture<NetworkClient> future = builder.connect(config.host, config.port);
        future.whenComplete((connected, error) -> {
            if (error != null) {
                IrcTestMod.chat("§c[IRC] Connect failed: " + error.getMessage());
                return;
            }
            this.client = connected;
            IrcTestMod.chat("§a[IRC] Connected. Authenticating as " + config.username + " ...");
            authenticate();
        });
    }

    public void authenticate() {
        if (client == null) {
            IrcTestMod.chat("§c[IRC] Not connected.");
            return;
        }
        client.send(new AuthRequestPacket(config.sender, config.username, config.password));
    }

    public void sendChat(String message) {
        if (client == null || !client.isConnected()) {
            IrcTestMod.chat("§c[IRC] Not connected.");
            return;
        }
        if (!authenticated) {
            IrcTestMod.chat("§c[IRC] Not authenticated yet.");
            return;
        }
        client.send(new ClientChatMessagePacket(config.sender, message, config.username));
    }

    public void disconnect() {
        authenticated = false;
        if (client != null) {
            client.disconnect();
            client = null;
            IrcTestMod.chat("§7[IRC] Disconnected.");
        }
    }

    public String status() {
        return "host=" + config.host + ":" + config.port
                + ", connected=" + isConnected()
                + ", authenticated=" + authenticated
                + ", user=" + config.username;
    }

    void handleAuthResult(AuthResultPacket packet) {
        if (packet.code() == 100 || packet.code() == 101) {
            authenticated = true;
            IrcTestMod.chat("§a[IRC] Auth OK (" + packet.code() + "): " + packet.msg()
                    + " role=" + packet.r());
        } else {
            authenticated = false;
            IrcTestMod.chat("§c[IRC] Auth failed (" + packet.code() + "): " + packet.msg());
        }
    }

    void handleKeyExchange(KeyExchangePacket packet) {
        IrcTestMod.chat("§b[IRC] Key exchange from " + packet.sender()
                + " (key length " + (packet.aesKeyBase64() == null ? 0 : packet.aesKeyBase64().length()) + ")");
    }

    void handleBroadcast(ChatBroadcastPacket packet) {
        UserEntry sender = packet.sender();
        String who = sender == null ? "?" : sender.username();
        IrcTestMod.chat("§d[IRC] <" + who + "> §f" + packet.message());
    }

    void handleSystemMessage(SystemMessagePacket packet) {
        String level = packet.level() == null ? "INFO" : packet.level().toUpperCase();
        String color;
        switch (level) {
            case "ERROR":
                color = "§c";
                break;
            case "WARN":
                color = "§e";
                break;
            default:
                color = "§7";
                break;
        }
        IrcTestMod.chat(color + "[IRC] " + packet.message());
    }

    void handleKeepAlive(KeepAlivePacket packet) {
        if (client != null && client.isConnected()) {
            client.send(new KeepAlivePacket(packet.id()));
        }
    }
}
