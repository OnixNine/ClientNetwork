package me.onixdev.ircchat.test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Client-side configuration for the protocol test mod.
 * Stored in {@code .minecraft/config/ircchat-test.json}.
 */
public final class IrcClientConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /** Server host. */
    public String host = "127.0.0.1";
    /** Server port (see config.json of the server). */
    public int port = 4847;
    /** Name used as the "sender" field on outgoing packets. */
    public String sender = "fabric-mod";
    /** Account username used for authentication. */
    public String username = "FabricTester";
    /** Account password used for authentication. */
    public String password = "test123";
    /**
     * AES-128 key (Base64) used to encrypt the channel. Must match the
     * {@code aes_key} value of the server's config.json. Empty = no encryption
     * (only works against a server started without encryption).
     */
    public String aesKeyBase64 = "";
    /** Compression threshold, must match the server (server uses 256). */
    public int compressThreshold = 256;
    /** Automatically connect and authenticate right after the client starts. */
    public boolean autoConnect = false;

    public static Path path() {
        return FabricLoader.getInstance().getConfigDir().resolve("ircchat-test.json");
    }

    public static IrcClientConfig load() {
        Path path = path();
        if (Files.exists(path)) {
            try {
                IrcClientConfig loaded = GSON.fromJson(Files.readString(path), IrcClientConfig.class);
                if (loaded != null) {
                    return loaded;
                }
            } catch (IOException | RuntimeException e) {
                IrcTestMod.chat("§c[IRC] Failed to read config: " + e.getMessage());
            }
        }
        IrcClientConfig created = new IrcClientConfig();
        created.save();
        return created;
    }

    public void save() {
        try {
            Files.createDirectories(path().getParent());
            Files.writeString(path(), GSON.toJson(this));
        } catch (IOException e) {
            IrcTestMod.chat("§c[IRC] Failed to save config: " + e.getMessage());
        }
    }
}
