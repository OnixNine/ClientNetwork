package me.onixdev.ircchat

import me.onixdev.ircchat.command.api.CommandManager
import me.onixdev.ircchat.console.ConsoleManager
import me.onixdev.ircchat.handler.AuthHandler
import me.onixdev.ircchat.handler.ChatHandler
import me.onixdev.ircchat.handler.LoggingInterceptor
import me.onixdev.ircchat.impl.c2.codecs.AuthRequestPacketCodec
import me.onixdev.ircchat.impl.c2.codecs.ClientChatMessagePacketCodec
import me.onixdev.ircchat.impl.c2.impl.AuthRequestPacket
import me.onixdev.ircchat.impl.c2.impl.ClientChatMessagePacket
import me.onixdev.ircchat.impl.s2.codecs.AuthResultPacketCodec
import me.onixdev.ircchat.impl.s2.codecs.ChatBroadCastPacketCodec
import me.onixdev.ircchat.impl.s2.codecs.KeyExchangePacketCodec
import me.onixdev.ircchat.impl.s2.impl.AuthResultPacket
import me.onixdev.ircchat.impl.s2.impl.ChatBroadcastPacket
import me.onixdev.ircchat.impl.s2.impl.KeyExchangePacket
import me.onixdev.ircchat.manager.ConnectionDataManager
import me.onixdev.ircchat.service.auth.HashFactory
import me.onixdev.ircchat.service.database.DataBaseService
import me.onixdev.ircchat.service.message.BroadCastMessageService
import me.onixdev.ircchat.service.message.validation.MessageLengthLimit
import me.onixdev.ircchat.service.message.validation.MessageSpamDelay
import me.onixdev.ircchat.service.message.validation.MessageValidationPattern
import me.onixdev.ircchat.util.config.BaseConfig
import org.json.JSONObject
import ru.kseonyt.net.Net
import ru.kseonyt.net.packet.DefaultPacketRegistry
import ru.kseonyt.net.packet.PacketRegistry
import ru.kseonyt.net.server.NetworkServer
import java.io.File
import java.nio.file.Files
import java.util.Base64
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec
import kotlin.system.exitProcess

enum class Server {
    INSTANCE;

    var connectionDataManager: ConnectionDataManager? = null
    private var networkServer: NetworkServer? = null
    private var port = 0
    private var config: BaseConfig? = null
    private val dataBaseService = DataBaseService()
    val commandManager = CommandManager()
    private lateinit var aesKey: SecretKey

    fun getAesKey(): SecretKey = aesKey

    fun start() {
        loadConfig()
        aesKey = loadOrCreateKey()
        if (config != null) {
            val registry = createPacketRegistry()
            connectionDataManager = ConnectionDataManager()

            val authHandler = AuthHandler(config!!.hash, dataBaseService, connectionDataManager!!)
            val chatHandler = ChatHandler(connectionDataManager!!)

            networkServer = Net.server()
                .port(config!!.port)
                .codec(registry)
                .compress(256)
                .encrypt(aesKey)
                .interceptor(LoggingInterceptor())
                .listener(authHandler)
                .listener(chatHandler)
                .start()

            networkServer!!.onConnect { ctx ->
                connectionDataManager!!.addConnection(ctx)
                println("New connection from ${ctx.remoteAddress}")
            }
            networkServer!!.onDisconnect { ctx ->
                connectionDataManager!!.removeConnection(ctx)
                println("Connection closed from ${ctx.remoteAddress}")
            }

            println("Server started on port ${config?.port}")
            initListeners()
            ConsoleManager.init()
        } else {
            println("Error starting server")
            exitProcess(1)
        }
    }

    fun getNetworkServer(): NetworkServer? = networkServer

    private fun createPacketRegistry(): PacketRegistry {
        val registry = DefaultPacketRegistry()
        registry.register(0, AuthRequestPacket::class.java,
            AuthRequestPacketCodec()
        )
        registry.register(2, ClientChatMessagePacket::class.java,
            ClientChatMessagePacketCodec()
        )
        registry.register(101, AuthResultPacket::class.java, AuthResultPacketCodec())
        registry.register(102, ChatBroadcastPacket::class.java, ChatBroadCastPacketCodec())
        registry.register(103, KeyExchangePacket::class.java, KeyExchangePacketCodec())
        return registry
    }

    private fun loadOrCreateKey(): SecretKey {
        val configFile = File("config.json")
        if (!configFile.exists()) return generateAndSaveKey()

        val json = JSONObject(String(Files.readAllBytes(configFile.toPath())))
        val keyBase64 = json.optString("aes_key", "")
        if (keyBase64.isNotEmpty()) {
            val keyBytes = Base64.getDecoder().decode(keyBase64)
            return SecretKeySpec(keyBytes, "AES")
        }
        return generateAndSaveKey()
    }

    private fun generateAndSaveKey(): SecretKey {
        val keyGen = KeyGenerator.getInstance("AES")
        keyGen.init(128)
        val key = keyGen.generateKey()

        val configFile = File("config.json")
        val json = if (configFile.exists()) {
            JSONObject(String(Files.readAllBytes(configFile.toPath())))
        } else {
            JSONObject()
        }
        json.put("aes_key", Base64.getEncoder().encodeToString(key.encoded))
        Files.write(configFile.toPath(), json.toString().toByteArray())

        println("AES-128-GCM key generated and saved to config.json")
        return key
    }

    private fun initListeners() {
        MessageValidationPattern()
        MessageLengthLimit()
        MessageSpamDelay()
        BroadCastMessageService()
    }

    private fun loadConfig() {
        val file = File("config.json")
        if (!file.exists()) {
            saveConfig()
            return
        }
        try {
            val json = String(Files.readAllBytes(file.toPath()))
            val jsonObject = JSONObject(json)
            port = jsonObject.getInt("port")
            val timeout = jsonObject.getInt("timeout")
            config = BaseConfig(port, timeout, HashFactory.create(jsonObject.optString("hash", "Sha256")))
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    private fun saveConfig() {
        val file = File("config.json")
        val config = JSONObject()
        config.put("port", port.takeIf { it != 0 } ?: 4847)
        config.put("timeout", 20000)
        config.put("hash", "Sha256")
        Files.write(file.toPath(), config.toString().toByteArray())
    }

    fun getDataBase(): DataBaseService = dataBaseService

    fun stop() {
        networkServer?.shutdown()
        println("Server stopped")
        exitProcess(0)
    }
}