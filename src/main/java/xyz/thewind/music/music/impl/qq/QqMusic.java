package xyz.thewind.music.music.impl.qq;

import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.thewind.music.model.SongBean;
import xyz.thewind.music.music.IMusicApi;
import xyz.thewind.music.music.common.MusicConfigManager;
import xyz.thewind.music.music.impl.qq.bean.*;
import xyz.thewind.music.music.impl.qq.bean.login.QQMusicLoginToken;
import xyz.thewind.music.util.Base64Converter;
import xyz.thewind.music.util.GsonConverter;
import xyz.thewind.music.util.HttpRequestHelper;

import java.net.URLEncoder;
import java.util.*;

import static xyz.thewind.music.music.common.SongUtil.*;
import static xyz.thewind.music.util.HttpRequestHelper.downloadWebSiteUseGet;
import static xyz.thewind.music.util.HttpRequestHelper.downloadWebSiteUsePost;


public class QqMusic implements IMusicApi {
    public static final String baseDownloadServer = "https://isure.stream.qqmusic.qq.com/";
    private static final Logger logger = LogManager.getLogger(QqMusic.class);
    private static final HashMap<String, String> qualityMap;
    private static final Map<String, String> configMap = new HashMap<>();

    private static final String SPLIT_MID = "<>";

    static {
        qualityMap = new HashMap<>();
        qualityMap.put(QUALITY_ZQ, "RS01songid.flac");
        qualityMap.put(QUALITY_SQ, "F000songid.flac");
        qualityMap.put(QUALITY_HQ, "M800songid.mp3");
        qualityMap.put(QUALITY_PQ, "M500songid.mp3");
        qualityMap.put(QUALITY_LQ, "C400songid.m4a");
    }

    private static String downloadLinkRequestParamsBuilder(List<SongBean> downloadInfoList) {
        QQMusicLoginToken token = MusicConfigManager.getInstance().getQQMusicLoginToken();

        DownloadRequestBean requestBean = new DownloadRequestBean();
        if (token != null) {
            requestBean.comm.authst = token.musicKey;
            requestBean.comm.uin = token.musicId;
            requestBean.queryvkey.param.setUin(token.musicId);
        }

        List<String> filenames = new LinkedList<>();
        List<String> musicfiles = new LinkedList<>();
        List<String> songmid = new LinkedList<>();
        List<Integer> songtype = new LinkedList<>();
        for (SongBean info : downloadInfoList) {
            filenames.add(qualityMap.get(info.getQuality()).replace("songid", info.getSongId().split(SPLIT_MID)[1]));
            musicfiles.add(qualityMap.get(info.getQuality()).replace("songid", info.getSongId().split(SPLIT_MID)[0]));
            songmid.add(info.getSongId().split(SPLIT_MID)[0]);
            songtype.add(1);
        }
        requestBean.queryvkey.param.setFilename(filenames);
        requestBean.queryvkey.param.setMusicfile(musicfiles);
        requestBean.queryvkey.param.setSongmid(songmid);
        requestBean.queryvkey.param.setSongtype(songtype);

        return new Gson().toJson(requestBean);
    }

    public static String getDownloadLinkBySongId(String songid, String quality) {

        SongBean downloadInfo = new SongBean(SRC_QQ, songid, quality);
        List<SongBean> downloadInfoList = new LinkedList<>();
        downloadInfoList.add(downloadInfo);
        String requestParams = downloadLinkRequestParamsBuilder(downloadInfoList);
        String response = downloadRequest(requestParams);
        String downloadLink = "";
        DownloadBean downloadBean = GsonConverter.fromJson(response, DownloadBean.class);
        if (downloadBean != null && downloadBean.data != null && downloadBean.data.data.songDetailList.size() > 0) {
            List<DownloadBean.Data1.Data.SongDetail> songDetailList = downloadBean.data.data.songDetailList;
            DownloadBean.Data1.Data.SongDetail songDetail = songDetailList.get(0);
            downloadLink = songDetail.getDownloadLink();
        }
        return downloadLink;


    }

    public static List<SongBean> getDownloadLinkBySongIdList(List<SongBean> downloadInfoList) {
        String requestParams = downloadLinkRequestParamsBuilder(downloadInfoList);
        String response = downloadRequest(requestParams);
        DownloadBean downloadBean = GsonConverter.fromJson(response, DownloadBean.class);
        if (downloadBean != null && downloadBean.data != null && downloadBean.data.data.songDetailList.size() > 0) {
            List<DownloadBean.Data1.Data.SongDetail> songDetailList = downloadBean.data.data.songDetailList;
            for (int i = 0; i < songDetailList.size(); i++) {
                DownloadBean.Data1.Data.SongDetail detail = songDetailList.get(i);
                downloadInfoList.get(i).getDownloadLinkMap().put(downloadInfoList.get(i).getQuality(), detail.getDownloadLink());
            }
        }
        return downloadInfoList;


    }

    private static String downloadRequest(String params) {
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("User-Agent", "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0)");
        headerMap.put("Host", "u.y.qq.com");
        headerMap.put("Connection", "Keep-Alive");
        headerMap.put("Accept", "application/json");
        return downloadWebSiteUsePost("https://u.y.qq.com/cgi-bin/musicu.fcg", params, headerMap);
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
        String response = qqSearchRequest(keyword, num);
        return qqSongSearchProcess(response);
    }

    @Override
    public SongBean searchDownloadLink(SongBean songBean, String quality) {
        String downloadLink = getDownloadLinkBySongId(songBean.getSongId(), quality);
        //String downloadLink= RequestModule.getPlayUrl(songBean.getSongId(),quality);
        songBean.getDownloadLinkMap().put(quality, downloadLink);
        return songBean;
    }

    @Override
    public List<SongBean> searchDownloadLink(List<SongBean> downloadInfoList) {
        //List<DownloadInfo> downloadInfoList
        return getDownloadLinkBySongIdList(downloadInfoList);
    }

    @Override
    public List<SongBean> searchWithDownload(String keyword, int num) {
        List<SongBean> songBeanList = searchMusic(keyword, num);
        for (SongBean bean : songBeanList) {
            if (bean.getQuality().equals(QUALITY_ZQ)) {
                searchDownloadLink(bean, QUALITY_ZQ);
                searchDownloadLink(bean, QUALITY_SQ);
                searchDownloadLink(bean, QUALITY_HQ);
            }
            if (bean.getQuality().equals(QUALITY_SQ)) {
                searchDownloadLink(bean, QUALITY_SQ);
                searchDownloadLink(bean, QUALITY_HQ);
            }
            if (bean.getQuality().equals(QUALITY_HQ)) {
                searchDownloadLink(bean, QUALITY_HQ);
            }
            searchDownloadLink(bean, QUALITY_LQ);
            searchDownloadLink(bean, QUALITY_PQ);

        }
        return songBeanList;
    }

    private String qqSearchRequest(String keyword, int num) {
        String response = "";
        try {
            String link = "https://u.y.qq.com/cgi-bin/musicu.fcg?data=" + URLEncoder.encode("{\"comm\":{\"ct\":\"19\",\"cv\":\"1873\",\"wid\":\"0\"},\"data\":{\"method\":\"DoSearchForQQMusicDesktop\",\"module\":\"music.search.SearchCgiService\",\"param\":{\"grp\":1,\"num_per_page\":" + num + ",\"page_num\":1,\"query\":\"" + keyword + "\",\"search_type\":0}}}");
            Map<String, String> headerMap = new HashMap<>();
            headerMap.put("User-Agent", USER_AGENT_ANDROID);
            response = downloadWebSiteUseGet(link, headerMap);
        } catch (Exception e) {
            logger.error("encode failed");
        }
        return response;

    }

    /**
     * this.songSrc = songSrc;
     * this.songId = songId;
     * this.songName = songName;
     * this.singersName = singersName;
     * this.albumName = albumName;
     * this.quality=quality;
     * this.picUrl = picUrl;
     * this.lrcUrl = lrcUrl;
     * this.mvUrl = mvUrl;
     *
     * @param response
     * @return
     */

    private List<SongBean> qqSongSearchProcess(String response) {
        List<SongBean> list = new LinkedList<>();
        if (response.length() == 0) {
            return list;
        }
        try {
            SearchResponse searchResponseBean = GsonConverter.fromJson(response, SearchResponse.class);
            if (searchResponseBean.getCode() == 0) {
                List<SearchResponse.SongItem> songDetailList = searchResponseBean.data.data.body.song.list;
                for (SearchResponse.SongItem songItem : songDetailList) {
                    String picUrl = "";
                    if (songItem.album != null) {
                        picUrl = "https://y.gtimg.cn/music/photo_new/T002R800x800M000" + songItem.album.mid + ".jpg";
                    }
                    String quality = songItem.file.getZqSize() > 0 ? QUALITY_ZQ : songItem.file.getSqSize() > 0 ? QUALITY_SQ : QUALITY_HQ;
                    SongBean songBean = new SongBean(SRC_QQ,
                            songItem.mid + SPLIT_MID + songItem.file.mediaMid,
                            songItem.name,
                            parseSingersId(songItem.singer),
                            parseSingers(songItem.singer),
                            songItem.album.mid,
                            songItem.album.name,
                            quality,
                            picUrl,
                            "",
                            songItem.mv.vid);
                    list.add(songBean);
                }
            }
        } catch (Exception ignored) {

        }
        return list;
    }

    private List<String> parseSingers(List<SearchResponse.Singer> singers) {
        List<String> singerList = new LinkedList<>();
        for (SearchResponse.Singer singer : singers) {
            singerList.add(singer.name);
        }
        return singerList;
    }

    private List<String> parseSingersId(List<SearchResponse.Singer> singers) {
        List<String> singerIdList = new LinkedList<>();
        for (SearchResponse.Singer singer : singers) {
            singerIdList.add(singer.mid);
        }
        return singerIdList;
    }

    @Override
    public String getLyric(String lyricId) {
        String baseLink = "https://c.y.qq.com/lyric/fcgi-bin/fcg_query_lyric_new.fcg?g_tk=5381&format=json&songmid=" + lyricId.split(SPLIT_MID)[0];
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Referer", "https://y.qq.com/portal/player.html");
        String encodeLyric = HttpRequestHelper.downloadWebSiteUseGet(baseLink, headerMap);
        try {
            LyricResponseBean bean = GsonConverter.fromJson(encodeLyric, LyricResponseBean.class);
            return Base64Converter.decode(bean.getLyric());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public SongBean searchSongDetail(String songId) {
        String data = "{\"songinfo\":{\"module\":\"music.pf_song_detail_svr\",\"method\":\"get_song_detail\",\"param\":{\"song_mid\":\"" + songId.split(SPLIT_MID)[0] + "\"}}}";
        try {
            String encode = URLEncoder.encode(data, "UTF-8");
            String api = "https://u.y.qq.com/cgi-bin/musicu.fcg";
            String response = HttpRequestHelper.downloadWebSiteUseGet(api + "?data=" + encode, null);
            SongDetail detail = GsonConverter.fromJson(response, SongDetail.class);
            SongDetail.Songinfo.Data.TrackInfo trackInfo = detail.songinfo.data.trackInfo;
            SongBean songBean = new SongBean("QQ", songId);
            songBean.setSongId(trackInfo.getMid());
            songBean.setSongName(trackInfo.getName());
            List<SongDetail.Songinfo.Data.TrackInfo.Singer> singerList = trackInfo.getSinger();
            List<String> singersName = new ArrayList<>();
            List<String> singersId = new ArrayList<>();
            for (SongDetail.Songinfo.Data.TrackInfo.Singer singer : singerList) {
                singersName.add(singer.name);
                singersId.add(singer.mid);
            }
            songBean.setSingersName(singersName);
            songBean.setSingersId(singersId);
            SongDetail.Songinfo.Data.TrackInfo.Album album = trackInfo.getAlbum();
            songBean.setAlbumId(album.getMid());
            songBean.setAlbumName(album.getName());
            if (album.getMid() != null && album.getMid().length() > 0) {
                songBean.setPicUrl("https://y.gtimg.cn/music/photo_new/T002R800x800M000" + album.getMid() + ".jpg");
            }
            List<SongDetail.Songinfo.Data.Info> infos = detail.songinfo.data.info;
            for (SongDetail.Songinfo.Data.Info info : infos) {
                if (info.type.equals("lyric")) {
                    if (info.content.size() > 0) {
                        songBean.setLrcUrl(info.content.get(0).value);
                    }

                }
            }
            songBean.setMvUrl(trackInfo.getMv().vid);
            return songBean;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
