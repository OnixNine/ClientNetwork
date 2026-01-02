package me.onixdev.ircchat.impl.s2;

import me.onixdev.ircchat.base.BasePacket;
import org.json.JSONObject;

public class ChatMessageS2packet extends BasePacket {
    private final String message;
    private final String author;
    private final String role;

    public ChatMessageS2packet(String sender, String message, String author, String role) {
        super(1, PacketBound.CLIENT, sender);
        this.message = message;
        this.author = author;
        this.role = role;
    }

    public ChatMessageS2packet(JSONObject data) {
        super(1, PacketBound.CLIENT, BasePacket.getUUID(data));
        this.message = data.getString("message");
        this.author = data.getString("author");
        this.role = data.getString("role");
    }

    public String getMessage() {
        return message;
    }

    public String getAuthor() {
        return author;
    }

    public String getRole() {return role;}

    @Override
    public String export() {
        JSONObject data = createBasePacket();
        data.put("message", message);
        data.put("author", author);
        data.put("role", role);
        return data.toString();
    }
}
