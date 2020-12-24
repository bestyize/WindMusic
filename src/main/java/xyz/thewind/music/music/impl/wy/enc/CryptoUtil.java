package xyz.thewind.music.music.impl.wy.enc;

import xyz.thewind.music.util.HttpRequestHelper;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class CryptoUtil {
    //调试网页可以获取以下三个参数，https://s3.music.126.net/web/s/core_66ffd7678210e9870810be4823d4098d.js?66ffd7678210e9870810be4823d4098d:formatted
    private static final String presetKey = "0CoJUm6Qyw8W8jud";
    private static final String pubKey = "010001";
    private static final String modulus = "00e0b509f6259df8642dbc35662901477df22677ec152b5ff68ace615bb7b725152b3ab17a876aea8a5aa76d2e417629ec4ee341f56135fccf695280104e0312ecbda92557c93870114af6c9d05c4f7f0c3685b7a46bee255932575cce10b424d813cfe4875d3e82047b97ddef52741d546b8e289dc6935b3ece0462db0a22b8e7";
    private static final String linuxapiKey = "rFgB&h#%2?^eDg:Q";
    private static final String iv = "0102030405060708";

    /**
     * 产生16位的随机字符串
     *
     * @param size
     * @return
     */
    private static String createSecretKey(int size) {
        String keys = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder key = new StringBuilder();
        for (int i = 0; i < size; i++) {
            double index = Math.floor(Math.random() * keys.length());
            key.append(keys.charAt((int) index));
        }
        return key.toString();
    }
    /**
     * aes加密
     *
     * @param content
     * @param key
     * @return
     */
    private static String aesEncrypt(String content, String key,String mode,String iv) {
        String result = null;
        if (content == null || key == null)
            return null;
        try {
            Cipher cipher = Cipher.getInstance(mode);
            if(iv.length()==0){
                cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES"));
            }else {
                cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES"),
                        new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8)));
            }
            byte[] bytes = cipher.doFinal(content.getBytes(StandardCharsets.UTF_8));
            result = Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;

    }

    /**
     * 长度不够前面补充0
     *
     * @param str
     * @param size
     * @return
     */
    private static String zFill(String str, int size) {
        StringBuilder strBuilder = new StringBuilder(str);
        while (strBuilder.length() < size) {
            strBuilder.insert(0, "0");
        }
        str = strBuilder.toString();
        return str;
    }

    /**
     * rsa加密
     *
     * @param text
     * @param pubKey
     * @param modulus
     * @return
     */
    private static String rsaEncrypt(String text, String pubKey, String modulus) {
        // 反转字符串
        text = new StringBuffer(text).reverse().toString();

        BigInteger biText = new BigInteger(strToHex(text), 16);
        BigInteger biEx = new BigInteger(pubKey, 16);
        BigInteger biMod = new BigInteger(modulus, 16);
        BigInteger biRet = biText.modPow(biEx, biMod);
        return zFill(biRet.toString(16), 256);
    }

    /**
     * 字符串转成16进制字符串
     *
     * @param s
     * @return
     */
    private static String strToHex(String s) {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            int ch = s.charAt(i);
            String s4 = Integer.toHexString(ch);
            str.append(s4);
        }
        return str.toString();
    }

    /**
     * 5 加密方法
     *
     * @param content
     * @return
     */
    public static String weapiEncrypt(String content) {
        String mode="AES/CBC/PKCS5Padding";
        String key = createSecretKey(16);//n5IA3AI20heJu9DW
//        String key="n5IA3AI20heJu9DW";
        String encText = aesEncrypt(aesEncrypt(content, presetKey,mode,iv), key,mode,iv);//76SjbrPbuyyYekhAc2YT6kNjIC6kOt4QwAz2+oBWiCVqLQvG3XcX3LCW4PDf3tmK
        String encSecKey = rsaEncrypt(key, pubKey, modulus);
        try {
            return "params="+ URLEncoder.encode(encText,"UTF-8")+"&encSecKey="+URLEncoder.encode(encSecKey,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";

    }

    /**
     * 加密方法
     *
     * @param content
     * @return
     */
    public static String linuxapiEncrypt(String content) {
        String mode="AES/ECB/PKCS5Padding";
        String params = aesEncrypt(content, linuxapiKey, mode,"").toUpperCase();
        return "params="+params;

    }


    public static void main(String[] args) {
        String id="65766";
        String quality="999000";
        String text = "{\"ids\":[\"" + id + "\"],\"br\":" + quality + ",\"csrf_token\":\"\"}";
        Map<String,String> headerMap=new HashMap<>();
        headerMap.put("Cookie","MUSIC_U=ba0a04e302da6a12399d1817fe4eae9dd7c6b7f4865bf4fd9b986b5c8c07893333a649814e309366;");
        String postContentWebApi = weapiEncrypt(text);
        System.out.println(HttpRequestHelper.downloadWebSiteUsePost("https://music.163.com/weapi/song/enhance/player/url",postContentWebApi,headerMap));
    }
}