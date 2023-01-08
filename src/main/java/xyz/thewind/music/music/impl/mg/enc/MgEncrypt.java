package xyz.thewind.music.music.impl.mg.enc;

import xyz.thewind.music.network.util.HttpUtil;
import xyz.thewind.music.util.Base64Util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static xyz.thewind.music.util.Md5Encrypt.convertToMd5;

public class MgEncrypt {
    public static String searchMusic(String keyword, int page, int num) {
        try {
            keyword = keyword.trim();
            String userPhoneNum = Base64.getEncoder().encodeToString(genPhoneNumber().getBytes(StandardCharsets.UTF_8));
            String requestUrl = requestUrl(keyword, num, page);
            Map<String, String> requestHeader = requestHeader(keyword, userPhoneNum);
            return HttpUtil.get(requestUrl, requestHeader).trim();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    public static Map<String, String> requestHeader(String keyword, String msiDn) {
        String deviceId = genDeviceId(32);
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("deviceId", deviceId);
        hashMap.put("appId", "yyapp2");
        hashMap.put("android_id", "afad815163f1d394");
        hashMap.put("ua", "Android_migu");
        hashMap.put("version", "7.21.0");
        hashMap.put("uiVersion", "A_music_3.12.4");
        hashMap.put("ip", Base64Util.encode("114.88.161.51"));
        //hashMap.put("channel", "0146951");
        String timestamp = System.currentTimeMillis() + "";
        String sig = "6cdc72a439cef99a3418d2a78aa28c73";//咪咕签名的MD5
        String sign = convertToMd5(keyword + sig + "yyapp2d16148780a1dcc7408e06336b98cfd50" + deviceId + msiDn + timestamp);
        hashMap.put("sign", sign);
        hashMap.put("timestamp", timestamp);
        hashMap.put("msisdn", msiDn);
        hashMap.put("os", "android 12");
        hashMap.put("mgm-Network-type", "04");
        hashMap.put("mode", "android");
        return hashMap;
    }

    public static String requestUrl(String keyword, int num, int page) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("feature", "1011000000");
        hashMap.put("pageNo", page + "");
        hashMap.put("pageSize", num + "");
        hashMap.put("sort", "0");
        hashMap.put("text", URLEncoder.encode(keyword, StandardCharsets.UTF_8));
        String searchSwitch = null;
        // {"song":1,"album":0,"singer":0,"tagSong":1,"mvSong":0,"bestShow":1,"songlist":0,"lyricSong":0}
        searchSwitch = URLEncoder.encode("{\"song\":1,\"album\":0,\"singer\":0,\"tagSong\":1,\"mvSong\":0,\"bestShow\":0,\"songlist\":0,\"lyricSong\":0}", StandardCharsets.UTF_8);
        hashMap.put("searchSwitch", searchSwitch);
        hashMap.put("sid", getSid());
        hashMap.put("isCopyright", "1");
        hashMap.put("isCorrect", "1");
        StringBuilder sb = new StringBuilder();
        for (String key : hashMap.keySet()) {
            sb.append(key).append("=").append(hashMap.get(key)).append("&");
        }
        return "https://jadeite.migu.cn/music_search/v3/search/searchAll?" + sb;
    }

    public static String getSid() {
        String uuid = UUID.randomUUID().toString();
        String uuid2 = UUID.randomUUID().toString();
        String replace = uuid.replace("-", "");
        return replace + uuid2.replace("-", "");
    }

    public static String genPhoneNumber() {
        int[] nums = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        StringBuilder sb = new StringBuilder(11);
        sb.append("188");
        Random ran = new Random(System.currentTimeMillis());
        for (int i = 0; i < 8; i++) {
            sb.append(nums[ran.nextInt(nums.length) % nums.length]);
        }
        return sb.toString();
    }

    public static String genDeviceId(int len) {
        char[] cs = "01234567890ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder(11);
        Random ran = new Random(System.currentTimeMillis());
        for (int i = 0; i < len; i++) {
            sb.append(cs[ran.nextInt(cs.length) % cs.length]);
        }
        return sb.toString();
    }
}
