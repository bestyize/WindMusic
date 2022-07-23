package xyz.thewind.music.music.impl.mg;

import com.google.gson.Gson;
import xyz.thewind.music.model.SongBean;
import xyz.thewind.music.music.MusicHelper;
import xyz.thewind.music.music.impl.mg.bean.LyricResponseBean;
import xyz.thewind.music.music.impl.mg.bean.MgDownloadBean;
import xyz.thewind.music.music.impl.mg.bean.SearchResponseBean;
import xyz.thewind.music.music.impl.mg.bean.SongDetail;
import xyz.thewind.music.util.GsonConverter;
import xyz.thewind.music.util.HttpRequestHelper;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

import static xyz.thewind.music.music.common.SongUtil.*;
import static xyz.thewind.music.util.HttpRequestHelper.downloadWebSiteUseGet;


public class MgMusic implements MusicHelper {
    //或者：https://app.c.nf.migu.cn/MIGUM3.0/v2.0/content/querySongBySongId.do?songId=1136379763
    private static final String baseContentLink = "https://c.musicapp.migu.cn/MIGUM3.0/v1.0/content/resourceinfo.do?ua=Android_migu&version=5.0&resourceType=2&copyrightId=";

    @Override
    public List<SongBean> searchMusic(String keyword, int num) {
        String response = mgHttpSearchRequest(keyword, num);
        return mgSongSearchRespProcess(response);
    }

    @Override
    public SongBean searchDownloadLink(SongBean songBean, String quality) {
        Map<String, String> downloadLinkMap = getDownloadLinkById(songBean.getSongId(), quality).getDownloadLinkMap();
        String downloadLink = downloadLinkMap.get(quality);
        if (downloadLink == null) {
            return songBean;
        }
        songBean.setDownloadLinkMap(downloadLinkMap);
        return songBean;
    }

    @Override
    public List<SongBean> searchWithDownload(String keyword, int num) {
        List<SongBean> songBeanList = searchMusic(keyword, num);
        for (SongBean bean : songBeanList) {
            searchDownloadLink(bean, "HQ");
        }
        return songBeanList;
    }

    //或者https://app.c.nf.migu.cn/MIGUM3.0/v1.0/content/search_all.do?isCopyright=1&isCorrect=1&pageNo=1&pageSize=20&sort=0&text=十年&searchSwitch={"song":1,"album":0,"singer":0,"tagSong":1,"mvSong":0,"songlist":0,"bestShow":1,"lyricSong":0}
    private String mgHttpSearchRequest(String keyword, int num) {
        String response = "";
        try {
            String key = URLEncoder.encode(keyword, "utf-8");
            String link = "https://m.music.migu.cn/migu/remoting/scr_search_tag?rows=" + num + "&type=2&keyword=" + key + "&pgc=1";
            Map<String, String> headerMap = new HashMap<>();
            headerMap.put("Referer", "https://m.music.migu.cn/v3/search?keyword=" + key);
            response = downloadWebSiteUseGet(link, headerMap);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
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
    private List<SongBean> mgSongSearchRespProcess(String response) {
        List<SongBean> list = new LinkedList<>();
        if (response.length() == 0) {
            return list;
        }
        try {
            SearchResponseBean mgSongRespBean = GsonConverter.fromJson(response, SearchResponseBean.class);
            if (mgSongRespBean != null) {
                List<SongDetail> detailList = mgSongRespBean.getSongDetailList();
                if (detailList != null) {
                    for (SongDetail detail : detailList) {
                        SongBean songBean = new SongBean(SRC_MG,
                                detail.getSongId(),
                                detail.getSongName(),
                                parseSingersId(detail.getSingerId()),
                                parseSingers(detail.getSingerName()),
                                detail.getAlbumId(),
                                detail.getAlbumName(),
                                detail.getQuality(),
                                detail.getPicUrl(),
                                detail.getLyricUrl(),
                                detail.getMvId());
                        list.add(songBean);

                    }
                }
            }
        } catch (Exception ignored) {

        }

        return list;
    }

    @Override
    public String getLyric(String lyricId) {
        String url = "https://m.music.migu.cn/migu/remoting/cms_detail_tag?cpid=" + lyricId;
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Referer", "https://m.music.migu.cn/v3/music/song/" + lyricId);
        String response = HttpRequestHelper.downloadWebSiteUseGet(url, headerMap);
        try {
            LyricResponseBean bean = GsonConverter.fromJson(response, LyricResponseBean.class);
            if (bean.getData().getLyricLrc() != null) {
                return bean.getData().getLyricLrc();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private List<String> parseSingers(String singers) {
        String[] singerArr = singers.split(",");
        return new LinkedList<>(Arrays.asList(singerArr));
    }

    private List<String> parseSingersId(String singersId) {
        String[] singerIdArr = singersId.split(",");
        return new LinkedList<>(Arrays.asList(singerIdArr));//kw音乐只返回第一个歌手id
    }

    @Override
    public SongBean searchSongDetail(String songId) {
        return getDownloadLinkById(songId, "");
    }

    private SongBean getDownloadLinkById(String songid, String quality) {
        SongBean songBean = new SongBean("MG", songid);
        try {
            String link = baseContentLink + songid;
            Map<String, String> headerMap = new HashMap<>();
            headerMap.put("User-Agent", USER_AGENT_ANDROID);
            headerMap.put("Host", "m.music.migu.cn");
            String response = downloadWebSiteUseGet(link, headerMap);
            MgDownloadBean mgDownloadBean = new Gson().fromJson(response, MgDownloadBean.class);

            if (mgDownloadBean != null && mgDownloadBean.getResource() != null && mgDownloadBean.getResource().size() > 0) {
                MgDownloadBean.MgMusicDetail musicDetail = mgDownloadBean.getResource().get(0);
                List<MgDownloadBean.MgMusicDetail.RateFormats> rateFormatList = musicDetail.getRateFormats();
                songBean.setLrcUrl(mgDownloadBean.getResource().get(0).getLrcUrl());
                if (rateFormatList != null) {
                    Map<String, String> map = songBean.getDownloadLinkMap();
                    for (MgDownloadBean.MgMusicDetail.RateFormats formats : rateFormatList) {
                        map.put(formats.getFormatType(), formats.getUrl());

                    }
                    if (musicDetail.getEqFormat() != null) {
                        map.put(QUALITY_EQ, musicDetail.getEqFormat().getUrl());
                    }
                }
                songBean.setSongName(musicDetail.getSongName());
                songBean.setAlbumName(musicDetail.getAlbumName());
                songBean.setSingersName(Collections.singletonList(musicDetail.getSingerName()));
                songBean.setSingersId(Collections.singletonList(musicDetail.getSingerId()));
                if (musicDetail.getAlbumImgs().size() > 0) {
                    songBean.setPicUrl(musicDetail.getAlbumImgs().get(0).getImg());
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return songBean;
    }


}
