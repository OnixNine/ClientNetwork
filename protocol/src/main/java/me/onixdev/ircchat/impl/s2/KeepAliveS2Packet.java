package me.onixdev.ircchat.impl.s2;

import me.onixdev.ircchat.base.BasePacket;
import me.onixdev.ircchat.hwid.HwidUtil;
import org.json.JSONObject;

public class KeepAliveS2Packet extends BasePacket {
    private long time;
    public KeepAliveS2Packet() {
        super(7, PacketBound.CLIENT, HwidUtil.getHwid());
        time = System.currentTimeMillis();
    }
    public KeepAliveS2Packet(JSONObject jsonObject) {
        super(7, PacketBound.SERVER, getUUID(jsonObject));
        time = jsonObject.optLong("time",System.currentTimeMillis());
    }

    public long getTime() {
        return time;
    }
    public double getPing() {
        return System.currentTimeMillis() - time;
    }

    @Override
    public String export() {
        return createBasePacket().put("time",time).toString();
    }
}
