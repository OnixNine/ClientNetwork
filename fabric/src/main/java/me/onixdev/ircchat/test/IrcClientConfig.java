package me.onixdev.ircchat.test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class IrcClientConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public String host = "127.0.0.1";
    public int port = 4847;
    public String sender = "fabric-mod";
    public String username = "FabricTester";
    public String password = "test123";
    public String aesKeyBase64 = "";
    public int compressThreshold = 256;
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
