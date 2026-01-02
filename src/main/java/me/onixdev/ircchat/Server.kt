package me.onixdev.ircchat

import me.onixdev.ircchat.handler.ClientPacketReceiver
import me.onixdev.ircchat.handler.PacketExecuter
import me.onixdev.ircchat.manager.ConnectionDataManager
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.nio.file.Files

class Server() {
    private var clientPacketReceiver: ClientPacketReceiver? = null
    private var port = 0
    private val packetExecuter = PacketExecuter()
    private val connectionDataManager = ConnectionDataManager()
    fun start() {
        loadConfig()
        clientPacketReceiver = ClientPacketReceiver(port, packetExecuter,connectionDataManager)
        clientPacketReceiver!!.start()
        println("Server Started port $port")
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
        config.put("crypt","sha256")
        try {
            Files.write(file.toPath(), config.toString().toByteArray())
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }
}