package xyz.thewind.music.music.impl.wy;

import xyz.thewind.music.model.SongBean;
import xyz.thewind.music.music.IMusicApiModel;
import xyz.thewind.music.music.common.MusicConfigManager;
import xyz.thewind.music.music.impl.wy.bean.DownloadResponseBean;
import xyz.thewind.music.music.impl.wy.bean.LyricResponse;
import xyz.thewind.music.music.impl.wy.bean.MusicDetail;
import xyz.thewind.music.music.impl.wy.bean.MvDownloadBean;
import xyz.thewind.music.music.impl.wy.bean.search.Album;
import xyz.thewind.music.music.impl.wy.bean.search.SearchResponse;
import xyz.thewind.music.music.impl.wy.bean.search.Singer;
import xyz.thewind.music.music.impl.wy.bean.search.SongDetail;
import xyz.thewind.music.util.GsonUtil;
import xyz.thewind.music.util.HttpUtil;
import xyz.thewind.music.util.IpHelper;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

import static xyz.thewind.music.music.common.MusicCommonConfig.*;


public class WyMusicModel implements IMusicApiModel {
    private static final HashMap<String, String> qualityMap;
    //MusicTools的Cookie
    private static final String userCookie = "MUSIC_U=41d12a493b80de6fa49d16f6570d469a23f84bcf3394fedda977317d531e9c861d1b60489766205edfc3c0ec8cbcadd641049cea1c6bb9b6";
    //我的Cookie
    //private static final String userCookie="MUSIC_U=ba0a04e302da6a12399d1817fe4eae9dd7c6b7f4865bf4fd9b986b5c8c07893333a649814e309366;";
    //MusicDownloadUI的Cookie
    //private static final String userCookie="MUSIC_U=7cc06a93dda8951677088ae1cf97bd05986585c17ccea94bc1cbb455ef45f69533a649814e309366"

    static {
        qualityMap = new HashMap<>();
        qualityMap.put(QUALITY_ZQ, "1999000");
        qualityMap.put(QUALITY_SQ, "999000");
        qualityMap.put(QUALITY_HQ, "320000");
        qualityMap.put(QUALITY_PQ, "192000");
        qualityMap.put(QUALITY_LQ, "128000");
    }

    @Override
    public List<SongBean> searchMusic(String keyword, int num) {
        int page = 1;
        String searchLink = null;
        try {
            searchLink = "http://music.163.com/api/cloudsearch/get/web?s=" + URLEncoder.encode(keyword, "UTF-8") + "&type=1&offset=" + (page - 1) * num + "&limit=" + num + "&total=true";
        } catch (Exception e) {
            e.printStackTrace();
        }
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("X-Real-IP", IpHelper.getRandomIp());
        String response = HttpUtil.get(searchLink, headerMap);
        return parseSearchResult(response);
    }

    //目前仅仅支持网易普通格式
    @Override
    public SongBean searchDownloadLink(SongBean songBean, String quality) {
        String bitRate = qualityMap.get(quality);
        if (bitRate == null) {
            bitRate = "128000";
        }
        String downloadLink = getPlayUrl(songBean.getSongId(), bitRate);
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

    /**
     * @param response
     * @return
     */
    private List<SongBean> parseSearchResult(String response) {
        List<SongBean> songBeanList = new LinkedList<>();
        try {
            SearchResponse searchResponseBean = GsonUtil.fromJson(response, SearchResponse.class);
            if (searchResponseBean == null || searchResponseBean.result == null) {
                return songBeanList;
            }
            List<SongDetail> songsDetailList = searchResponseBean.result.getSongsList();
            for (SongDetail detail : songsDetailList) {
                SongBean song = new SongBean.Builder()
                        .songSrc(SRC_WY)
                        .songId(detail.getSongId() + "")
                        .songName(detail.getSongName())
                        .singersName(detail.getSingersName())
                        .singersId(detail.getSingersId())
                        .albumId(detail.getAlbum().getAlbumId() + "")
                        .albumName(detail.getAlbum().getAlbumName())
                        .picUrl(detail.getAlbum().albumPicUrl)
                        .quality(detail.getQuality())
                        .quality(detail.getQuality())
                        .mvUrl(detail.getMvId() + "")
                        .build();
                songBeanList.add(song);
            }
        } catch (Exception ignored) {

        }
        return songBeanList;
    }

    private String getPlayUrl(String id, String quality) {
        List<String> ids = new ArrayList<>();
        ids.add(id);
        return getPlayUrl(ids, quality);
    }

    private String getPlayUrl(List<String> ids, String quality) {
        String url = null;
        try {
            url = "http://music.163.com/api/song/enhance/player/url?ids=" + URLEncoder.encode(ids.toString(), "UTF-8") + "&br=" + quality;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Map<String, String> headerMap = new HashMap<>();
        String cookie = "MUSIC_U=" + MusicConfigManager.getInstance().getWyMusicToken();
        headerMap.put("X-Real-IP", IpHelper.getRandomIp());
        headerMap.put("Cookie", cookie.length() == 8 ? userCookie : cookie);
        String response = HttpUtil.get(url, headerMap);
        DownloadResponseBean responseBean = GsonUtil.fromJson(response, DownloadResponseBean.class);
        if (responseBean != null && responseBean.getData() != null) {
            DownloadResponseBean.Data data = responseBean.getData().get(0);
            if (data != null) {
                return data.getUrl();
            }
        }
        return null;
    }

    @Override
    public String searchMvUrl(String mvId) {
        //https://music.163.com/api/song/enhance/play/mv/url?id=303140&r=1080;
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("X-Real-IP", IpHelper.getRandomIp());
        String response = HttpUtil.get("https://music.163.com/api/song/enhance/play/mv/url?id=" + mvId + "&r=1080", headerMap);
        MvDownloadBean bean = GsonUtil.fromJson(response, MvDownloadBean.class);
        if (bean != null) {
            return bean.getData().getUrl();
        }
        return "";
    }

    @Override
    public String getLyric(String lyricId) {
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("X-Real-IP", IpHelper.getRandomIp());
        String baseUrl = "https://music.163.com/api/song/lyric?lv=0&kv=0&tv=0&id=" + lyricId;
        String response = HttpUtil.get(baseUrl, headerMap);
        try {
            LyricResponse bean = GsonUtil.fromJson(response, LyricResponse.class);
            if (bean != null && bean.getLrc().get("lyric") != null) {
                return bean.getLrc().get("lyric").toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public SongBean searchSongDetail(String songId) {
        String api = "https://music.163.com/api/song/detail?ids=%5b" + songId + "%5d";
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("X-Real-IP", IpHelper.getRandomIp());
        String response = HttpUtil.get(api, headerMap);
        try {
            MusicDetail songDetail = GsonUtil.fromJson(response, MusicDetail.class);
            SongBean songBean = new SongBean("WY", songId);
            if (songDetail.getSongs().size() > 0 && songDetail.getSongs().get(0).getArtists().size() > 0) {
                MusicDetail.Song song = songDetail.getSongs().get(0);
                List<Singer> artists = song.getArtists();
                Album album = song.getAlbum();
                songBean.setSongName(song.getName());
                if (album != null) {
                    String picUrl = album.getAlbumPicUrl().replace("http://", "https://");
                    songBean.setPicUrl(picUrl);
                    songBean.setAlbumId(album.getAlbumId() + "");
                    songBean.setAlbumName(album.getAlbumName());
                }
                List<String> singerNames = new ArrayList<>();
                List<String> singerIds = new ArrayList<>();
                for (Singer artist : artists) {
                    singerIds.add(artist.getSingerId()+ "");
                    singerNames.add(artist.getSingerName());
                }
                songBean.setSingersId(singerIds);
                songBean.setSingersName(singerNames);
                return songBean;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
