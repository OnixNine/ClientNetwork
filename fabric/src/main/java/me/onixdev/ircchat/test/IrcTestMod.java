package me.onixdev.ircchat.test;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

/**
 * Client-only entrypoint. Exposes the {@code /irctest} command and (optionally)
 * auto-connects to the IRC protocol server on startup.
 *
 * Commands:
 * <pre>
 *   /irctest connect            - connect + authenticate using config
 *   /irctest auth               - (re)send the auth request
 *   /irctest disconnect         - close the connection
 *   /irctest status             - print the current state
 *   /irctest send &lt;message&gt;    - send a chat message to the server
 *   /irctest reload             - reload config from disk
 * </pre>
 */
public final class IrcTestMod implements ClientModInitializer {

    private static IrcClientConfig config;
    private static IrcConnection connection;

    @Override
    public void onInitializeClient() {
        config = IrcClientConfig.load();
        connection = new IrcConnection(config);

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("irctest")
                    .then(ClientCommandManager.literal("connect")
                            .executes(ctx -> {
                                connection.connect();
                                return 1;
                            }))
                    .then(ClientCommandManager.literal("auth")
                            .executes(ctx -> {
                                connection.authenticate();
                                return 1;
                            }))
                    .then(ClientCommandManager.literal("disconnect")
                            .executes(ctx -> {
                                connection.disconnect();
                                return 1;
                            }))
                    .then(ClientCommandManager.literal("status")
                            .executes(ctx -> {
                                chat("§7[IRC] " + connection.status());
                                return 1;
                            }))
                    .then(ClientCommandManager.literal("send")
                            .then(ClientCommandManager.argument("message", StringArgumentType.greedyString())
                                    .executes(ctx -> {
                                        connection.sendChat(StringArgumentType.getString(ctx, "message"));
                                        return 1;
                                    })))
                    .then(ClientCommandManager.literal("reload")
                            .executes(ctx -> {
                                config = IrcClientConfig.load();
                                connection.setConfig(config);
                                chat("§7[IRC] Config reloaded.");
                                return 1;
                            })));
        });

        if (config.autoConnect) {
            connection.connect();
        }
    }

    /** Print a message into the client chat HUD (thread-safe). */
    public static void chat(String message) {
        MinecraftClient client = MinecraftClient.getInstance();
        client.execute(() -> {
            if (client.inGameHud != null) {
                client.inGameHud.getChatHud().addMessage(Text.literal(message));
            }
        });
    }
}
