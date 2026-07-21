package me.onixdev.ircchat.handler

import ru.kseonyt.net.context.NetworkContext
import ru.kseonyt.net.packet.Packet
import ru.kseonyt.net.pipeline.InterceptorChain
import ru.kseonyt.net.pipeline.PacketInterceptor

class LoggingInterceptor : PacketInterceptor<Packet?> {
    override fun packetType(): Class<Packet?> {
        return Packet::class.java as Class<Packet?>
    }

    override fun onRead(pkt: Packet, ctx: NetworkContext, chain: InterceptorChain) {
        println("[INTERCEPTOR] Server received packet: " + pkt.javaClass.getSimpleName())
        chain.proceed()
    }

    override fun onWrite(pkt: Packet, ctx: NetworkContext, chain: InterceptorChain) {
        chain.proceed()
    }
}