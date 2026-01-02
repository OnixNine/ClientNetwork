package me.onixdev.ircchat.impl.c2;

import me.onixdev.ircchat.base.BasePacket;
import org.json.JSONObject;

public class AuthC2Packet extends BasePacket {
    private String username,pass;
    public AuthC2Packet(String sender,String username,String pass) {
        super(0, PacketBound.CLIENT, sender);
        this.username = username;
        this.pass = pass;
    }
    public AuthC2Packet(JSONObject jsonObject) {
        super(0, PacketBound.CLIENT,BasePacket.getUUID(jsonObject));
        this.username = jsonObject.getString("username");
        this.pass = jsonObject.getString("pass");
    }

    public String getUsername() {
        return username;
    }
    public String getPass() {
        return pass;
    }

    @Override
    public String export() {
        JSONObject json = createBasePacket();
        json.put("username",username);
        json.put("pass",pass);
        return json.toString();
    }
}
