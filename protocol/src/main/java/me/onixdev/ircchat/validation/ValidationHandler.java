package me.onixdev.ircchat.validation;

import org.json.JSONObject;

public class ValidationHandler {
    public static void check(JSONObject json,String... fields) {
        for (String str : fields) {
            if (json.has(str)) {
                return;
            }
            throw new RuntimeException("no: " + str);
        }
    }
}
