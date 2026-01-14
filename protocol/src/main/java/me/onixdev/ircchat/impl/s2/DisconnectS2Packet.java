package me.onixdev.ircchat.impl.s2;

import me.onixdev.ircchat.base.BasePacket;
import org.json.JSONObject;

public class DisconnectS2Packet extends BasePacket {
    private String message;
    public DisconnectS2Packet(String sender,String message) {
        super(5,PacketBound.CLIENT,sender);
        this.message = message;
    }
    public DisconnectS2Packet(JSONObject jsonObject) {
        super(5,PacketBound.CLIENT,BasePacket.getUUID(jsonObject));
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String export() {
        JSONObject jsonObject = createBasePacket();
        jsonObject.put("msg",message);
        return jsonObject.toString();
    }
}
