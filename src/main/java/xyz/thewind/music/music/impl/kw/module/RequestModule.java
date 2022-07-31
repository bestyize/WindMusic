package xyz.thewind.music.music.impl.kw.module;

import xyz.thewind.music.music.common.MusicCommonConfig;
import xyz.thewind.music.music.impl.kw.bean.download.DownloadResp;
import xyz.thewind.music.music.impl.kw.enc.KwEncrypt;
import xyz.thewind.music.util.GsonUtil;
import xyz.thewind.music.util.HttpUtil;

import java.util.HashMap;
import java.util.Map;

import static xyz.thewind.music.music.common.MusicCommonConfig.USER_AGENT_ANDROID;


public class RequestModule {
    private static String baseUrl = "http://nmobi.kuwo.cn/mobi.s?f=kuwo&q=";

    private static final Map<String, String> qualityMap = new HashMap<>() {{
        put(MusicCommonConfig.QUALITY_ZQ, "4000kflac");
        put(MusicCommonConfig.QUALITY_SQ, "2000kflac");
        put(MusicCommonConfig.QUALITY_HQ, "320kmp3");
        put(MusicCommonConfig.QUALITY_PQ, "192kmp3");
        put(MusicCommonConfig.QUALITY_LQ, "128kmp3");
    }};

    /**
     *  支持hires音质
     * @param songId
     * @param quality
     * @return
     */
    public static String getPlayUrl(String songId, String quality) {
        String resp = HttpUtil.get(buildDownReqLink(songId, quality));
        return parseDownloadLink(resp);
    }

    private static String buildDownReqLink(String songId, String quality) {
        String timeStamp = System.currentTimeMillis() + "";
        String br = qualityMap.get(quality);
        String format = "flac|mp3|aac";
        String token = null;
        StringBuilder reqParams = createCommonParams();
        reqParams.append("type=convert_url_with_sign");
        reqParams.append("&br=");
        reqParams.append(br);
        reqParams.append("&format=");
        reqParams.append(format);
        reqParams.append("&sig=");
        reqParams.append("0");
        reqParams.append("&rid=");
        reqParams.append(songId);
        reqParams.append("&priority=");
        reqParams.append("bitrate");
        reqParams.append("&loginUid=");
        reqParams.append("0");
        reqParams.append("&network=WIFI");
        reqParams.append("&loginSid=");
        reqParams.append("0");
        reqParams.append("&localUid=");
        reqParams.append(-1);
        reqParams.append("&mode=audition");
        reqParams.append("&token=");
        reqParams.append(token);
        reqParams.append("&timestamp=");
        reqParams.append(timeStamp);
        reqParams.append("&uid=");
        reqParams.append("0");
        return baseUrl + KwEncrypt.encrypt(reqParams.toString());
    }

    private static String parseDownloadLink(String resp){
        DownloadResp downloadResp = GsonUtil.fromJson(resp, DownloadResp.class);
        if (downloadResp == null) {
            return null;
        }
        return downloadResp.data.url;
    }

    private static StringBuilder createCommonParams() {
        StringBuilder sb = new StringBuilder();
        sb.append("user=");
        sb.append("&android_id=");
        sb.append("0");
        sb.append("&prod=");
        sb.append("0");
        sb.append("&corp=kuwo");
        sb.append("&newver=3");
        sb.append("&vipver=");
        sb.append("10.1.6.0");
        sb.append("&source=");
        sb.append("kwplayer_ar_10.1.6.0.apk");
        sb.append("&p2p=1");
        sb.append("&q36=");
        sb.append("0");
        sb.append("&loginUid=");
        sb.append("0");
        sb.append("&loginSid=");
        sb.append("0");
        sb.append("&notrace=");
        sb.append(1);
        sb.append("&");
        return sb;
    }


    /**
     * 下面的接口是支持无损下载，且不需要加密的，mid为歌曲ID，br有四种选项 分别是:空出（aac）、128kmp3、320kmp3、2000kflac
     * http://www.kuwo.cn/bd/search/getSongUrlByMid?mid=226543302&br=2000kflac&bdfrom=&c=nfmbhi6fxwaj
     * 下面接口不支持hires音质
     */

    public static String getDownloadLinkById(String songid, String quality) {
        String downloadLink = null;
        try {
            String link = "https://nmobi.kuwo.cn/mobi.s?f=kuwo&q=";
            String encParams = KwEncrypt.encrypt("&type=convert_url_with_sign" + qualityMap.get(quality) + "&sig=0&ss=ss&rid=" + songid + "&network=wifi");
            Map<String, String> headerMap = new HashMap<>();
            headerMap.put("User-Agent", USER_AGENT_ANDROID);
            String response = HttpUtil.get(link + encParams, headerMap);
            DownloadResp resp = GsonUtil.fromJson(response, DownloadResp.class);
            if (resp == null) {
                return null;
            }
            return resp.data.url;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return downloadLink;
    }
}
