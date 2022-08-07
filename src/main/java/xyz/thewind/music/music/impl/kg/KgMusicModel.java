package xyz.thewind.music.music.impl.kg;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.thewind.music.model.SongBean;
import xyz.thewind.music.music.IMusicApiModel;
import xyz.thewind.music.music.common.MusicConfigManager;
import xyz.thewind.music.music.impl.kg.bean.LyricData;
import xyz.thewind.music.music.impl.kg.bean.LyricSearchResponseBean;
import xyz.thewind.music.music.impl.kg.bean.search.MobSongDetail;
import xyz.thewind.music.music.impl.kg.bean.search.SearchResponse;
import xyz.thewind.music.music.impl.kg.bean.search.SongDetail;
import xyz.thewind.music.music.impl.kg.module.RequestModule;
import xyz.thewind.music.network.proxy.HttpProxy;
import xyz.thewind.music.util.Base64Util;
import xyz.thewind.music.util.GsonUtil;
import xyz.thewind.music.network.util.HttpUtil;

import java.net.Proxy;
import java.util.*;

import static xyz.thewind.music.music.common.MusicCommonConfig.*;


public class KgMusicModel implements IMusicApiModel {
    private static final Logger logger = LogManager.getLogger(KgMusicModel.class);

    public static String searchRequest(String keyword, int num) {
        String response = "";
        try {
            response = HttpUtil.get(RequestModule.buildSearchUrl(keyword, num));
        } catch (Exception e) {
            logger.error(e.toString());
        }
        return response;
    }

    public static String searchMusicWithProxy(String keyword, int num, Proxy proxy) {
        String response = "";
        try {
            response = HttpUtil.get(RequestModule.buildSearchUrl(keyword, num), null, proxy);
        } catch (Exception e) {
            logger.error(e.toString());
        }
        return response;
    }

    public static String downloadRequest(String hash, String quality) {
        return downloadRequest(hash);
    }

    private static String downloadRequest(String hash) {
        String userid = MusicConfigManager.getInstance().getKgUid();
        String token = MusicConfigManager.getInstance().getKgToken();
        String reqLink = RequestModule.buildDownloadRequestUrl(hash, userid, token);
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("x-router", "tracker.kugou.com");
        String response = HttpUtil.get(reqLink, headerMap);
        if (response.contains("http:") && response.contains("\"")) {
            response = response.substring(response.indexOf("\"url\":[\"") + "\"url\":[\"".length());
            response = response.substring(0, response.indexOf("\"")).replaceAll("\\\\", "");
        } else {
            response = "";
        }
        return response;

    }

    @Override
    public List<SongBean> searchMusic(String keyword, int num) {
        String response = searchRequest(keyword, num);
        return parseSearchResult(response);
    }

    @Override
    public SongBean searchDownloadLink(SongBean songBean, String quality) {
        String songId = songBean.getSongId();
        if (songId != null && songId.length() >= 32) {
            String[] hashArr = songBean.getSongId().split("-");
            Map<String, String> map = songBean.getDownloadLinkMap();
            songBean.setDownloadLinkMap(map);
            String hash = hashArr[0];
            if (quality.equals(QUALITY_ZQ) && hashArr.length == 4) {
                hash = hashArr[3];
            }
            if (quality.equals(QUALITY_SQ) && hashArr.length == 3) {
                hash = hashArr[2];
            } else if (quality.equals(QUALITY_HQ) && hashArr.length >= 2) {
                hash = hashArr[1];
            }
            map.put(quality, downloadRequest(hash, quality));
        }
        return songBean;
    }

    @Override
    public List<SongBean> searchWithDownload(String keyword, int num) {
        List<SongBean> songBeanList = searchMusic(keyword, num);
        for (SongBean bean : songBeanList) {
            String[] hashArr = bean.getSongId().split("-");
            String[] qualitys = {QUALITY_PQ, QUALITY_HQ, QUALITY_SQ, QUALITY_ZQ};
            Map<String, String> map = bean.getDownloadLinkMap();
            bean.setDownloadLinkMap(map);
            for (int i = 0; i < hashArr.length; i++) {
                map.put(qualitys[i], downloadRequest(hashArr[i], qualitys[i]));
            }
        }
        return songBeanList;
    }

    private List<SongBean> parseSearchResult(String response) {

        List<SongBean> list = new ArrayList<>();
        if (response.length() == 0) {
            System.out.println("parseSearchResult, length = 0");
            return list;
        }
        SearchResponse responseBean = GsonUtil.fromJson(response, SearchResponse.class);
        List<SongDetail> songDetailList = responseBean.data.lists;
        for (SongDetail detail : songDetailList) {
            SongBean song = new SongBean.Builder()
                    .songSrc(SRC_KG)
                    .songId(detail.getSongId())
                    .songName(detail.getSongName())
                    .singersId(detail.getSingersId())
                    .singersName(detail.getSingersName())
                    .albumId(detail.getAlbumId())
                    .albumName(detail.getAlbumName())
                    .quality(detail.getQuality())
                    .mvUrl(detail.getMvId())
                    .build();
            list.add(song);
        }
        if (list.isEmpty()) {
            System.out.println("parseSearchResult, error resp = " + response);
        }
        return list;
    }

    private String getCoverByHash(String hash) {
        String link = "https://expendablekmrcdn.kugou.com/container/v1/image?data=%5B%7B%22hash%22%3A%22" + hash + "%22%7D%5D";
        String response = HttpUtil.get(link, null);
        if (response.contains("\"sizable_avatar\":\"")) {
            response = response.substring(response.indexOf("\"sizable_avatar\":\"") + "\"sizable_avatar\":\"".length());
            response = response.substring(0, response.indexOf("\""));
            response = response.replace("{size}", "480");
        } else {
            response = "";
        }
        return response;

    }

    /**
     * 也可以先：https://wwwapi.kugou.com/yy/index.php?r=play/getdata&hash=0CCA78AF607DC52400D31C18BF036277&mid=12345
     * 获取album_id
     * 然后：https://wwwapi.kugou.com/yy/index.php?r=play/getdata&hash=0CCA78AF607DC52400D31C18BF036277&mid=12345&album_id=40602950
     * 获取歌词
     *
     * @param lyricId
     * @return
     */
    @Override
    public String getLyric(String lyricId) {
        if (lyricId == null || lyricId.length() < 32) {
            return "";
        }
        if (lyricId.contains("-")) {
            lyricId = lyricId.substring(0, lyricId.indexOf("-"));
        }

        String searchLink = "http://krcs.kugou.com/search?ver=1&hash=" + lyricId;
        String searchResponse = HttpUtil.get(searchLink, null);
        try {
            LyricSearchResponseBean bean = GsonUtil.fromJson(searchResponse, LyricSearchResponseBean.class);
            if (bean != null && bean.getCandidates() != null && bean.getCandidates().size() > 0) {
                LyricSearchResponseBean.Lyric lyric = bean.getCandidates().get(0);
                String requestUrl = "http://lyrics.kugou.com/download?&fmt=lrc&ver=1&id=" + lyric.getId() + "&accesskey=" + lyric.getAccesskey();
                LyricData lyricData = GsonUtil.fromJson(HttpUtil.get(requestUrl, null), LyricData.class);
                if (lyricData != null) {
                    return Base64Util.decode(lyricData.getContent());
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public SongBean searchSongDetail(String songId) {
        String sid = songId;
        if (songId.length() < 20) {
            return null;
        }
        if (songId.contains("-")) {
            songId = songId.substring(0, songId.indexOf("-"));
        }
        String url = "https://wwwapi.kugou.com/yy/index.php?r=play/getdata&hash=" + songId.toUpperCase(Locale.ROOT) + "&mid=12345";
        try {
            String response = HttpUtil.get(url, null);
            MobSongDetail.Data data = GsonUtil.fromJson(response, MobSongDetail.class).getData();
            if (data != null) {
                SongBean bean = new SongBean(SRC_KG, sid);
                bean.setSongName(data.getSongName());
                bean.setPicUrl(data.getPicUrl());
                List<MobSongDetail.Data.Authors> authors = data.getAuthors();
                List<String> singersName = new ArrayList<>();
                List<String> singerIds = new ArrayList<>();
                for (MobSongDetail.Data.Authors author : authors) {
                    singerIds.add(author.getSingerId());
                    singersName.add(author.getSingerName());
                }
                bean.setSingersId(singerIds);
                bean.setSingersName(singersName);
                bean.setAlbumName(data.getAlbumName());
                bean.setAlbumId(data.getAlbumId());
                return bean;
            }
        } catch (Exception r) {
            r.printStackTrace();
        }
        return null;
    }


}
