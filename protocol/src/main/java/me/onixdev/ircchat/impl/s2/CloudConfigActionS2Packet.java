package me.onixdev.ircchat.impl.s2;

import me.onixdev.ircchat.base.BasePacket;
import me.onixdev.ircchat.validation.ValidationHandler;
import org.json.JSONObject;

public class CloudConfigActionS2Packet extends BasePacket {
    private String config,message = "";
    public CloudConfigActionS2Packet(String sender, String config, String message) {
        super(3, PacketBound.CLIENT, sender);
    }
    public CloudConfigActionS2Packet(JSONObject jsonObject) {
        super(3,PacketBound.CLIENT,BasePacket.getUUID(jsonObject));
        ValidationHandler.check(jsonObject,"config","message");
        message = jsonObject.getString("message");
        config = jsonObject.getString("config");
    }

    public String getConfig() {
        return config;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String export() {
        JSONObject base = createBasePacket();
        base.put("message",message);
        base.put("config",config);
        return base.toString();
    }
}
