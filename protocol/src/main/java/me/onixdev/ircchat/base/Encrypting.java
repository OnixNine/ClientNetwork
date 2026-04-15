package me.onixdev.ircchat.base;


import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Encrypting {
    public static final Encrypting INSTANCE = new Encrypting();
    private Encrypting() {
    }
    private String key;
    public void setKey(String key) {this.key = key;}
    public String getKey() {return key;}

    public String encrypt(String input) {
        byte[] xorResult = applyXor(input.getBytes(StandardCharsets.UTF_8), key);
        return Base64.getEncoder().encodeToString(xorResult);
    }

    public String decrypt(String base64Input) {
        byte[] decoded = Base64.getDecoder().decode(base64Input);
        byte[] xorResult = applyXor(decoded, key);
        return new String(xorResult, StandardCharsets.UTF_8);
    }

    private byte[] applyXor(byte[] data, String key) {
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        byte[] result = new byte[data.length];

        for (int i = 0; i < data.length; i++) {
            result[i] = (byte) (data[i] ^ keyBytes[i % keyBytes.length]);
        }
        return result;
    }
}

