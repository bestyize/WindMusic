package xyz.thewind.music.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Base64Converter {

    final static Base64.Encoder encoder = Base64.getEncoder();
    final static Base64.Decoder decoder = Base64.getDecoder();

    public static String encode(String clearText) {
        byte[] b = clearText.getBytes(StandardCharsets.UTF_8);
        return encoder.encodeToString(b);
    }

    public static String decode(String encodedText) {
        return new String(decoder.decode(encodedText), StandardCharsets.UTF_8);
    }
}
