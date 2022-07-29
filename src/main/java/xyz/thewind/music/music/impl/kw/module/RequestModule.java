package xyz.thewind.music.music.impl.kw.module;

import xyz.thewind.music.music.impl.kw.enc.KwEncrypt;
import xyz.thewind.music.util.HttpUtil;

import java.util.HashMap;
import java.util.Map;

import static xyz.thewind.music.music.common.MusicCommonConfig.*;


public class RequestModule {
    private static final Map<String, String> songQualityMap = new HashMap<>() {{
        put(QUALITY_ZQ, "&br=4000&format=flac");
        put(QUALITY_SQ, "&br=2000&format=flac");
        put(QUALITY_HQ, "&br=320&format=mp3");
        put(QUALITY_PQ, "&br=192&format=mp3");
        put(QUALITY_LQ, "&br=128&format=mp3");
    }};


    public static StringBuilder createCommonParams(String uid) {
        StringBuilder sb = new StringBuilder();
        sb.append("user=");
        sb.append(uid);
        sb.append("&android_id=");
        sb.append("");
        sb.append("&prod=");
        sb.append("");
        sb.append("&corp=kuwo");
        sb.append("LONG_AUDIO_PARAM");
        sb.append("&vipver=");
        sb.append("1");

        sb.append("&q36=");
        sb.append("&key_ostar_q36=f2ce3c2ef68ddfd1b2bea7ed00001f314716");
        sb.append("&loginUid=");
        sb.append(uid);
        sb.append("&loginSid=");
        sb.append("0");
        sb.append("&notrace=");
        sb.append("1");
        sb.append("&");
        return sb;
    }

    public static void main(String[] args) {
        System.out.println(getPlayUrl("226543302", QUALITY_PQ));

        String link = "http://mobi.kuwo.cn/mobi.s?f=kwxs&q=";

        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("User-Agent", USER_AGENT_ANDROID);
        String response = HttpUtil.get(getPlayUrl("226543302", QUALITY_ZQ), headerMap);
        System.out.println(response);
    }

    public static String getPlayUrl(String songId, String quality) {
        String uid= "0";
        String sign = "0";
        String token = "";
        StringBuilder createCommonParams = createCommonParams(uid);
        createCommonParams.append("type=convert_url_with_sign");
        createCommonParams.append(songQualityMap.get(quality));
        createCommonParams.append("&sig=");
        createCommonParams.append(sign);
        createCommonParams.append("&rid=");
        createCommonParams.append(songId);
        createCommonParams.append("&priority=");
        createCommonParams.append("1");
        createCommonParams.append("&loginUid=");
        createCommonParams.append(uid);
        createCommonParams.append("&network=");
        createCommonParams.append("wifi");
        createCommonParams.append("&loginSid=");
        createCommonParams.append("0");
        createCommonParams.append("&localUid=");
        createCommonParams.append(-1);
        createCommonParams.append("&mode=audition");
        createCommonParams.append("&from=scenemusic");
        createCommonParams.append("&token=");
        createCommonParams.append(token);
        createCommonParams.append("&timestamp=");
        createCommonParams.append(System.currentTimeMillis());
        createCommonParams.append("&uid=");
        createCommonParams.append(uid);
        return "http://nmobi.kuwo.cn/mobi.s?f=kwxs&q="+KwEncrypt.encrypt(createCommonParams.toString());
    }

}
