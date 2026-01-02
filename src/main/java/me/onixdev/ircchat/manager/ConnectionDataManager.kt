package me.onixdev.ircchat.manager

import me.onixdev.ircchat.entity.IrcEntity
import org.java_websocket.WebSocket
import java.util.HashMap
import java.util.HashSet
import java.util.concurrent.ConcurrentHashMap

class ConnectionDataManager {
    private val connections: HashMap<WebSocket, IrcEntity> =  HashMap<WebSocket,IrcEntity>()
    fun addConnection(connection: WebSocket) {
        connections[connection] = IrcEntity(connection)
    }
    fun removeConnection(connection: WebSocket) {
        connections.remove(connection)
    }
    fun getConnection(connection: WebSocket): IrcEntity? {
        return connections[connection]
    }
}