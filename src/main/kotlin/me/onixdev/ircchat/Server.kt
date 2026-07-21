package me.onixdev.ircchat

import me.onixdev.ircchat.base.Encrypting
import me.onixdev.ircchat.command.api.CommandManager
import me.onixdev.ircchat.console.ConsoleManager
import me.onixdev.ircchat.handler.LoggingInterceptor
import me.onixdev.ircchat.handler.NetworkServerHandler
import me.onixdev.ircchat.impl.c2.codecs.AuthRequestPacketCodec
import me.onixdev.ircchat.impl.c2.codecs.ClientChatMessagePacketCodec
import me.onixdev.ircchat.impl.c2.impl.AuthRequestPacket
import me.onixdev.ircchat.impl.c2.impl.ClientChatMessagePacket
import me.onixdev.ircchat.impl.s2.codecs.AuthResultPacketCodec
import me.onixdev.ircchat.impl.s2.codecs.ChatBroadCastPacketCodec
import me.onixdev.ircchat.impl.s2.impl.AuthResultPacket
import me.onixdev.ircchat.impl.s2.impl.ChatBroadcastPacket
import me.onixdev.ircchat.manager.ConnectionDataManager
import me.onixdev.ircchat.service.auth.HashFactory
import me.onixdev.ircchat.service.database.DataBaseService
import me.onixdev.ircchat.service.message.BroadCastMessageService
import me.onixdev.ircchat.service.message.validation.MessageValidationPattern
import me.onixdev.ircchat.util.config.BaseConfig
import org.json.JSONObject
import ru.kseonyt.net.Net
import ru.kseonyt.net.packet.DefaultPacketRegistry
import ru.kseonyt.net.packet.PacketRegistry
import ru.kseonyt.net.server.NetworkServer
import ru.kseonyt.net.udp.UdpEndpoint
import java.io.File
import java.nio.file.Files
import javax.crypto.KeyGenerator
import kotlin.system.exitProcess

enum class Server {
    INSTANCE;

    var connectionDataManager: ConnectionDataManager? = null
    private var networkServer: NetworkServer? = null
    private var udpEndpoint: UdpEndpoint? = null
    private var port = 0
    private var config: BaseConfig? = null
    private val dataBaseService = DataBaseService()
    lateinit var  handler:NetworkServerHandler
    val commandManager = CommandManager()

    fun start() {
        loadConfig()
        if (config != null) {
            val registry = createPacketRegistry()
            connectionDataManager = ConnectionDataManager()
            handler = NetworkServerHandler(config!!, dataBaseService,connectionDataManager!!)
            networkServer = Net.server()
                .port(config!!.port)
                .codec(registry)
                .compress(256)
                //.encrypt(generateKey())
               .interceptor(LoggingInterceptor())
                .listener(handler)
                .start()
            networkServer!!.onConnect { ctx -> handler.onConnect(ctx) }
                .onDisconnect { ctx -> handler.onDisconnect(ctx) }

            println("Server started on port ${config?.port}")
            initListeners()
            ConsoleManager.init()
        } else {
            println("Error starting server")
            exitProcess(1)
        }
    }

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
        return registry
    }

    private fun generateKey(): javax.crypto.SecretKey {
        val keyGen = KeyGenerator.getInstance("AES")
        keyGen.init(128)
        return keyGen.generateKey()
    }

    private fun initListeners() {
        MessageValidationPattern()
//        MessageLengthLimit()
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
            val key = jsonObject.optString("key", "41dd854w8s")
            Encrypting.INSTANCE.key = key
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
        config.put("key", "41dd854w8s")
        Files.write(file.toPath(), config.toString().toByteArray())
    }

    fun getDataBase(): DataBaseService = dataBaseService

    fun stop() {
        networkServer?.shutdown()
        udpEndpoint?.shutdown()
        println("Server stopped")
        exitProcess(0)
    }
}