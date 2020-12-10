package xyz.thewind.music.music.impl;

import com.google.gson.annotations.SerializedName;
import xyz.thewind.music.model.SongBean;
import xyz.thewind.music.music.MusicHelper;
import xyz.thewind.music.util.GsonConverter;
import xyz.thewind.music.util.HttpRequestHelper;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

import static xyz.thewind.music.util.SongUtil.*;


public class WyMusic implements MusicHelper {
    private static final HashMap<String, String> qualityMap;
    private static final String userCookie="MUSIC_U=ba0a04e302da6a12399d1817fe4eae9dd7c6b7f4865bf4fd9b986b5c8c07893333a649814e309366;";
    private static final Map<String,String> configMap=new HashMap<>();
    static {
        qualityMap=new HashMap<>();
        qualityMap.put(QUALITY_SQ,"999000");
        qualityMap.put(QUALITY_HQ,"320000");
        qualityMap.put(QUALITY_PQ,"192000");
        qualityMap.put(QUALITY_LQ,"128000");
    }
    @Override
    public void setConfigMap(Map<String, String> configMaps) {
        configMap.putAll(configMaps);
    }
    @Override
    public List<SongBean> searchMusic(String keyword, int num) {
        int page=1;
        String searchLink= null;
        try {
            searchLink = "http://music.163.com/api/cloudsearch/get/web?s="+ URLEncoder.encode(keyword,"UTF-8")+"&type=1&offset="+(page-1)*num+"&limit="+num+"&total=true";
        } catch (Exception e) {
            e.printStackTrace();
        }
        String response = HttpRequestHelper.downloadWebSiteUseGet(searchLink, null);
        return wySongSearchRespProcess(response);
    }
    //目前仅仅支持网易普通格式
    @Override
    public SongBean searchDownloadLink(SongBean songBean,String quality) {
        String bitRate=qualityMap.get(quality);
        if(bitRate==null){
            bitRate="128000";
        }
        String downloadLink=getPlayUrl(songBean.getSongId(),bitRate);
        Map<String,String> map=songBean.getDownloadLinkMap();
        map.put(quality,downloadLink);
        return songBean;
    }

    @Override
    public List<SongBean> searchWithDownload(String keyword, int num) {
        List<SongBean> songBeanList=searchMusic(keyword,num);
        for (SongBean bean:songBeanList){
            searchDownloadLink(bean,"SQ");
            searchDownloadLink(bean,"HQ");
            searchDownloadLink(bean,"PQ");
            searchDownloadLink(bean,"LQ");
        }
        return songBeanList;
    }

    /**
     * @param response
     * @return
     */
    private List<SongBean> wySongSearchRespProcess(String response){
        List<SongBean> songBeanList=new LinkedList<>();
        try {
            SearchResponseBean searchResponseBean= GsonConverter.fromJson(response,SearchResponseBean.class);
            if(searchResponseBean!=null&&searchResponseBean.result!=null){
                List<SearchResponseBean.Data.SongDetail> songsDetailList=searchResponseBean.result.getSongsList();
                for (SearchResponseBean.Data.SongDetail detail:songsDetailList){
                    SongBean songBean=new SongBean(SRC_WY,
                            String.valueOf(detail.getSongId()),
                            detail.getSongName(),
                            parseSingersId(detail.getAr()),
                            parseSingers(detail.getAr()),
                            String.valueOf(detail.getAl().getAlbumId()),
                            detail.getAl().getAlbumName(),
                            detail.getPrivilege().maxbr==999000?QUALITY_SQ:QUALITY_HQ,
                            detail.getAl().getAlbumPicUrl(),
                            "",
                            String.valueOf(detail.getMvId())
                            );
                    songBeanList.add(songBean);
                }
            }
        }catch (Exception ignored){

        }
        return songBeanList;
    }

    private List<String> parseSingers(List<SearchResponseBean.Data.SongDetail.Singer> singers){
        List<String> singerList=new LinkedList<>();
        for (SearchResponseBean.Data.SongDetail.Singer singer:singers){
            singerList.add(singer.getSingerName());
        }
        return singerList;
    }
    private List<String> parseSingersId(List<SearchResponseBean.Data.SongDetail.Singer> singers){
        List<String> singerIdList=new LinkedList<>();
        for (SearchResponseBean.Data.SongDetail.Singer singer:singers){
            singerIdList.add(singer.getSingerId()+"");
        }
        return singerIdList;
    }

    private String getPlayUrl(String id, String quality){
        List<String> ids=new ArrayList<>();
        ids.add(id);
        return getPlayUrl(ids,quality);
    }

    private String getPlayUrl(List<String> ids,String quality){
        String url= null;
        try {
            url = "http://music.163.com/api/song/enhance/player/url?ids="+ URLEncoder.encode(ids.toString(),"UTF-8")+"&br="+quality;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Map<String,String> headerMap=new HashMap<>();
        String cookie=configMap.get("wy_music_u");
        headerMap.put("Cookie",cookie==null?userCookie:cookie);
        String response=HttpRequestHelper.downloadWebSiteUseGet(url,headerMap);
        DownloadResponseBean responseBean=GsonConverter.fromJson(response,DownloadResponseBean.class);
        if(responseBean!=null&&responseBean.getData()!=null){
            DownloadResponseBean.Data data=responseBean.getData().get(0);
            if(data!=null){
                return data.getUrl();
            }
        }
        return null;
    }

    @Override
    public String searchMvUrl(String mvId) {
        //https://music.163.com/api/song/enhance/play/mv/url?id=303140&r=1080;
        String response=HttpRequestHelper.downloadWebSiteUseGet("https://music.163.com/api/song/enhance/play/mv/url?id="+mvId+"&r=1080",null);
        MvDownloadBean bean=GsonConverter.fromJson(response,MvDownloadBean.class);
        if(bean!=null){
            return bean.getData().getUrl();
        }
        return "";
    }

    private static class SearchResponseBean{
        boolean needLogin;
        Data result;
        int code;

        public boolean isNeedLogin() {
            return needLogin;
        }

        public void setNeedLogin(boolean needLogin) {
            this.needLogin = needLogin;
        }

        public Data getResult() {
            return result;
        }

        public void setResult(Data result) {
            this.result = result;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        static class Data{
            @SerializedName("songs")
            List<SongDetail> songList;
            int songCount;

            public List<SongDetail> getSongsList() {
                return songList;
            }

            public void setSongsList(List<SongDetail> songsList) {
                this.songList = songsList;
            }

            public int getSongCount() {
                return songCount;
            }

            public void setSongCount(int songCount) {
                this.songCount = songCount;
            }

            static class SongDetail{
                @SerializedName("id")
                long songId;
                @SerializedName("name")
                String songName;
                List<Singer> ar;
                Album al;

                @SerializedName("s")
                Quality sQuality;
                @SerializedName("h")
                Quality hQuality;
                @SerializedName("p")
                Quality pQuality;
                @SerializedName("l")
                Quality lQuality;
                @SerializedName("mv")
                long mvId;

                Privilege privilege;

                public long getSongId() {
                    return songId;
                }

                public void setSongId(long songId) {
                    this.songId = songId;
                }

                public String getSongName() {
                    return songName;
                }

                public void setSongName(String songName) {
                    this.songName = songName;
                }

                public List<Singer> getAr() {
                    return ar;
                }

                public void setAr(List<Singer> ar) {
                    this.ar = ar;
                }

                public Album getAl() {
                    return al;
                }

                public void setAl(Album al) {
                    this.al = al;
                }

                public Quality getsQuality() {
                    return sQuality;
                }

                public void setsQuality(Quality sQuality) {
                    this.sQuality = sQuality;
                }

                public Quality gethQuality() {
                    return hQuality;
                }

                public void sethQuality(Quality hQuality) {
                    this.hQuality = hQuality;
                }

                public Quality getpQuality() {
                    return pQuality;
                }

                public void setpQuality(Quality pQuality) {
                    this.pQuality = pQuality;
                }

                public Quality getlQuality() {
                    return lQuality;
                }

                public void setlQuality(Quality lQuality) {
                    this.lQuality = lQuality;
                }

                public long getMvId() {
                    return mvId;
                }

                public void setMvId(long mvId) {
                    this.mvId = mvId;
                }

                public Privilege getPrivilege() {
                    return privilege;
                }

                public void setPrivilege(Privilege privilege) {
                    this.privilege = privilege;
                }

                static class Singer{
                    @SerializedName("id")
                    long singerId;
                    @SerializedName("name")
                    String singerName;

                    public long getSingerId() {
                        return singerId;
                    }

                    public void setSingerId(long singerId) {
                        this.singerId = singerId;
                    }

                    public String getSingerName() {
                        return singerName;
                    }

                    public void setSingerName(String singerName) {
                        this.singerName = singerName;
                    }
                }
                static class Album{
                    @SerializedName("id")
                    long albumId;
                    @SerializedName("name")
                    String albumName;
                    @SerializedName("picUrl")
                    String albumPicUrl;

                    public long getAlbumId() {
                        return albumId;
                    }

                    public void setAlbumId(long albumId) {
                        this.albumId = albumId;
                    }

                    public String getAlbumName() {
                        return albumName;
                    }

                    public void setAlbumName(String albumName) {
                        this.albumName = albumName;
                    }

                    public String getAlbumPicUrl() {
                        return albumPicUrl;
                    }

                    public void setAlbumPicUrl(String albumPicUrl) {
                        this.albumPicUrl = albumPicUrl;
                    }
                }
                static class Quality{
                    @SerializedName("br")
                    String bitRate;
                    @SerializedName("size")
                    long fileSize;

                    public String getBitRate() {
                        return bitRate;
                    }

                    public void setBitRate(String bitRate) {
                        this.bitRate = bitRate;
                    }

                    public long getFileSize() {
                        return fileSize;
                    }

                    public void setFileSize(long fileSize) {
                        this.fileSize = fileSize;
                    }
                }
                static class Privilege{
                    int maxbr;

                    public int getMaxbr() {
                        return maxbr;
                    }

                    public void setMaxbr(int maxbr) {
                        this.maxbr = maxbr;
                    }
                }

            }
        }
    }

    private static class DownloadResponseBean{
        int code;
        List<Data> data=new ArrayList<>();

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public List<Data> getData() {
            return data;
        }

        public void setData(List<Data> data) {
            this.data = data;
        }

        static class Data{
            long id;
            String url;
            int br;
            long size;
            String type;

            public long getId() {
                return id;
            }

            public void setId(long id) {
                this.id = id;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public int getBr() {
                return br;
            }

            public void setBr(int br) {
                this.br = br;
            }

            public long getSize() {
                return size;
            }

            public void setSize(long size) {
                this.size = size;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }
        }
    }

    private static class MvDownloadBean{
        int code;
        Data data=new Data();

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public Data getData() {
            return data;
        }

        public void setData(Data data) {
            this.data = data;
        }

        static class Data{
            int id;
            String url;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }
        }
    }
}
