package xyz.thewind.music.music.impl;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import xyz.thewind.music.model.SongBean;
import xyz.thewind.music.music.MusicHelper;
import xyz.thewind.music.util.GsonConverter;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

import static xyz.thewind.music.util.HttpRequestHelper.downloadWebSiteUseGet;
import static xyz.thewind.music.util.SongUtil.*;


public class MgMusic implements MusicHelper {
    private static final String baseContentLink = "https://c.musicapp.migu.cn/MIGUM2.0/v1.0/content/resourceinfo.do?ua=Android_migu&version=5.0&resourceType=2&copyrightId=";

    @Override
    public List<SongBean> searchMusic(String keyword, int num) {
        String response=mgHttpSearchRequest(keyword,num);
        return mgSongSearchRespProcess(response);
    }

    @Override
    public SongBean searchDownloadLink(SongBean songBean,String quality) {
        Map<String,String> downloadLinkMap=getDownloadLinkById(songBean.getSongId(), quality).getDownloadLinkMap();
        String downloadLink =downloadLinkMap .get(quality);
        if(downloadLink==null){
            return songBean;
        }
        songBean.setDownloadLinkMap(downloadLinkMap);
        return songBean;
    }

    @Override
    public List<SongBean> searchWithDownload(String keyword, int num) {
        List<SongBean> songBeanList=searchMusic(keyword,num);
        for (SongBean bean:songBeanList){
            searchDownloadLink(bean,"HQ");
        }
        return songBeanList;
    }

    private String mgHttpSearchRequest(String keyword, int num){
        String response="";
        try {
            String link = "https://m.music.migu.cn/migu/remoting/scr_search_tag?rows=" + num + "&type=2&keyword=" + URLEncoder.encode(keyword, "utf-8") + "&pgc=1";
            Map<String,String> headerMap=new HashMap<>();
            response = downloadWebSiteUseGet(link,headerMap);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return response;

    }
    /**
     *         this.songSrc = songSrc;
     *         this.songId = songId;
     *         this.songName = songName;
     *         this.singersName = singersName;
     *         this.albumName = albumName;
     *         this.quality=quality;
     *         this.picUrl = picUrl;
     *         this.lrcUrl = lrcUrl;
     *         this.mvUrl = mvUrl;
     * @param response
     * @return
     */
    private List<SongBean> mgSongSearchRespProcess(String response){
        List<SongBean> list=new LinkedList<>();
        if(response.length()==0){
            return list;
        }
        try {
            SearchResponseBean mgSongRespBean= GsonConverter.fromJson(response, SearchResponseBean.class);
            if(mgSongRespBean!=null){
                List<SearchResponseBean.SongDetail> detailList=mgSongRespBean.songDetailList;
                if(detailList!=null){
                    for (SearchResponseBean.SongDetail detail:detailList){
                        SongBean songBean=new SongBean(SRC_MG,
                                detail.songId,
                                detail.songName,
                                parseSingersId(detail.getSingerId()),
                                parseSingers(detail.singerName),
                                detail.getAlbumId(),
                                detail.albumName,
                                detail.quality==null?null:detail.quality.equals("1")?QUALITY_SQ:QUALITY_HQ,
                                detail.picUrl,
                                detail.lyricUrl,
                                detail.mvId);
                        list.add(songBean);

                    }
                }
            }
        }catch (Exception ignored){

        }

        return list;
    }

    private List<String> parseSingers(String singers){
        String[] singerArr=singers.split(",");
        return new LinkedList<>(Arrays.asList(singerArr));
    }

    private List<String> parseSingersId(String singersId){
        String[] singerIdArr=singersId.split(",");
        return new LinkedList<>(Arrays.asList(singerIdArr));//kw音乐只返回第一个歌手id
    }

    private static class SearchResponseBean{
        @SerializedName("musics")
        List<SongDetail> songDetailList;
        class SongDetail{
            @SerializedName("copyrightId")
            String songId;
            @SerializedName("title")
            String songName;
            String albumName;
            String albumId;
            @SerializedName("singerName")
            String singerName;
            String singerId;
            @SerializedName("mp3")
            String lqLink;
            @SerializedName("lyrics")
            String lyricUrl;
            @SerializedName("cover")
            String picUrl;
            @SerializedName("hasSQqq")
            String quality;
            @SerializedName("mvId")
            String mvId;

            public String getSongId() {
                return songId;
            }

            public void setSongId(String songId) {
                this.songId = songId;
            }

            public String getSongName() {
                return songName;
            }

            public void setSongName(String songName) {
                this.songName = songName;
            }

            public String getAlbumName() {
                return albumName;
            }

            public void setAlbumName(String albumName) {
                this.albumName = albumName;
            }

            public String getAlbumId() {
                return albumId;
            }

            public void setAlbumId(String albumId) {
                this.albumId = albumId;
            }

            public String getSingerName() {
                return singerName;
            }

            public void setSingerName(String singerName) {
                this.singerName = singerName;
            }

            public String getSingerId() {
                return singerId;
            }

            public void setSingerId(String singerId) {
                this.singerId = singerId;
            }

            public String getLqLink() {
                return lqLink;
            }

            public void setLqLink(String lqLink) {
                this.lqLink = lqLink;
            }

            public String getLyricUrl() {
                return lyricUrl;
            }

            public void setLyricUrl(String lyricUrl) {
                this.lyricUrl = lyricUrl;
            }

            public String getPicUrl() {
                return picUrl;
            }

            public void setPicUrl(String picUrl) {
                this.picUrl = picUrl;
            }

            public String getQuality() {
                return quality;
            }

            public void setQuality(String quality) {
                this.quality = quality;
            }

            public String getMvId() {
                return mvId;
            }

            public void setMvId(String mvId) {
                this.mvId = mvId;
            }
        }

    }

    private SongBean getDownloadLinkById(String songid, String quality) {
        SongBean songBean=new SongBean("MG",songid);
        try {
            String link=baseContentLink + songid;
            Map<String,String> headerMap=new HashMap<>();
            headerMap.put("User-Agent",USER_AGENT_ANDROID);
            headerMap.put("Host","m.music.migu.cn");
            String response= downloadWebSiteUseGet(link ,headerMap);
            MgDownloadBean mgDownloadBean=new Gson().fromJson(response,MgDownloadBean.class);

            if(mgDownloadBean!=null&&mgDownloadBean.resource!=null&& mgDownloadBean.resource.size()>0){
                List<MgDownloadBean.MgMusicDetail.RateFormats> rateFormatList= mgDownloadBean.resource.get(0).getRateFormats();
                songBean.setLrcUrl(mgDownloadBean.resource.get(0).getLrcUrl());
                if(rateFormatList!=null){
                    Map<String,String> map=songBean.getDownloadLinkMap();
                    for (MgDownloadBean.MgMusicDetail.RateFormats formats:rateFormatList){
                        map.put(formats.formatType,formats.getUrl());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return songBean;
    }

    private class MgDownloadBean{
        String code;
        String info;
        List<MgMusicDetail> resource;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getInfo() {
            return info;
        }

        public void setInfo(String info) {
            this.info = info;
        }

        public List<MgMusicDetail> getResource() {
            return resource;
        }

        public void setResource(List<MgMusicDetail> resource) {
            this.resource = resource;
        }

        class MgMusicDetail{
            String lrcUrl;
            List<RateFormats> rateFormats;

            public String getLrcUrl() {
                return lrcUrl;
            }

            public void setLrcUrl(String lrcUrl) {
                this.lrcUrl = lrcUrl;
            }

            public List<RateFormats> getRateFormats() {
                return rateFormats;
            }

            public void setRateFormats(List<RateFormats> rateFormats) {
                this.rateFormats = rateFormats;
            }

            class RateFormats{
                String formatType;
                String url;
                String size;

                public String getFormatType() {
                    return formatType;
                }

                public void setFormatType(String formatType) {
                    this.formatType = formatType;
                }

                public String getUrl() {
                    if(url!=null&&url.contains("/public")){
                        url="http://freetyst.nf.migu.cn" + url.substring(url.indexOf("/public"));
                    }
                    return url;
                }

                public void setUrl(String url) {
                    this.url = url;
                }

                public String getSize() {
                    return size;
                }

                public void setSize(String size) {
                    this.size = size;
                }
            }

        }

    }



}
