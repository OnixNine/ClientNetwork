
package me.onixdev.ircchat.handler;

import ru.kseonyt.net.packet.Packet;
import ru.kseonyt.net.pipeline.InterceptorChain;
import ru.kseonyt.net.pipeline.PacketInterceptor;
import ru.kseonyt.net.context.NetworkContext;

public class LoggingInterceptor implements PacketInterceptor<Packet> {

    @Override
    public Class<Packet> packetType() {
        return Packet.class; // Перехватываем все пакеты
    }

    @Override
    public void onRead(Packet pkt, NetworkContext ctx, InterceptorChain chain) {
        System.out.println("[INTERCEPTOR] Server received packet: " + pkt.getClass().getSimpleName());
        chain.proceed(); // Обязательно вызываем, чтобы пакет пошёл дальше
    }

    @Override
    public void onWrite(Packet pkt, NetworkContext ctx, InterceptorChain chain) {
        // Для отладки записи пока ничего не выводим, но тоже пропускаем
        chain.proceed();
    }
}