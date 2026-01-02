package me.onixdev.ircchat.impl.s2;

import me.onixdev.ircchat.base.BasePacket;
import org.json.JSONObject;

public class AuthFinishS2Packet extends BasePacket {
    private final String msg,role,username;
    private final int code;
    public AuthFinishS2Packet(String sender,int code,String msg,String r,String username) {
        super(0, PacketBound.CLIENT, sender);
        this.code = code;
        this.msg = msg;
        this.role = r;
        this.username = username;
    }
    public AuthFinishS2Packet(JSONObject jsonObject) {
        super(0,PacketBound.CLIENT,BasePacket.getUUID(jsonObject));
        this.role = jsonObject.getString("role");
        this.username = jsonObject.getString("user");
        code = jsonObject.getInt("code");
        msg = jsonObject.getString("msg");

    }

    public String getMsg() {
        return msg;
    }

    public int getCode() {
        return code;
    }
    public String getRole() {
        return role;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String export() {
        JSONObject jsonObject = createBasePacket();
        jsonObject.put("msg",msg);
        jsonObject.put("code",code);
        jsonObject.put("role",role);
        jsonObject.put("user",username);
        return jsonObject.toString();
    }
}
