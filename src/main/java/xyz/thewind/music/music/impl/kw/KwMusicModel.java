package xyz.thewind.music.music.impl.kw;

import com.sun.jdi.PathSearchingVirtualMachine;
import xyz.thewind.music.model.SongBean;
import xyz.thewind.music.music.IMusicApiModel;
import xyz.thewind.music.music.impl.kw.bean.LyricResponseBean;
import xyz.thewind.music.music.impl.kw.bean.SearchResponseBean;
import xyz.thewind.music.music.impl.kw.bean.WebSongDetail;
import xyz.thewind.music.music.impl.kw.bean.download.DownloadResp;
import xyz.thewind.music.music.impl.kw.bean.search.SongDetail;
import xyz.thewind.music.music.impl.kw.bean.search.SearchResp;
import xyz.thewind.music.music.impl.kw.enc.KwEncrypt;
import xyz.thewind.music.util.GsonConverter;
import xyz.thewind.music.util.HttpRequestHelper;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static xyz.thewind.music.music.common.MusicCommonConfig.*;
import static xyz.thewind.music.util.HttpRequestHelper.downloadWebSiteUseGet;


public class KwMusicModel implements IMusicApiModel {
    private static final HashMap<String, String> qualityMap;
    private static final Map<String, String> mvQualityMap = new HashMap<>();

    static {
        qualityMap = new HashMap<>();
        qualityMap.put(QUALITY_ZQ, "&br=4000&format=flac");
        qualityMap.put(QUALITY_SQ, "&br=2000&format=flac");
        qualityMap.put(QUALITY_HQ, "&br=320&format=mp3");
        qualityMap.put(QUALITY_PQ, "&br=192&format=mp3");
        qualityMap.put(QUALITY_LQ, "&br=128&format=mp3");
        mvQualityMap.put(QUALITY_SQ, "MP4BD");
        mvQualityMap.put(QUALITY_HQ, "MP4UL");
        mvQualityMap.put(QUALITY_PQ, "MP4");
        mvQualityMap.put(QUALITY_LQ, "MP4L");
    }

    @Override
    public List<SongBean> searchMusic(String keyword, int num) {
        return parseSearchResult(doSearchMusic(keyword, num));
    }

    @Override
    public SongBean searchDownloadLink(SongBean songBean, String quality) {
        String downloadLink = getDownloadLinkById(songBean.getSongId(), quality);
        if (downloadLink == null) {
            return songBean;
        }
        Map<String, String> map = songBean.getDownloadLinkMap();
        map.put(quality, downloadLink);
        return songBean;
    }

    @Override
    public List<SongBean> searchWithDownload(String keyword, int num) {
        List<SongBean> songBeanList = searchMusic(keyword, num);
        for (SongBean bean : songBeanList) {
            searchDownloadLink(bean, QUALITY_ZQ);
            searchDownloadLink(bean, QUALITY_SQ);
            searchDownloadLink(bean, QUALITY_HQ);
            searchDownloadLink(bean, QUALITY_PQ);
            searchDownloadLink(bean, QUALITY_LQ);
        }
        return songBeanList;
    }

    private String doSearchMusic(String keyword, int num) {
        String reqLink = "http://search.kuwo.cn/r.s?user=0&all=" + URLEncoder.encode(keyword, StandardCharsets.UTF_8) + "&pn=0&rn=" + num + "&uid=0&ver=kwplayer_ar_10.1.5.0&vipver=1&newver=3&correct=1&ft=music&cluster=0&strategy=2012&encoding=utf8&rformat=json&vermerge=1&mobi=1&searchapi=5";
        return HttpRequestHelper.downloadWebSiteUseGet(reqLink);
    }

    private List<SongBean> parseSearchResult(String response) {
        List<SongBean> list = new LinkedList<>();
        if (response.length() == 0) {
            return list;
        }
        SearchResp resp = GsonConverter.fromJson(response, SearchResp.class);
        if (resp == null) {
            return list;
        }
        List<SongDetail> songDetailList = resp.songList;
        for (SongDetail detail : songDetailList) {
            SongBean songBean = new SongBean(SRC_KW, detail.getSongId(), detail.getSongName(), detail.getSingersId(), detail.getSingersName(),
                    detail.getAlbumId(), detail.getAlbumName(), detail.getQuality(), detail.getPicUrl(), "", "");
            list.add(songBean);
        }
        return list;
    }

    /**
     * 下面的接口是支持无损下载，且不需要加密的，mid为歌曲ID，br有四种选项 分别是:空出（aac）、128kmp3、320kmp3、2000kflac
     * http://www.kuwo.cn/bd/search/getSongUrlByMid?mid=226543302&br=2000kflac&bdfrom=&c=nfmbhi6fxwaj
     */
    private String getDownloadLinkById(String songid, String quality) {
        String downloadLink = null;
        try {
            String link = "https://nmobi.kuwo.cn/mobi.s?f=kuwo&q=";
            String encParams = KwEncrypt.encrypt("&type=convert_url_with_sign" + qualityMap.get(quality) + "&sig=0&ss=ss&rid=" + songid + "&network=wifi");
            Map<String, String> headerMap = new HashMap<>();
            headerMap.put("User-Agent", USER_AGENT_ANDROID);
            String response = downloadWebSiteUseGet(link + encParams, headerMap);
            DownloadResp resp = GsonConverter.fromJson(response, DownloadResp.class);
            if (resp == null) {
                return null;
            }
            return resp.data.url;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return downloadLink;
    }

    /**
     * http://m.kuwo.cn/newh5/singles/songinfoandlrc?musicId=80488731
     *
     * @param lyricId
     * @return
     */
    @Override
    public String getLyric(String lyricId) {
        StringBuilder sb = new StringBuilder();
        try {
            String link = "http://m.kuwo.cn/newh5/singles/songinfoandlrc?musicId=" + lyricId;
            LyricResponseBean bean = GsonConverter.fromJson(HttpRequestHelper.downloadWebSiteUseGet(link, null), LyricResponseBean.class);

            if (bean != null) {
                List<LyricResponseBean.Data.Pair> pairList = bean.getData().getLrclist();
                if (pairList != null) {
                    for (LyricResponseBean.Data.Pair pair : pairList) {
                        String lyric = pair.getLineLyric();
                        String time = convertToLyricTime(pair.getTime());
                        String line = "[" + time + "]" + lyric + "\n";
                        sb.append(line);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public String convertToLyricTime(String time) {
        if (time == null || time.length() < 4 || !time.contains(".") || time.lastIndexOf(".") + 2 > time.length() - 1) {
            return "";
        }
        time = time.substring(0, time.indexOf(".") + 3);
        int len = time.length();

        String subSecond = time.substring(len - 2, len);
        String secondStr = time.substring(0, len - 3);
        int timeInSecond = Integer.parseInt(secondStr);
        StringBuilder sb = new StringBuilder();
        int second = timeInSecond % 60;
        int minute = timeInSecond / 60;
        if (minute < 10) {
            sb.append("0");
        }
        sb.append(minute).append(":");
        if (second < 10) {
            sb.append("0");
        }
        sb.append(second).append(".").append(subSecond);
        return sb.toString();
    }

    /**
     * http://www.kuwo.cn/url?rid=80488731&response=url&format=mp4&type=convert_url
     *
     * @param mvId
     * @return
     */
    @Override
    public String searchMvUrl(String mvId) {
        String link = "http://www.kuwo.cn/url?response=url&format=mp4&type=convert_url&rid=" + mvId;
        String url = downloadWebSiteUseGet(link, null);
        return (!url.startsWith("http")) ? "" : url;
    }

    /**
     * 方式2，可选质量
     *
     * @param mvId
     * @param quality
     * @return
     */
    public String searchMvUrl(String mvId, String quality) {
        String link = "http://mobi.kuwo.cn/mobi.s?f=kuwo&q=";
        String encParams = KwEncrypt.encrypt("&type=convert_mv_url2&format=mp4&quality=" + mvQualityMap.get(quality) + "&sig=0&rid=" + mvId + "&network=wifi");
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("User-Agent", USER_AGENT_ANDROID);
        String response = downloadWebSiteUseGet(link + encParams, headerMap);
        if (response.contains("url=") && response.contains("sig=")) {
            return response.substring(response.indexOf("url=") + 4, response.indexOf("sig="));
        }
        return "";
    }

    @Override
    public SongBean searchSongDetail(String songId) {
        String requestLink = "http://www.kuwo.cn/api/www/music/musicInfo?mid=" + songId;
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Cookie", "kw_token=62COOIBCS34");
        headerMap.put("csrf", "62COOIBCS34");
        //headerMap.put("Referer","https://kuwo.cn/search/list?key="+key);
        headerMap.put("Keep-Alive", "close");
        String response = downloadWebSiteUseGet(requestLink, headerMap);
        SongBean songBean = new SongBean("KW", songId);
        try {
            WebSongDetail detail = GsonConverter.fromJson(response, WebSongDetail.class);
            WebSongDetail.Data data = detail.getData();
            if (data != null) {
                songBean.setSongName(data.getSongName());
                songBean.setSingersId(Collections.singletonList(data.getSingerId() + ""));
                songBean.setPicUrl(data.getPic());
                songBean.setAlbumName(data.getAlbumName());
                songBean.setQuality(data.hasLossless ? "SQ" : "HQ");
                songBean.setAlbumId(data.albumId + "");
                songBean.setSingersName(Collections.singletonList(data.getSingerName()));
                return songBean;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }




}
