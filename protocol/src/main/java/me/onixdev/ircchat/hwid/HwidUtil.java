package me.onixdev.ircchat.hwid;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class HwidUtil {
    public static String getHwid() {
        try {
            StringBuilder hardwareInfo = new StringBuilder();
            hardwareInfo.append(System.getenv("PROCESSOR_IDENTIFIER"));
            hardwareInfo.append(System.getenv("PROCESSOR_LEVEL"));
            hardwareInfo.append(System.getenv("PROCESSOR_REVISION"));
            hardwareInfo.append(System.getenv("PROCESSOR_ARCHITECTURE"));
            hardwareInfo.append(System.getenv("PROCESSOR_ARCHITEW6432"));
            hardwareInfo.append(System.getProperty("os.name"));
            hardwareInfo.append(System.getProperty("os.arch"));
            hardwareInfo.append(System.getProperty("os.version"));
            hardwareInfo.append(System.getenv("COMPUTERNAME"));
            hardwareInfo.append(System.getenv("USERNAME"));
            hardwareInfo.append(System.getenv("HOMEDRIVE"));
            String salted = hardwareInfo.toString() + "SCAMWARE_SALT_2024";
            return new String( Base64.getEncoder().encode(salted.getBytes(StandardCharsets.UTF_8)));

        } catch (Exception e) {
            return "";
        }
    }
}
