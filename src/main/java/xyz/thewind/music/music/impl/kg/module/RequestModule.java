package xyz.thewind.music.music.impl.kg.module;

import xyz.thewind.music.music.impl.qq.module.ThirdPartApi;
import xyz.thewind.music.util.HttpUtil;
import xyz.thewind.music.util.Md5Encrypt;

import java.util.*;

public class RequestModule {
    public static String getPlayUrl(String songId, String quality) {
        return ThirdPartApi.getPlayUrl(songId, quality);
    }

    public static String buildSearchUrl(String keyword, int num) {
        String searchUrl = "https://complexsearchretry.kugou.com/v2/search/song";
        String appkey = "OIlwieks28dk2k092lksi2UIkp";
        Map<String, Object> map = new HashMap<>();
        map.put("userid", "0");
        map.put("area_code", "1");
        map.put("appid", "1005");
        map.put("page", "1");
        map.put("token", "");
        map.put("privilegefilter", "0");
        map.put("requestid", UUID.randomUUID());
        map.put("pagesize", num + "");
        map.put("clienttime", System.currentTimeMillis() / 1000);
        map.put("iscorrection", "1");
        map.put("uuid", "0");
        map.put("keyword", keyword);
        map.put("mid", "0");
        map.put("dfid", "0");
        map.put("clientver", "99999");
        map.put("platform", "AndroidFilter");
        map.put("signature", getSignature(appkey, map));
        return HttpUtil.buildGetUrl(searchUrl, map);
    }

    public static String buildDownloadRequestUrl(String hash, String userId, String userToken) {
        String dfid = "-";
        String pid = "2";
        String mid = "0";
        String cmd = "26";
        String clienttime = System.currentTimeMillis() / 1000 + "";

        String uuid = "0";
        String area_code = "1";
        String behavior = "play";
        String appid = "1005";
        String module = "";
        String vipType = "6";
        String ptype = "0";
        String mtype = "2";
        String album_id = "2900867";
        String pidversion = "3001";
        String pidVersionSecret = "57ae12eb6890223e355ccfcb74edf70d";
        String clientver = "10479";
        String version = "10479";
        String album_audio_id = "album_audio_id";

        Map<String, Object> map = new HashMap<>();
        map.put("dfid", dfid);
        map.put("hash", hash);
        map.put("mtype", mtype);
        map.put("album_id", album_id);
        map.put("album_audio_id", album_audio_id);
        map.put("module", module);
        map.put("behavior", behavior);
        map.put("cmd", cmd);

        map.put("uuid", uuid);
        map.put("clientver", clientver);
        map.put("clienttime", clienttime);

        map.put("pid", pid);
        map.put("appid", appid);
        map.put("mid", mid);
        map.put("version", version);
        map.put("token", userToken);

        map.put("vipType", vipType);
        map.put("userid", userId);
        map.put("area_code", area_code);
        map.put("dfid", dfid);
        map.put("ptype", ptype);
        map.put("pidversion", pidversion);
        map.put("key", Md5Encrypt.convertToMd5(hash + pidVersionSecret + appid + mid + userId));
        String appkey = "OIlwieks28dk2k092lksi2UIkp";
        map.put("signature", getSignature(appkey, map));
        return HttpUtil.buildGetUrl("https://gateway.kugou.com/v3/url" , map);
    }

    public static String getSignature(String str, Map<String, Object> map) {
        if (map == null || map.size() < 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        Set<String> keySet = map.keySet();
        String[] strArr = keySet.toArray(new String[keySet.size()]);
        Arrays.sort(strArr, String::compareTo);
        for (String str3 : strArr) {
            sb.append(str3);
            sb.append("=");
            sb.append(map.get(str3));
        }
        return Md5Encrypt.convertToMd5(str + sb + str);
    }
}
