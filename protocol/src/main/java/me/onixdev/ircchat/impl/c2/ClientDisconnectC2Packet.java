package me.onixdev.ircchat.impl.c2;

import me.onixdev.ircchat.base.BasePacket;
import me.onixdev.ircchat.hwid.HwidUtil;

public class ClientDisconnectC2Packet extends BasePacket {
    public ClientDisconnectC2Packet() {
        super(8, PacketBound.SERVER, HwidUtil.getHwid());
    }

    @Override
    public String export() {
        return createBasePacket().toString();
    }
}
