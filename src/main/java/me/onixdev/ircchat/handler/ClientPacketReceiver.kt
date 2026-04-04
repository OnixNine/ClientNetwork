package me.onixdev.ircchat.handler

import io.github.oshai.kotlinlogging.KotlinLogging
import me.onixdev.ircchat.base.BasePacket
import me.onixdev.ircchat.impl.c2.AuthC2Packet
import me.onixdev.ircchat.impl.c2.ChatMessageC2Packet
import me.onixdev.ircchat.impl.c2.ClientDisconnectC2Packet
import me.onixdev.ircchat.impl.s2.AuthFinishS2Packet
import me.onixdev.ircchat.impl.s2.ChatMessageS2packet
import me.onixdev.ircchat.impl.s2.SystemMessageS2Packet
import me.onixdev.ircchat.manager.ConnectionDataManager
import me.onixdev.ircchat.security.Encrypting
import me.onixdev.ircchat.service.UserAuthService
import me.onixdev.ircchat.service.database.DataBaseService
import me.onixdev.ircchat.service.packet.PacketFactory
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
    private val connectionDataManager: ConnectionDataManager,private val dataBaseService: DataBaseService
) : WebSocketServer(InetSocketAddress(config.port)) {

    val logger = KotlinLogging.logger("PacketLogger")
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
                        logger.info("disconnecting due TimeOut")
                        connection.send(PacketFactory.disconnectPacket("","Вы не успели авторизоваться"))
                        connection.close(1003,"Надо было авторизоваться")
                        connectNoAuth.remove(connection)
                    }
                }
            }
        }
    }

    override fun onOpen(conn: WebSocket, handshake: ClientHandshake?) {
        logger.info { "Connected client ${conn.remoteSocketAddress}" }
        connections.add(conn)
        connectNoAuth.add(conn)
        connectionDataManager.addConnection(conn)
    }

    override fun onClose(conn: WebSocket, code: Int, reason: String?, remote: Boolean) {
        logger.info { "Closed client ${conn.remoteSocketAddress}" }
        connections.remove(conn)
        connectNoAuth.remove(conn)
        connectionDataManager.removeConnection(conn)
    }

    override fun onMessage(conn: WebSocket, message: String?) {
        try {
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
        lateinit var packet: BasePacket

            packet = PacketFactory.getPacketById(json)
            if (packet is ClientDisconnectC2Packet) {
                conn.close()
            }
            logger.info { "received Packet ${packet.javaClass.simpleName} from ${conn.remoteSocketAddress}" }
            if (packet is AuthC2Packet) {
                val entity = connectionDataManager.getConnection(conn)
                if (entity != null) {
                    if (entity.authed) {
                        // Когда другой чел пытается залогиниться под логином этого когда тот уже авторизован?
                        return
                    }
                    packet = AuthC2Packet(json)
                    entity.userName = packet.username
                    entity.uuid = packet.sender
                    val aas = dataBaseService.findByUserName(packet.username)
                    if (!aas.beforeJoined) {
                        entity.userName = packet.username
                        entity.passHash = packet.pass
                        dataBaseService.create(packet.username,packet.pass)
                        entity.sendPacket(
                            AuthFinishS2Packet(
                                packet.sender,
                                101,
                                "Registered",
                                entity.role,
                                entity.userName
                            )
                        )
                    } else {
                        entity.init()
                        val hash = UserAuthService.getHash(packet.pass)
                        logger.info { "has: " + hash + " " + " pass: " + aas.passWord }
                        val valid = UserAuthService.checkAuth(UserAuthService.getHash(aas.passWord), hash)
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
                    conn.send(PacketFactory.disconnectPacket(packet.sender,"InvalidData[1]"))
                    conn.close(1003, "invalidDataType")
                    return
                }
            }
            if (packet is ChatMessageC2Packet) {
                val entity = connectionDataManager.getConnection(conn)
                if (entity != null) {
                    if (!entity.authed) {
                        entity.sendPacket(SystemMessageS2Packet(packet.sender, "you not authenticate!",101))
                        return
                    }
                    if (entity.lastMessage == packet.message && entity.role != "dev") return
                    val message = packet.message
                    if (message.length > 256) {
                        logger.warn { "Message too long: ${message.length} bytes " }
                        conn.send(PacketFactory.disconnectPacket(packet.sender,"Message to long expected ${message.length} > 256"))
                        conn.close(1003, "Message to long expected ${message.length} > 256")
                        return
                    }
                    packetHandler.handle(packet as ChatMessageC2Packet?)
                    for (connect in connections) {
                        val data = connectionDataManager.getConnection(connect)
                        if (data != null) {
                            if (data.authed) {
                                val msg = ChatMessageS2packet(packet.sender, packet.message, entity.userName,entity.role).export()
                                connect.send(Encrypting.encrypt(msg))
                            }
                        }
                    }
                    entity.lastMessage = packet.message
                }
            }
        } catch (e: Exception) {
            conn.send(PacketFactory.disconnectPacket("Server","Error while decoding packet"))
           logger.error{"Error while decoding packet: " + e.message}
            conn.close(1003, "Error while decoding packet")
        }
    }

    override fun onError(conn: WebSocket?, ex: Exception) {
        logger.error{"Error : " + ex.message}
        ex.printStackTrace()
    }

    override fun onStart() {
    }


}