package me.onixdev.ircchat

import me.onixdev.ircchat.handler.ClientPacketReceiver
import me.onixdev.ircchat.handler.PacketExecuter
import me.onixdev.ircchat.manager.ConnectionDataManager
import me.onixdev.ircchat.util.config.BaseConfig
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.nio.file.Files
import kotlin.system.exitProcess

class Server {
    private var clientPacketReceiver: ClientPacketReceiver? = null
    private var port = 0
    private var config: BaseConfig? = null
    private val packetExecuter = PacketExecuter()
    private val connectionDataManager = ConnectionDataManager()
    fun start() {
        loadConfig()
        if (config != null) {
            clientPacketReceiver = ClientPacketReceiver(config!!, packetExecuter, connectionDataManager)
            clientPacketReceiver!!.start()
            println("Server Started port ${config?.port}")
        }
        else {
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
            config = BaseConfig(port, timeout)

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
        try {
            Files.write(file.toPath(), config.toString().toByteArray())
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }
}