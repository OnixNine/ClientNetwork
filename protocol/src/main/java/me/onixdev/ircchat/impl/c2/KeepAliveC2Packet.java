package me.onixdev.ircchat.impl.c2;

import me.onixdev.ircchat.base.BasePacket;
import me.onixdev.ircchat.hwid.HwidUtil;

public class KeepAliveC2Packet extends BasePacket {
    public KeepAliveC2Packet() {
        super(7, PacketBound.SERVER, HwidUtil.getHwid());
    }

    @Override
    public String export() {
        return createBasePacket().toString();
    }
}
