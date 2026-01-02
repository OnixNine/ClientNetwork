package me.onixdev.ircchat.impl.s2;

import me.onixdev.ircchat.base.BasePacket;
import org.json.JSONObject;

public class ChatMessageS2packet extends BasePacket {
    private String message,author = "";
    public ChatMessageS2packet(String sender, String message,String author) {
        super(1,PacketBound.CLIENT, sender);
        this.message = message;
        this.author = author;
    }
    public ChatMessageS2packet(JSONObject data) {
        super(1,PacketBound.CLIENT,BasePacket.getUUID(data));
        this.message = data.getString("message");
        this.author = data.getString("author");
    }
    public String getMessage() {
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
