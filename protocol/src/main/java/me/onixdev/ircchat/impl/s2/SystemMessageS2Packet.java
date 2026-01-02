package me.onixdev.ircchat.impl.s2;

import me.onixdev.ircchat.base.BasePacket;
import org.json.JSONObject;

public class SystemMessageS2Packet extends BasePacket {
    private String message;
    private int code;
    public SystemMessageS2Packet(String sender, String message,int code) {
        super(2, PacketBound.CLIENT, sender);
        this.message = message;
        this.code = code;
    }
    public SystemMessageS2Packet(JSONObject jsonObject) {
        super(2, PacketBound.CLIENT, BasePacket.getUUID(jsonObject));
        this.message = jsonObject.getString("message");
        this.code = jsonObject.getInt("code");
    }
    public String getMessage() {
        return message;
    }
    public int getCode() {
        return code;
    }
    @Override
    public String export() {
        JSONObject json = createBasePacket();
        json.put("message", message);
        json.put("code",code);
        return json.toString();
    }
}
