package me.onixdev.ircchat

import me.onixdev.ircchat.command.api.CommandManager
import me.onixdev.ircchat.console.ConsoleManager
import me.onixdev.ircchat.handler.ClientPacketReceiver
import me.onixdev.ircchat.handler.PacketExecuter
import me.onixdev.ircchat.manager.ConnectionDataManager
import me.onixdev.ircchat.service.auth.HashFactory
import me.onixdev.ircchat.service.database.DataBaseService
import me.onixdev.ircchat.util.config.BaseConfig
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.nio.file.Files
import kotlin.system.exitProcess

enum class Server {
    INSTANCE;

    private var clientPacketReceiver: ClientPacketReceiver? = null
    private var port = 0
    private var config: BaseConfig? = null
    private val packetExecuter = PacketExecuter()
    private val connectionDataManager = ConnectionDataManager()
    private val dataBaseService = DataBaseService()
    val commandManager = CommandManager()
    fun start() {
        loadConfig()
        if (config != null) {
            clientPacketReceiver =
                ClientPacketReceiver(config!!, packetExecuter, connectionDataManager, dataBaseService)
            clientPacketReceiver!!.start()
            println("Server Started port ${config?.port}")
            ConsoleManager.init()
        } else {
            println("Error while starting the server stoping")
            exitProcess(1)
        }
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


        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    private fun saveConfig() {
        val file = File("config.json")
        if (!file.exists()) {
            try {
                file.createNewFile()
                val config = JSONObject()
                config.put("port", "123")
                Files.write(file.toPath(), config.toString().toByteArray())
            } catch (e: IOException) {
                throw RuntimeException(e)
            }
        }
        val config = JSONObject()
        config.put("port", port)
        config.put("timeout", 20000)
        config.put("hash", "Sha256")
        try {
            Files.write(file.toPath(), config.toString().toByteArray())
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    fun getDataBase(): DataBaseService {
        return dataBaseService
    }
}