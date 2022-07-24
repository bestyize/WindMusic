package xyz.thewind.music.music.impl.mg;

import com.google.gson.Gson;
import xyz.thewind.music.model.SongBean;
import xyz.thewind.music.music.IMusicApiModel;
import xyz.thewind.music.music.impl.mg.bean.LyricResponseBean;
import xyz.thewind.music.music.impl.mg.bean.MgDownloadBean;
import xyz.thewind.music.music.impl.mg.bean.search.MgSearchResp;
import xyz.thewind.music.music.impl.mg.bean.search.SongDetail;
import xyz.thewind.music.util.GsonConverter;
import xyz.thewind.music.util.HttpRequestHelper;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

import static xyz.thewind.music.music.common.MusicCommonConfig.*;
import static xyz.thewind.music.util.HttpRequestHelper.downloadWebSiteUseGet;


public class MgMusicModel implements IMusicApiModel {
    //或者：https://app.c.nf.migu.cn/MIGUM3.0/v2.0/content/querySongBySongId.do?songId=1136379763
    private static final String baseContentLink = "https://c.musicapp.migu.cn/MIGUM3.0/v1.0/content/resourceinfo.do?ua=Android_migu&version=5.0&resourceType=2&copyrightId=";

    @Override
    public List<SongBean> searchMusic(String keyword, int num) {
        return parseSearchResult(doSearchMusic(keyword, num));
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

    public static void main(String[] args) {
        MgMusicModel mgMusic = new MgMusicModel();
        System.out.println(GsonConverter.toJson(mgMusic.searchMusic("周杰伦", 1)));
    }

    private String doSearchMusic(String keyword, int num) {
        String key = null;
        try {
            key = URLEncoder.encode(keyword, "utf-8");
        } catch (UnsupportedEncodingException e) {
            return "";
        }
        String api = "https://app.c.nf.migu.cn/MIGUM3.0/v1.0/content/search_all.do?isCopyright=1&isCorrect=1&pageNo=1&pageSize=" + num + "&sort=0&text=" + key + "&searchSwitch={\"song\":1}";
        return downloadWebSiteUseGet(api, null);
    }

    private List<SongBean> parseSearchResult(String response) {
        List<SongBean> songBeanList = new LinkedList<>();
        MgSearchResp resp = GsonConverter.fromJson(response, MgSearchResp.class);
        if (resp == null) {
            return songBeanList;
        }
        for (List<SongDetail> details : resp.songResultData.resultList) {
            SongDetail detail = details.get(0);
            SongBean songBean = new SongBean(SRC_MG, detail.getSongId(), detail.getSongName(), detail.getSingersId(),
                    detail.getSingersName(), detail.getAlbumId(), detail.getAlbumName(), detail.getQuality(),
                    detail.getPicUrl(), detail.getLyricUrl(), detail.getMvId());
            songBeanList.add(songBean);
        }
        return songBeanList;
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




    @Override
    public SongBean searchSongDetail(String songId) {
        return getDownloadLinkById(songId, "");
    }

    private SongBean getDownloadLinkById(String songid, String quality) {
        SongBean songBean = new SongBean("MG", songid);
        String link = baseContentLink + songid;
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("User-Agent", USER_AGENT_ANDROID);
        headerMap.put("Host", "m.music.migu.cn");
        String response = downloadWebSiteUseGet(link, headerMap);
        MgDownloadBean mgDownloadBean = new Gson().fromJson(response, MgDownloadBean.class);
        if (mgDownloadBean == null || mgDownloadBean.getResource() == null || mgDownloadBean.getResource().size() == 0) {
            return songBean;
        }
        MgDownloadBean.MgMusicDetail musicDetail = mgDownloadBean.getResource().get(0);
        List<MgDownloadBean.MgMusicDetail.RateFormats> rateFormatList = musicDetail.getRateFormats();
        songBean.setLrcUrl(mgDownloadBean.getResource().get(0).getLrcUrl());
        Map<String, String> map = songBean.getDownloadLinkMap();
        if (rateFormatList != null) {
            for (MgDownloadBean.MgMusicDetail.RateFormats formats : rateFormatList) {
                map.put(formats.getFormatType(), formats.getUrl());
            }
        }
        if (musicDetail.getEqFormat() != null) {
            map.put(QUALITY_EQ, musicDetail.getEqFormat().getUrl());
        }
        songBean.setSongName(musicDetail.getSongName());
        songBean.setAlbumName(musicDetail.getAlbumName());
        songBean.setSingersName(Collections.singletonList(musicDetail.getSingerName()));
        songBean.setSingersId(Collections.singletonList(musicDetail.getSingerId()));
        if (musicDetail.getAlbumImgs().size() > 0) {
            songBean.setPicUrl(musicDetail.getAlbumImgs().get(0).getImg());
        }
        return songBean;
    }


}
