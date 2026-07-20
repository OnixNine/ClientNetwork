package me.onixdev.ircchat.handler;

import dev.onix.EventManager;
import io.github.oshai.kotlinlogging.KotlinLogging;
import me.onixdev.ircchat.entity.IrcEntity;
import me.onixdev.ircchat.impl.c2.impl.AuthRequestPacket
import me.onixdev.ircchat.impl.c2.impl.ClientChatMessagePacket
import me.onixdev.ircchat.impl.s2.impl.AuthResultPacket
import me.onixdev.ircchat.service.database.DataBaseService;
import me.onixdev.ircchat.util.config.BaseConfig;
import me.onixdev.ircchat.util.events.ClientMessageSendEvent;
import ru.kseonyt.net.annotation.NetworkListener;
import ru.kseonyt.net.annotation.PacketHandler;
import ru.kseonyt.net.context.AttributeKey;
import ru.kseonyt.net.context.NetworkContext;
import java.util.concurrent.ConcurrentHashMap;

@NetworkListener
class NetworkServerHandler(
        private val config: BaseConfig,
        private val dataBaseService: DataBaseService
) {
    private val logger = KotlinLogging.logger("ServerHandler")
    private val contexts = ConcurrentHashMap.newKeySet<NetworkContext>()

    companion object {
        val USER_KEY = AttributeKey.valueOf<IrcEntity>("user")
        val AUTH_KEY = AttributeKey.valueOf<Boolean>("authenticated")
        val TIMEOUT_KEY = AttributeKey.valueOf<Long>("connectTime")
    }

    @PacketHandler
    fun onAuthRequest(pkt: AuthRequestPacket, ctx: NetworkContext) {
        println("AuthRequest")
        val entity = IrcEntity(ctx).apply {
            userName = pkt.username
            uuid = ctx.remoteAddress.toString()
        }

        val user = dataBaseService.findByUserName(pkt.username)
        if (!user.beforeJoined) {
            println("AAAA")
            // Регистрация
            dataBaseService.create(pkt.username, config.hash.hashPassword(pkt.password))
            entity.role = "user"
            ctx.attr(AUTH_KEY).set(true)
            ctx.attr(USER_KEY).set(entity)
            ctx.send(AuthResultPacket("",101, "Registered", entity.role, pkt.username))
            logger.info("User ${pkt.username} registered")
        } else if (config.hash.verifyPassword(config.hash.hashPassword(pkt.password), user.passWord)) {
            // Успешный вход
            entity.role = user.role
            ctx.attr(AUTH_KEY).set(true)
            ctx.attr(USER_KEY).set(entity)
            ctx.send(AuthResultPacket("a",100, "Auth Success!", entity.role, pkt.username))
            logger.info("User ${pkt.username} authenticated")
        } else {
            ctx.send(AuthResultPacket("a",201, "Invalid Password!", "user", pkt.username))
            logger.warn("Failed auth attempt for ${pkt.username}")
        }
    }

    @PacketHandler
    fun onChatMessage(pkt: ClientChatMessagePacket, ctx: NetworkContext) {
        val isAuth = ctx.attr(AUTH_KEY).get() ?: false
//        if (!isAuth) {
//            ctx.send(SystemMessagePacket("You are not authenticated!", 401))
//            return
//        }

        val entity = ctx.attr(USER_KEY).get()!!
//        if (entity == null) {
//            ctx.send(SystemMessagePacket("User data not found!", 404))
//            return
//        }

        val event = ClientMessageSendEvent(entity, pkt.message, entity.userName, entity.role)
        EventManager.callEvent(event)
        if (event.isCancelled) return

                // Рассылка всем авторизованным пользователям
                contexts.forEach {
            val isAuthTarget = it.attr(AUTH_KEY).get() ?: false
            if (isAuthTarget) {
                //it.send(ChatBroadcastPacket(entity.userName, pkt.message, entity.role))
            }
        }
    }
//    @PacketHandler
//    fun onDisconnect(pkt: DisconnectPacket, ctx: NetworkContext) {
//        val entity = ctx.attr(USER_KEY).get()
//        if (entity != null) {
//            logger.info("User ${entity.userName} disconnected: ${pkt.reason}")
//        }
//        contexts.remove(ctx)
//        ctx.disconnect(pkt.reason)
//    }

    // Обработчик новых соединений
    fun onConnect(ctx: NetworkContext) {
        contexts.add(ctx)
        ctx.attr(TIMEOUT_KEY).set(System.currentTimeMillis())
        logger.info("New connection from ${ctx.remoteAddress}")
        ctx.send(AuthResultPacket("",101, "Registered", "", "ssse"))
    }

    // Обработчик закрытых соединений
    fun onDisconnect(ctx: NetworkContext) {
        contexts.remove(ctx)
        val entity = ctx.attr(USER_KEY).get()
        if (entity != null) {
            logger.info("User ${entity.userName} disconnected")
        }
    }
}