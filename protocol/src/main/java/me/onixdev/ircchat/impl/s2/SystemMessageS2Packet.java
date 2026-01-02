package me.onixdev.ircchat.impl.s2;

import me.onixdev.ircchat.base.BasePacket;
import org.json.JSONObject;

public class SystemMessageS2Packet extends BasePacket {
    private String message;
    public SystemMessageS2Packet(String sender, String message) {
        super(2, PacketBound.SERVER, sender);
        this.message = message;
    }
    public SystemMessageS2Packet(JSONObject jsonObject) {
        super(2, PacketBound.SERVER, BasePacket.getUUID(jsonObject));
        this.message = jsonObject.getString("message");
    }
    public String getMessage() {
        return message;
    }
    @Override
    public String export() {
        JSONObject json = createBasePacket();
        json.put("message", message);
        return json.toString();
    }
}
