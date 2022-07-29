package xyz.thewind.music.music.impl.wy.module;

import xyz.thewind.music.util.Md5Encrypt;

import java.util.HashMap;
import java.util.Map;

import static xyz.thewind.music.util.HttpUtil.get;

public class ThirdPartApi {
    public static String getPlayUrl(String songMid, String quality) {
        return SixYinDumper.requestUrl(songMid, quality);
    }

    private static class SixYinDumper {
        private static final String sixYinHost = "https://api.sixyin.com";
        private static final Map<String, String> qualityMap = new HashMap<>();

        static {
            qualityMap.put("SQ", "flac");
            qualityMap.put("HQ", "320k");
            qualityMap.put("PQ", "128k");
            qualityMap.put("LQ", "128k");
        }

        public static String requestUrl(String songMid, String quality) {
            String url = "/url/wy/" + songMid + "/" + qualityMap.get(quality);
            String auth = Md5Encrypt.convertToMd5(url + "wycheck").substring(0, 16);
            Map<String, String> headerMap = new HashMap<>();
            headerMap.put("user-agent", "lx-music request");
            headerMap.put("lxm", "0b2fcf4ff7cc722cf7f5f0ac080bf62c76ce74ccf4cdf6340ecf84b25d1c2b80d81088339d730cca7d5d5c813814881d6d01&1005000");
            headerMap.put("wycheck", auth);
            String response = get(sixYinHost + url, headerMap);
            if (response.contains("\"data\":\"")) {
                response = response.substring(response.indexOf("\"data\":\"") + "\"data\":\"".length());
                if (response.contains("\"")) {
                    response = response.substring(0, response.indexOf("\""));
                }
            }
            return response;
        }
    }
}
