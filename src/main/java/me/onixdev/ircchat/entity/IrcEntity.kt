package me.onixdev.ircchat.entity

import me.onixdev.ircchat.base.BasePacket
import me.onixdev.ircchat.security.Encrypting
import me.onixdev.ircchat.service.UserAuthService
import org.java_websocket.WebSocket
import org.json.JSONObject
import java.io.File
import java.nio.file.Files

class IrcEntity(val connection: WebSocket) {
    var role: String = "user"
    var userName: String = ""
    var passHash: String = ""
    var uuid: String = ""
    var file: File? = null
    var authed: Boolean = false;
    fun sendPacket(packet: BasePacket) {
        connection.send(Encrypting.encrypt(packet.export()))
    }

    fun init() {
        val temp = File("user")
        if (!temp.exists()) {
            temp.mkdirs()
            print("p:" + temp.absolutePath)
        }
        file = File("user/$userName.json")
        if (!file!!.exists()) {
            file!!.createNewFile()
            val json = JSONObject()
            json.put("name", userName)
            json.put("passHash", UserAuthService.getHash(passHash))
            json.put("uuid", uuid)
            json.put("role", role)
            Files.write(file!!.toPath(), json.toString().toByteArray())
        } else {
            val json = JSONObject(String(Files.readAllBytes(file!!.toPath())))
            passHash = json.getString("passHash")
            uuid = json.getString("uuid")
            userName = json.getString("name")
            role = json.getString("role")
        }
    }

    fun hasBeforeJoin(): Boolean {
        return File("user/$userName.json").exists()
    }

}