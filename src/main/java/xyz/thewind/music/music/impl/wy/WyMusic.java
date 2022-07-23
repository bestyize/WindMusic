package xyz.thewind.music.music.impl.wy;

import xyz.thewind.music.model.SongBean;
import xyz.thewind.music.music.IMusicApi;
import xyz.thewind.music.music.impl.wy.bean.*;
import xyz.thewind.music.util.GsonConverter;
import xyz.thewind.music.util.HttpRequestHelper;
import xyz.thewind.music.util.IpHelper;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

import static xyz.thewind.music.music.common.SongUtil.*;


public class WyMusic implements IMusicApi {
    private static final HashMap<String, String> qualityMap;
    //MusicTools的Cookie
    private static final String userCookie = "MUSIC_U=41d12a493b80de6fa49d16f6570d469a23f84bcf3394fedda977317d531e9c861d1b60489766205edfc3c0ec8cbcadd641049cea1c6bb9b6";
    //我的Cookie
    //private static final String userCookie="MUSIC_U=ba0a04e302da6a12399d1817fe4eae9dd7c6b7f4865bf4fd9b986b5c8c07893333a649814e309366;";
    //MusicDownloadUI的Cookie
    //private static final String userCookie="MUSIC_U=7cc06a93dda8951677088ae1cf97bd05986585c17ccea94bc1cbb455ef45f69533a649814e309366";
    private static final Map<String, String> configMap = new HashMap<>();

    static {
        qualityMap = new HashMap<>();
        qualityMap.put(QUALITY_ZQ, "1999000");
        qualityMap.put(QUALITY_SQ, "999000");
        qualityMap.put(QUALITY_HQ, "320000");
        qualityMap.put(QUALITY_PQ, "192000");
        qualityMap.put(QUALITY_LQ, "128000");
    }

    @Override
    public void setConfigMap(Map<String, String> configMaps) {
        if (configMaps != null) {
            configMap.clear();
            configMap.putAll(configMaps);
        }
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
        String response = HttpRequestHelper.downloadWebSiteUseGet(searchLink, headerMap);
        return wySongSearchRespProcess(response);
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
    private List<SongBean> wySongSearchRespProcess(String response) {
        List<SongBean> songBeanList = new LinkedList<>();
        try {
            SearchResponseBean searchResponseBean = GsonConverter.fromJson(response, SearchResponseBean.class);
            if (searchResponseBean != null && searchResponseBean.result != null) {
                List<SearchResponseBean.Data.SongDetail> songsDetailList = searchResponseBean.result.getSongsList();
                for (SearchResponseBean.Data.SongDetail detail : songsDetailList) {
                    SongBean songBean = new SongBean(SRC_WY,
                            String.valueOf(detail.getSongId()),
                            detail.getSongName(),
                            parseSingersId(detail.getAr()),
                            parseSingers(detail.getAr()),
                            String.valueOf(detail.getAl().getAlbumId()),
                            detail.getAl().getAlbumName(),
                            getMaxQuality(detail),
                            detail.getAl().getAlbumPicUrl(),
                            "",
                            String.valueOf(detail.getMvId())
                    );
                    songBeanList.add(songBean);
                }
            }
        } catch (Exception ignored) {

        }
        return songBeanList;
    }

    private String getMaxQuality(SearchResponseBean.Data.SongDetail detail) {
        if (detail.getPrivilege().maxbr < 999000) {
            return QUALITY_HQ;
        }
        if (detail.getZqQuality() != null) {
            return QUALITY_ZQ;
        }
        return QUALITY_SQ;
    }

    private List<String> parseSingers(List<SearchResponseBean.Data.SongDetail.Singer> singers) {
        List<String> singerList = new LinkedList<>();
        for (SearchResponseBean.Data.SongDetail.Singer singer : singers) {
            singerList.add(singer.getSingerName());
        }
        return singerList;
    }

    private List<String> parseSingersId(List<SearchResponseBean.Data.SongDetail.Singer> singers) {
        List<String> singerIdList = new LinkedList<>();
        for (SearchResponseBean.Data.SongDetail.Singer singer : singers) {
            singerIdList.add(singer.getSingerId() + "");
        }
        return singerIdList;
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
        String cookie = "MUSIC_U=" + configMap.get("wy_music_u");
        headerMap.put("X-Real-IP", IpHelper.getRandomIp());
        headerMap.put("Cookie", cookie.length() == 8 ? userCookie : cookie);
        String response = HttpRequestHelper.downloadWebSiteUseGet(url, headerMap);
        DownloadResponseBean responseBean = GsonConverter.fromJson(response, DownloadResponseBean.class);
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
        String response = HttpRequestHelper.downloadWebSiteUseGet("https://music.163.com/api/song/enhance/play/mv/url?id=" + mvId + "&r=1080", headerMap);
        MvDownloadBean bean = GsonConverter.fromJson(response, MvDownloadBean.class);
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
        String response = HttpRequestHelper.downloadWebSiteUseGet(baseUrl, headerMap);
        try {
            LyricResponse bean = GsonConverter.fromJson(response, LyricResponse.class);
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
        String response = HttpRequestHelper.downloadWebSiteUseGet(api, headerMap);
        try {
            SongDetail songDetail = GsonConverter.fromJson(response, SongDetail.class);
            SongBean songBean = new SongBean("WY", songId);
            if (songDetail.getSongs().size() > 0 && songDetail.getSongs().get(0).getArtists().size() > 0) {
                SongDetail.Song song = songDetail.getSongs().get(0);
                List<SongDetail.Song.Artist> artists = song.getArtists();
                SongDetail.Song.Album album = song.getAlbum();
                songBean.setSongName(song.getName());
                if (album != null) {
                    String picUrl = album.getPicUrl().replace("http://", "https://");
                    songBean.setPicUrl(picUrl);
                    songBean.setAlbumId(album.getId() + "");
                    songBean.setAlbumName(album.getName());
                }
                List<String> singerNames = new ArrayList<>();
                List<String> singerIds = new ArrayList<>();
                for (SongDetail.Song.Artist artist : artists) {
                    singerIds.add(artist.getId() + "");
                    singerNames.add(artist.getName());
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
