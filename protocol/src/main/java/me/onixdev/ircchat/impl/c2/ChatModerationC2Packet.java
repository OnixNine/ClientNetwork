package me.onixdev.ircchat.impl.c2;

import me.onixdev.ircchat.base.BasePacket;
import me.onixdev.ircchat.validation.ValidationHandler;
import org.json.JSONObject;

public class ChatModerationC2Packet extends BasePacket {
    private final String action;
    private final String target;
    public ChatModerationC2Packet(String sender,String target,String action) {
        super(2, PacketBound.CLIENT, sender);
        this.action = action;
        this.target = target;
    }
    public ChatModerationC2Packet(JSONObject jsonObject) {
        super(2,PacketBound.CLIENT,BasePacket.getUUID(jsonObject));
        ValidationHandler.check(jsonObject,"target","action");
        target = jsonObject.getString("target");
        action = jsonObject.getString("action");
    }

    public String getAction() {
        return action;
    }

    public String getTarget() {
        return target;
    }

    @Override
    public String export() {
        JSONObject jsonObject = createBasePacket();
        jsonObject.put("action",action);
        jsonObject.put("target",target);
        return jsonObject.toString();
    }
}
