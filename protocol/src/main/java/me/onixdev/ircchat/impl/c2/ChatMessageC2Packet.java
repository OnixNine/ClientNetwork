package me.onixdev.ircchat.impl.c2;

import me.onixdev.ircchat.base.BasePacket;
import org.json.JSONObject;

import java.util.UUID;

public class ChatMessageC2Packet extends BasePacket {
    private String message,author = "";
    public ChatMessageC2Packet(String sender,String message,String author) {
        super(1,PacketBound.CLIENT, sender);
        this.message = message;
        this.author = author;
    }
    public ChatMessageC2Packet(JSONObject data) {
        super(1,PacketBound.CLIENT,BasePacket.getUUID(data));
        this.message = data.getString("message");
        this.author = data.getString("author");
    }
    public String getMessage() {
        if (message == null) return "";
        if (message.isEmpty()) return "";
        return message;
    }
    public String getAuthor() {
        return author;
    }
    @Override
    public String export() {
        JSONObject data = createBasePacket();
        data.put("message",message);
        data.put("author",author);
        return data.toString();
    }
}
