package me.onixdev.ircchat.impl.s2;

import com.sun.net.httpserver.Authenticator;
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
        this.role = jsonObject.optString("role","user");
        this.username = jsonObject.optString("user","userOne");
        code = jsonObject.optInt("code");
        msg = jsonObject.optString("msg");

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
    public AuthStatus getStatus() {
        return AuthStatus.getById(getCode());
    }
    public enum AuthStatus {
        Success(100),
        InvalidPassWord(201),
        NotFound(404);

        private final int id;

        AuthStatus(int i) {
            id = i;
        }

        public int getId() {
            return id;
        }
        static AuthStatus getById(int id) {
            if (id == 100) return Success;
            if (id == 201) return InvalidPassWord;
            return NotFound;
        }
    }

}
