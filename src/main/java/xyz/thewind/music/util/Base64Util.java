package xyz.thewind.music.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Base64Util {

    final static Base64.Encoder encoder = Base64.getEncoder();
    final static Base64.Decoder decoder = Base64.getDecoder();

    public static String encode(String clearText) {
        if (clearText == null || clearText.length() == 0) {
            return "";
        }
        byte[] b = clearText.getBytes(StandardCharsets.UTF_8);
        return encoder.encodeToString(b);
    }

    public static String decode(String encodedText) {
        String decodeStr = null;
        try {
            decodeStr = new String(decoder.decode(encodedText), StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return decodeStr == null ? "" : decodeStr;
    }
}
