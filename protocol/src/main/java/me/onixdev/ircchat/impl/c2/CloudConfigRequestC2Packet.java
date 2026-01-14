package me.onixdev.ircchat.impl.c2;

import me.onixdev.ircchat.base.BasePacket;
import me.onixdev.ircchat.validation.ValidationHandler;
import org.json.JSONObject;

public class CloudConfigRequestC2Packet extends BasePacket {
    private String name;
    public CloudConfigRequestC2Packet( String sender,String name) {
        super(4, PacketBound.CLIENT, sender);
        this.name = name;
    }
    public CloudConfigRequestC2Packet(JSONObject jsonObject) {
        super(4,PacketBound.CLIENT,BasePacket.getUUID(jsonObject));
        ValidationHandler.check(jsonObject,"name");
    }

    public String getName() {
        return name;
    }

    @Override
    public String export() {
        JSONObject base = createBasePacket();
        base.put("name",name);
        return base.toString();
    }
}
