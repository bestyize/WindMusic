package xyz.thewind.music.music.impl.qq;

import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.thewind.music.model.SongBean;
import xyz.thewind.music.music.IMusicApiModel;
import xyz.thewind.music.music.common.MusicConfigManager;
import xyz.thewind.music.music.impl.qq.bean.download.DownloadRequestBean;
import xyz.thewind.music.music.impl.qq.bean.LyricResponseBean;
import xyz.thewind.music.music.impl.qq.bean.MusicDetail;
import xyz.thewind.music.music.impl.qq.bean.download.DownloadDetail;
import xyz.thewind.music.music.impl.qq.bean.download.DownloadResponse;
import xyz.thewind.music.music.impl.qq.bean.login.QQMusicLoginToken;
import xyz.thewind.music.music.impl.qq.bean.search.Album;
import xyz.thewind.music.music.impl.qq.bean.search.SearchResponse;
import xyz.thewind.music.music.impl.qq.bean.search.Singer;
import xyz.thewind.music.music.impl.qq.bean.search.SongDetail;
import xyz.thewind.music.util.Base64Util;
import xyz.thewind.music.util.GsonUtil;
import xyz.thewind.music.util.HttpUtil;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static xyz.thewind.music.music.common.MusicCommonConfig.*;
import static xyz.thewind.music.util.HttpUtil.get;
import static xyz.thewind.music.util.HttpUtil.post;


public class QqMusicModel implements IMusicApiModel {
    public static final String baseDownloadServer = "https://isure.stream.qqmusic.qq.com/";
    private static final Logger logger = LogManager.getLogger(QqMusicModel.class);
    private static final HashMap<String, String> qualityMap;

    private static final String SPLIT_MID = "<>";

    static {
        qualityMap = new HashMap<>();
        qualityMap.put(QUALITY_ZQ, "RS01songid.flac");
        qualityMap.put(QUALITY_SQ, "F000songid.flac");
        qualityMap.put(QUALITY_HQ, "M800songid.mp3");
        qualityMap.put(QUALITY_PQ, "M500songid.mp3");
        qualityMap.put(QUALITY_LQ, "C400songid.m4a");
    }

    @Override
    public List<SongBean> searchMusic(String keyword, int num) {
        String response = doSearch(keyword, num);
        return parseSearchResult(response);
    }

    @Override
    public SongBean searchDownloadLink(SongBean songBean, String quality) {
        String downloadLink = getDownloadLinkBySongId(songBean.getSongId(), quality);
        songBean.getDownloadLinkMap().put(quality, downloadLink);
        return songBean;
    }

    @Override
    public List<SongBean> searchDownloadLink(List<SongBean> downloadInfoList) {
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

    private String doSearch(String keyword, int num) {
        String response = "";
        try {
            String link = "https://u.y.qq.com/cgi-bin/musicu.fcg?data=" + URLEncoder.encode("{\"comm\":{\"ct\":\"19\",\"cv\":\"1873\",\"wid\":\"0\"},\"data\":{\"method\":\"DoSearchForQQMusicDesktop\",\"module\":\"music.search.SearchCgiService\",\"param\":{\"grp\":1,\"num_per_page\":" + num + ",\"page_num\":1,\"query\":\"" + keyword + "\",\"search_type\":0}}}");
            Map<String, String> headerMap = new HashMap<>();
            headerMap.put("User-Agent", USER_AGENT_ANDROID);
            response = get(link, headerMap);
        } catch (Exception e) {
            logger.error("encode failed");
        }
        return response;
    }

    /**
     * @param response
     * @return
     */

    private List<SongBean> parseSearchResult(String response) {
        List<SongBean> list = new LinkedList<>();
        if (response.length() == 0) {
            return list;
        }
        SearchResponse searchResponse = null;
        try {
            searchResponse = GsonUtil.fromJson(response, SearchResponse.class);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        if (searchResponse == null || searchResponse.getCode() != 0) {
            return list;
        }
        List<SongDetail> songDetailList = searchResponse.data.data.body.songList.list;
        for (SongDetail detail : songDetailList) {
            SongBean song = new SongBean.Builder()
                    .songSrc(SRC_QQ)
                    .songId(detail.getMid() + SPLIT_MID + detail.getFile().getMediaMid())
                    .songName(detail.getName())
                    .singersName(detail.getSingersName())
                    .singersId(detail.getSingersId())
                    .albumId(detail.getAlbum().getMid())
                    .albumName(detail.getAlbum().getName())
                    .quality(detail.getQuality())
                    .picUrl(detail.getPicUrl())
                    .mvUrl(detail.getMv().getVid())
                    .build();
            list.add(song);
        }
        return list;
    }


    @Override
    public String getLyric(String lyricId) {
        String baseLink = "https://c.y.qq.com/lyric/fcgi-bin/fcg_query_lyric_new.fcg?g_tk=5381&format=json&songmid=" + lyricId.split(SPLIT_MID)[0];
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Referer", "https://y.qq.com/portal/player.html");
        String encodeLyric = HttpUtil.get(baseLink, headerMap);
        try {
            LyricResponseBean bean = GsonUtil.fromJson(encodeLyric, LyricResponseBean.class);
            return Base64Util.decode(bean.getLyric());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public SongBean searchSongDetail(String songId) {
        String data = "{\"songinfo\":{\"module\":\"music.pf_song_detail_svr\",\"method\":\"get_song_detail\",\"param\":{\"song_mid\":\"" + songId.split(SPLIT_MID)[0] + "\"}}}";
        String encode;
        encode = URLEncoder.encode(data, StandardCharsets.UTF_8);
        String api = "https://u.y.qq.com/cgi-bin/musicu.fcg";
        String response = HttpUtil.get(api + "?data=" + encode, null);
        MusicDetail detail = GsonUtil.fromJson(response, MusicDetail.class);
        SongDetail trackInfo = detail.songinfo.data.getSongDetail();
        SongBean songBean = new SongBean("QQ", songId);
        songBean.setSongId(trackInfo.getMid());
        songBean.setSongName(trackInfo.getName());
        List<Singer> singerList = trackInfo.getSingers();
        List<String> singersName = new ArrayList<>();
        List<String> singersId = new ArrayList<>();
        for (Singer singer : singerList) {
            singersName.add(singer.name);
            singersId.add(singer.mid);
        }
        songBean.setSingersName(singersName);
        songBean.setSingersId(singersId);
        Album album = trackInfo.getAlbum();
        songBean.setAlbumId(album.getMid());
        songBean.setAlbumName(album.getName());
        if (album.getMid() != null && album.getMid().length() > 0) {
            songBean.setPicUrl("https://y.gtimg.cn/music/photo_new/T002R800x800M000" + album.getMid() + ".jpg");
        }
        List<MusicDetail.Info> infos = detail.songinfo.data.info;
        for (MusicDetail.Info info : infos) {
            if (!info.type.equals("lyric")) {
                continue;
            }
            if (info.content.size() > 0) {
                songBean.setLrcUrl(info.content.get(0).value);
            }
        }
        songBean.setMvUrl(trackInfo.getMv().vid);
        return songBean;
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
        DownloadResponse downloadResponse = GsonUtil.fromJson(response, DownloadResponse.class);
        if (downloadResponse != null && downloadResponse.data.dataInfo.songDetailList.size() > 0) {
            List<DownloadDetail> songDetailList = downloadResponse.data.dataInfo.songDetailList;
            DownloadDetail songDetail = songDetailList.get(0);
            downloadLink = songDetail.getDownloadLink();
        }
        return downloadLink;
    }

    public static List<SongBean> getDownloadLinkBySongIdList(List<SongBean> downloadInfoList) {
        String requestParams = downloadLinkRequestParamsBuilder(downloadInfoList);
        String response = downloadRequest(requestParams);
        DownloadResponse downloadBean = GsonUtil.fromJson(response, DownloadResponse.class);
        if (downloadBean != null && downloadBean.data != null && downloadBean.data.dataInfo.songDetailList.size() > 0) {
            List<DownloadDetail> songDetailList = downloadBean.data.dataInfo.songDetailList;
            for (int i = 0; i < songDetailList.size(); i++) {
                DownloadDetail detail = songDetailList.get(i);
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
        return post("https://u.y.qq.com/cgi-bin/musicu.fcg", params, headerMap);
    }

}
