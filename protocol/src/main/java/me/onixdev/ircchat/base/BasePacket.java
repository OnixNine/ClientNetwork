package me.onixdev.ircchat.base;

import org.json.JSONObject;

import java.util.UUID;

public abstract class BasePacket {
    protected final int id;
    protected final String sender;
    protected final PacketBound packetBound;

    public BasePacket(int id,PacketBound bound,String sender) {
        this.id = id;
        this.packetBound = bound;
        this.sender = sender;
    }

    public int getId() {
        return id;
    }
    public String getSender() {return sender;}
    public PacketBound getPacketBound() {return packetBound;}

    public abstract String export();

    protected JSONObject createBasePacket() {
        JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("bound", packetBound.name());
        json.put("sender", sender);
        return json;
    }
    public enum PacketBound {
        CLIENT,
        SERVER
    }
    public static String getUUID(JSONObject json) {
        return json.getString("sender");
    }
}
