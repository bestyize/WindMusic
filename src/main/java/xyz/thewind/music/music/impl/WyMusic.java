package xyz.thewind.music.music.impl;

import com.google.gson.annotations.SerializedName;
import xyz.thewind.music.model.SongBean;
import xyz.thewind.music.music.MusicHelper;
import xyz.thewind.music.util.GsonConverter;


import java.net.URLEncoder;
import java.util.*;

import static xyz.thewind.music.util.AesEncrypt.AESEncode;
import static xyz.thewind.music.util.HttpRequestHelper.downloadWebSiteUsePost;
import static xyz.thewind.music.util.SongUtil.*;


public class WyMusic implements MusicHelper {
    public static final String wyCookie = "MUSIC_U=8149f85dbe6e500d647b36e8504fce3bd243a48a699bc26e7531174de4cd486e26296e43872d0f6956a6c2c5be5fc4fd0d8f0d281b3ffd12; __remember_me=true; __csrf=26ae61aeed306bf69e4d2ea5edd00c9b;";
    private static final HashMap<String, String> qualityMap;

    static {
        qualityMap=new HashMap<>();
        qualityMap.put(QUALITY_SQ,"999000");
        qualityMap.put(QUALITY_HQ,"320000");
        qualityMap.put(QUALITY_PQ,"192000");
        qualityMap.put(QUALITY_LQ,"128000");
    }
    @Override
    public List<SongBean> searchMusic(String keyword, int num) {

        int page=1;
        String text = "{\"s\":\"" + keyword + "\",\"type\":1,\"offset\":" + (page - 1) * num + ",\"limit\":" + num + ",\"total\":true}";
        String response = wyHttpRequest("http://music.163.com/weapi/cloudsearch/get/web?csrf_token=", text);
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
     *        this.songSrc = songSrc;
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

    private String wyHttpRequest(String url,String params){
        String encParams=requestParamsEncrypt(params);
        String response="";
        try {

            Map<String,String> headerMap=new HashMap<>();
            headerMap.put("User-Agent","Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.100 Safari/537.36");
            headerMap.put("Host","music.163.com");
            headerMap.put("Connection","Keep-Alive");
            headerMap.put("Content-Type","application/x-www-form-urlencoded");
            headerMap.put("Cookie",wyCookie);
            headerMap.put("Accept","text/html, application/xhtml+xml, application/xml; q=0.9, image/webp, */*; q=0.8");
            response=downloadWebSiteUsePost(url,encParams,headerMap);
        } catch (Exception ignored) {
        }
        return response;
    }


    private String getPlayUrl(String id, String quality){
        String text = "{\"ids\":[\"" + id + "\"],\"br\":" + quality + ",\"csrf_token\":\"\"}";
        String html = wyHttpRequest("http://music.163.com/weapi/song/enhance/player/url?csrf_token=", text);
        String url=html.substring(html.indexOf("\"url\":")+"\"url\":".length()+1);
        url=url.substring(0,url.indexOf(",")-1);
        return url;
    }

    private String requestParamsEncrypt(String params){
        String encKey="0CoJUm6Qyw8W8jud";
        String secKey = "a44e542eaac91dce";
        int pad = 16 - params.length() % 16;
        for (int i = 0; i < pad; i++)
        {
            params = params + (char)pad;
        }
        String aiv = "0102030405060708";
        try {
            String aesEncData=AESEncode(AESEncode(params,encKey,aiv),secKey,aiv);
            String encSecKey = "411571dca16717d9af5ef1ac97a8d21cb740329890560688b1b624de43f49fdd7702493835141b06ae45f1326e264c98c24ce87199c1a776315e5f25c11056b02dd92791fcc012bff8dd4fc86e37888d5ccc060f7837b836607dbb28bddc703308a0ba67c24c6420dd08eec2b8111067486c907b6e53c027ae1e56c188bc568e";
            return "params="+ URLEncoder.encode(aesEncData,"utf-8") +"&encSecKey="+encSecKey;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    private class SearchResponseBean{
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

        class Data{
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

            class SongDetail{
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

                class Singer{
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
                class Album{
                    long albumId;
                    String albumName;
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
                class Quality{
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
                class Privilege{
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
}
