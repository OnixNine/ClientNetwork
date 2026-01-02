package me.onixdev.ircchat.handler

import me.onixdev.ircchat.base.BasePacket
import me.onixdev.ircchat.impl.c2.AuthC2Packet
import me.onixdev.ircchat.impl.c2.ChatMessageC2Packet
import me.onixdev.ircchat.impl.s2.AuthFinishS2Packet
import me.onixdev.ircchat.impl.s2.ChatMessageS2packet
import me.onixdev.ircchat.impl.s2.SystemMessageS2Packet
import me.onixdev.ircchat.manager.ConnectionDataManager
import me.onixdev.ircchat.security.Encrypting
import me.onixdev.ircchat.service.UserAuthService
import me.onixdev.ircchat.service.task.GlobalScheduler
import me.onixdev.ircchat.util.config.BaseConfig
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import java.net.InetSocketAddress
import kotlin.math.abs
import kotlin.time.Duration.Companion.seconds

class ClientPacketReceiver(
   private val config: BaseConfig,
    private val packetHandler: PacketExecuter,
    private val connectionDataManager: ConnectionDataManager
) : WebSocketServer(InetSocketAddress(config.port)) {

    private val connections: MutableSet<WebSocket> = HashSet()
    private val connectNoAuth: MutableSet<WebSocket> = HashSet()
    init {
        GlobalScheduler.runTaskTimer("AuthTimeout",0.seconds,1.seconds) {
            checkTimeOut()
        }
    }

    private fun checkTimeOut() {
        if (connectNoAuth.isNotEmpty()) {
            for (connection in connectNoAuth) {
                val data = connectionDataManager.getConnection(connection)
                if (data != null) {
                    val time = abs(System.currentTimeMillis().minus(data.createtime))
                    if (time > config.timeout) {
                        connection.close(1003,"Надо было авторизоваться")
                        connectNoAuth.remove(connection)
                    }
                }
            }
        }
    }

    override fun onOpen(conn: WebSocket, handshake: ClientHandshake?) {
        connections.add(conn)
        connectNoAuth.add(conn)
        connectionDataManager.addConnection(conn)
    }

    override fun onClose(conn: WebSocket, code: Int, reason: String?, remote: Boolean) {
        connections.remove(conn)
        connectNoAuth.remove(conn)
        connectionDataManager.removeConnection(conn)
    }

    override fun onMessage(conn: WebSocket, message: String?) {
        val json: org.json.JSONObject = org.json.JSONObject(Encrypting.decrypt(message.toString()))
        if (!json.has("id") || !json.has("sender")) {
            println("Invalid packet: no id")
            conn.closeConnection(1003, "invalidDATA")
            return
        }
        val bound: String = json.getString("bound")
        if (bound.isNotEmpty() && bound != "CLIENT") {
            conn.close(1003, "invalidBound")
            return
        }

        val id: Int = json.getInt("id")
        lateinit var packet: BasePacket
        try {
            when (id) {
                0 -> {
                    val entity = connectionDataManager.getConnection(conn)
                    if (entity != null) {
                        packet = AuthC2Packet(json)
                        entity.userName = packet.username
                        entity.uuid = packet.sender
                        if (!entity.hasBeforeJoin()) {
                            entity.passHash = packet.pass
                            entity.init()
                        } else {
                            entity.init()
                            val hash = UserAuthService.getHash(packet.pass)
                            val valid = UserAuthService.checkAuth(entity.passHash, hash)
                            if (valid) {
                                entity.sendPacket(
                                    AuthFinishS2Packet(
                                        packet.sender,
                                        100,
                                        "Auth Success!",
                                        entity.role,
                                        entity.userName
                                    )
                                )
                                entity.authed = true
                                connectNoAuth.remove(conn)
                            } else {
                                entity.sendPacket(
                                    AuthFinishS2Packet(
                                        packet.sender,
                                        201,
                                        "Invalid Password!",
                                        entity.role,
                                        entity.userName
                                    )
                                )
                            }
                        }
                    } else {
                        conn.close(1003, "invalidDataType")
                        return
                    }
                }

                1 -> {
                    packet = ChatMessageC2Packet(json)
                    val entity = connectionDataManager.getConnection(conn)
                    if (entity != null) {
                        if (!entity.authed) {
                            entity.sendPacket(SystemMessageS2Packet(packet.sender, "you not authenticate!",101))
                            return
                        }
                        packetHandler.handle(packet as ChatMessageC2Packet?)
                        for (connect in connections) {
                            val msg = ChatMessageS2packet(packet.sender, packet.message, entity.userName,entity.role).export()
                            connect.send(Encrypting.encrypt(msg))
                        }
                    }

                }

                else -> {
                    conn.close(1003, "invalidPacket")
                }
            }
        } catch (e: Exception) {
            println("Error while decoding packet: " + e.message)
            conn.close(1003, "invalidData")
        }
    }

    override fun onError(conn: WebSocket?, ex: Exception) {
        ex.printStackTrace()
    }

    override fun onStart() {
    }


}