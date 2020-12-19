package xyz.thewind.music.music.impl;

import com.google.gson.annotations.SerializedName;
import xyz.thewind.music.model.SongBean;
import xyz.thewind.music.music.MusicHelper;
import xyz.thewind.music.util.GsonConverter;
import xyz.thewind.music.util.HttpRequestHelper;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static xyz.thewind.music.util.HttpRequestHelper.downloadWebSiteUseGet;
import static xyz.thewind.music.util.SongUtil.*;


public class KwMusic implements MusicHelper {
    private static final HashMap<String, String> qualityMap;
    private static final Map<String,String> mvQualityMap=new HashMap<>();

    static {
        qualityMap=new HashMap<>();
        qualityMap.put(QUALITY_SQ,"&br=2000&format=flac");
        qualityMap.put(QUALITY_HQ,"&br=320&format=mp3");
        qualityMap.put(QUALITY_PQ,"&br=192&format=mp3");
        qualityMap.put(QUALITY_LQ,"&br=128&format=mp3");
        mvQualityMap.put(QUALITY_SQ,"MP4BD");
        mvQualityMap.put(QUALITY_HQ,"MP4UL");
        mvQualityMap.put(QUALITY_PQ,"MP4");
        mvQualityMap.put(QUALITY_LQ,"MP4L");
    }

    @Override
    public List<SongBean> searchMusic(String keyword, int num) {

        String response=kwHttpSearchRequest(keyword,num);
        return kwSongSearchRespProcess(response);
    }

    @Override
    public SongBean searchDownloadLink(SongBean songBean,String quality) {
        String downloadLink = getDownloadLinkById(songBean.getSongId(), quality);
        if(downloadLink==null){
            return songBean;
        }
        Map<String,String> map=songBean.getDownloadLinkMap();
        map.put(quality,downloadLink);
        return songBean;
    }

    @Override
    public List<SongBean> searchWithDownload(String keyword, int num) {
        List<SongBean> songBeanList=searchMusic(keyword,num);
        for (SongBean bean:songBeanList){
            searchDownloadLink(bean,QUALITY_SQ);
            searchDownloadLink(bean,QUALITY_HQ);
            searchDownloadLink(bean,QUALITY_PQ);
            searchDownloadLink(bean,QUALITY_LQ);
        }
        return songBeanList;
    }

    /**
     * 还有另一个接口不需要Cookie
     * http://www.kuwo.cn/bd/search/musicSearch?key=%E5%8D%81%E5%B9%B4&pn=0&rn=20&bdfrom=xshow&c=nfmbhi6fxwaj
     * @param keyword
     * @param num
     * @return
     */
    private String kwHttpSearchRequest(String keyword, int num){
        String key,response="";
        try {
            key = URLEncoder.encode(keyword, "utf-8");
            String requestLink = "http://www.kuwo.cn/api/www/search/searchMusicBykeyWord?key=" + key+ "&pn=1&rn=" + num;
            Map<String,String> headerMap=new HashMap<>();
            headerMap.put("Cookie","kw_token=62COOIBCS34");
            headerMap.put("csrf","62COOIBCS34");
            headerMap.put("Referer","https://kuwo.cn/search/list?key="+key);
            headerMap.put("Keep-Alive","close");
            response = downloadWebSiteUseGet(requestLink,headerMap);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return response;

    }

    /**
     * @param response
     * @return
     */
    private List<SongBean> kwSongSearchRespProcess(String response){

        List<SongBean> list=new LinkedList<>();
        if(response.length()==0){
            return list;
        }
        try {
            SearchResponseBean searchResponseBean= GsonConverter.fromJson(response,SearchResponseBean.class);
            if(searchResponseBean.code==HTTP_CODE_200){
                List<SearchResponseBean.Data.SongDetail> songDetailList=searchResponseBean.data.list;
                for (SearchResponseBean.Data.SongDetail detail:songDetailList){
                    SongBean songBean=new SongBean(SRC_KW,
                            detail.songId,
                            detail.songName,
                            parseSingersId(detail.getSingerId()+""),
                            parseSingers(detail.singerName),
                            String.valueOf(detail.getAlbumId()),
                            detail.albumName,
                            (detail.hasLossless?QUALITY_SQ:QUALITY_HQ),
                            detail.getPicUrl(),"","");
                    list.add(songBean);
                }
            }
        }catch (Exception ignored){

        }

        return list;
    }

    private List<String> parseSingers(String singers){
        List<String> singerList=new LinkedList<>();
        String[] singerArr=singers.split("&");
        Collections.addAll(singerList, singerArr);
        return singerList;
    }

    private List<String> parseSingersId(String singerId){
        return new LinkedList<>(Collections.singletonList(singerId));//kw音乐只返回第一个歌手id
    }

    /**
     * 还有另外的接口，支持VIP歌曲，除了flac都可以下载
     * http://www.kuwo.cn/url?format=mp3&rid=228908&response=url&type=convert_url3&br=320&from=web&t=1584003980221&reqId=xxxxxxxxxxxxxxxxxx
     * http://antiserver.kuwo.cn/anti.s?type=convert_url3&rid=228908&format=mp3&response=url&br=320
     * 下面的接口是支持无损下载，且不需要加密的，mid为歌曲ID，br有四种选项 分别是:空出（aac）、128kmp3、320kmp3、2000kflac
     * http://www.kuwo.cn/bd/search/getSongUrlByMid?mid=1875835&br=2000kflac&bdfrom=&c=nfmbhi6fxwaj
     */

    private String getDownloadLinkById(String songid, String quality) {
        String downloadLink=null;
        try {
            String link = "http://mobi.kuwo.cn/mobi.s?f=kuwo&q=";
            String encParams = KwEncrypt.encrypt("&type=convert_url2" + qualityMap.get(quality) + "&sig=0&rid=" + songid + "&network=wifi");
            Map<String,String> headerMap=new HashMap<>();
            headerMap.put("User-Agent",USER_AGENT_ANDROID);
            String response= downloadWebSiteUseGet(link + encParams,headerMap);
            if(response!=null&&response.contains("url=")&&response.contains("sig=")){
                downloadLink= response.substring(response.indexOf("url=") + 4, response.lastIndexOf("sig="));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return downloadLink;
    }

    /**
     * http://m.kuwo.cn/newh5/singles/songinfoandlrc?musicId=80488731
     * @param lyricId
     * @return
     */
    @Override
    public String getLyric(String lyricId) {
        StringBuilder sb=new StringBuilder();
        try {
            String link="http://m.kuwo.cn/newh5/singles/songinfoandlrc?musicId="+lyricId;
            LyricResponseBean bean=GsonConverter.fromJson(HttpRequestHelper.downloadWebSiteUseGet(link,null),LyricResponseBean.class);

            if(bean!=null){
                List<LyricResponseBean.Data.Pair> pairList=bean.getData().getLrclist();
                if(pairList!=null){
                    for (LyricResponseBean.Data.Pair pair:pairList){
                        String lyric=pair.getLineLyric();
                        String time=convertToLyricTime(pair.getTime());
                        String line="["+time+"]"+lyric+"\n";
                        sb.append(line);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return sb.toString();
    }
    public String convertToLyricTime(String time){
        if(time==null||time.length()<4||!time.contains(".")||time.lastIndexOf(".")+2>time.length()-1){
            return "";
        }
        time=time.substring(0,time.indexOf(".")+3);
        int len=time.length();

        String subSecond=time.substring(len-2,len);
        String secondStr=time.substring(0,len-3);
        int timeInSecond=Integer.parseInt(secondStr);
        StringBuilder sb=new StringBuilder();
        int second=timeInSecond%60;
        int minute=timeInSecond/60;
        if(minute<10){
            sb.append("0");
        }
        sb.append(minute).append(":");
        if(second<10){
            sb.append("0");
        }
        sb.append(second).append(".").append(subSecond);
        return sb.toString();
    }

    /**
     * http://www.kuwo.cn/url?rid=80488731&response=url&format=mp4&type=convert_url
     * @param mvId
     * @return
     */
    @Override
    public String searchMvUrl(String mvId) {
        String link="http://www.kuwo.cn/url?response=url&format=mp4&type=convert_url&rid="+mvId;
        String url=downloadWebSiteUseGet(link,null);
        return (!url.startsWith("http"))?"":url;
    }

    /**
     * 方式2，可选质量
     * @param mvId
     * @param quality
     * @return
     */
    public String searchMvUrl(String mvId,String quality){
        String link = "http://mobi.kuwo.cn/mobi.s?f=kuwo&q=";
        String encParams = KwEncrypt.encrypt("&type=convert_mv_url2&format=mp4&quality="+mvQualityMap.get(quality)+"&sig=0&rid=" + mvId + "&network=wifi");
        Map<String,String> headerMap=new HashMap<>();
        headerMap.put("User-Agent",USER_AGENT_ANDROID);
        String response= downloadWebSiteUseGet(link + encParams,headerMap);
        if(response!=null&&response.contains("url=")&&response.contains("sig=")){
            return response.substring(response.indexOf("url=")+4,response.indexOf("sig="));
        }
        return "";
    }

    @Override
    public SongBean searchSongDetail(String songId) {
        String requestLink = "http://www.kuwo.cn/api/www/music/musicInfo?mid=" +songId;
        Map<String,String> headerMap=new HashMap<>();
        headerMap.put("Cookie","kw_token=62COOIBCS34");
        headerMap.put("csrf","62COOIBCS34");
        //headerMap.put("Referer","https://kuwo.cn/search/list?key="+key);
        headerMap.put("Keep-Alive","close");
        String response = downloadWebSiteUseGet(requestLink,headerMap);
        SongBean songBean=new SongBean("KW",songId);
        try{
            SongDetail detail=GsonConverter.fromJson(response,SongDetail.class);
            SongDetail.Data data=detail.getData();
            if(detail!=null&&data!=null){
                songBean.setSongName(data.getSongName());
                songBean.setSingersId(Arrays.asList(data.getSingerId()+""));
                songBean.setPicUrl(data.getPic());
                songBean.setAlbumName(data.albumName);
                songBean.setQuality(data.hasLossless?"SQ":"HQ");
                songBean.setAlbumId(data.albumId+"");
                songBean.setSingersName(Arrays.asList(data.singerName));
                return songBean;
            }
        }catch (Exception e){

        }
        return null;
    }

    private static class SearchResponseBean{

        private int code;
        private long curTime;
        private Data data;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public long getCurTime() {
            return curTime;
        }

        public void setCurTime(long curTime) {
            this.curTime = curTime;
        }

        public Data getData() {
            return data;
        }

        public void setData(Data data) {
            this.data = data;
        }

        static class Data{
            private String total;
            private List<SongDetail> list;

            public String getTotal() {
                return total;
            }

            public void setTotal(String total) {
                this.total = total;
            }

            public List<SongDetail> getList() {
                return list;
            }

            public void setList(List<SongDetail> list) {
                this.list = list;
            }

            static class SongDetail{
                @SerializedName("rid")
                private String songId;
                @SerializedName("name")
                private String songName;
                @SerializedName("artist")
                private String singerName;
                @SerializedName("pic")
                private String picUrl;

                private int duration;
                private boolean hasLossless;
                private int hasMv;
                private String releaseDate;
                @SerializedName("album")
                private String albumName;
                @SerializedName("albumid")
                private long albumId;
                @SerializedName("artistid")
                private int singerId;
                @SerializedName("albumPic")
                private String albumPicUrl;
                private String songTimeMinutes;
                private boolean isListenFee;

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

                public String getSingerName() {
                    return singerName;
                }

                public void setSingerName(String singerName) {
                    this.singerName = singerName;
                }

                public String getPicUrl() {
                    if(picUrl!=null&&picUrl.contains("http://")){
                        picUrl=picUrl.replace("http://","https://");
                    }
                    return picUrl;
                }

                public void setPicUrl(String picUrl) {
                    this.picUrl = picUrl;
                }

                public int getDuration() {
                    return duration;
                }

                public void setDuration(int duration) {
                    this.duration = duration;
                }

                public boolean isHasLossless() {
                    return hasLossless;
                }

                public void setHasLossless(boolean hasLossless) {
                    this.hasLossless = hasLossless;
                }

                public int getHasMv() {
                    return hasMv;
                }

                public void setHasMv(int hasMv) {
                    this.hasMv = hasMv;
                }

                public String getReleaseDate() {
                    return releaseDate;
                }

                public void setReleaseDate(String releaseDate) {
                    this.releaseDate = releaseDate;
                }

                public String getAlbumName() {
                    return albumName;
                }

                public void setAlbumName(String albumName) {
                    this.albumName = albumName;
                }

                public long getAlbumId() {
                    return albumId;
                }

                public void setAlbumId(long albumId) {
                    this.albumId = albumId;
                }

                public int getSingerId() {
                    return singerId;
                }

                public void setSingerId(int singerId) {
                    this.singerId = singerId;
                }

                public String getAlbumPicUrl() {
                    return albumPicUrl;
                }

                public void setAlbumPicUrl(String albumPicUrl) {
                    this.albumPicUrl = albumPicUrl;
                }

                public String getSongTimeMinutes() {
                    return songTimeMinutes;
                }

                public void setSongTimeMinutes(String songTimeMinutes) {
                    this.songTimeMinutes = songTimeMinutes;
                }

                public boolean isListenFee() {
                    return isListenFee;
                }

                public void setListenFee(boolean listenFee) {
                    isListenFee = listenFee;
                }
            }
        }


    }

    private static class LyricResponseBean{
        Data data=new Data();

        public Data getData() {
            return data;
        }

        public void setData(Data data) {
            this.data = data;
        }

        static class Data{
            List<Pair> lrclist;

            public List<Pair> getLrclist() {
                return lrclist;
            }

            public void setLrclist(List<Pair> lrclist) {
                this.lrclist = lrclist;
            }

            static class Pair{
                String lineLyric;
                String time;

                public String getLineLyric() {
                    return lineLyric;
                }

                public void setLineLyric(String lineLyric) {
                    this.lineLyric = lineLyric;
                }

                public String getTime() {
                    return time;
                }

                public void setTime(String time) {
                    this.time = time;
                }
            }
        }
    }

    private static class SongDetail{
        int code;
        Data data;

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
            @SerializedName("artist")
            String singerName;
            String pic;
            @SerializedName("songId")
            long rid;
            int duration;
            @SerializedName("artistid")
            long singerId;
            @SerializedName("album")
            String albumName;
            @SerializedName("albumid")
            long albumId;
            boolean hasLossless;
            @SerializedName("name")
            String songName;

            public String getSingerName() {
                return singerName;
            }

            public void setSingerName(String singerName) {
                this.singerName = singerName;
            }

            public String getPic() {
                if(pic!=null&&pic.contains("http://")){
                    pic=pic.replace("http://","https://");
                }
                return pic;
            }

            public void setPic(String pic) {
                this.pic = pic;
            }

            public long getRid() {
                return rid;
            }

            public void setRid(long rid) {
                this.rid = rid;
            }

            public int getDuration() {
                return duration;
            }

            public void setDuration(int duration) {
                this.duration = duration;
            }

            public long getSingerId() {
                return singerId;
            }

            public void setSingerId(long singerId) {
                this.singerId = singerId;
            }

            public String getAlbumName() {
                return albumName;
            }

            public void setAlbumName(String albumName) {
                this.albumName = albumName;
            }

            public long getAlbumId() {
                return albumId;
            }

            public void setAlbumId(long albumId) {
                this.albumId = albumId;
            }

            public boolean isHasLossless() {
                return hasLossless;
            }

            public void setHasLossless(boolean hasLossless) {
                this.hasLossless = hasLossless;
            }

            public String getSongName() {
                return songName;
            }

            public void setSongName(String songName) {
                this.songName = songName;
            }
        }
    }

    /**
     * 从酷我客户端上找到的加密算法
     */
    private static class KwEncrypt {

        public static final byte[] kwEncryptKey = "ylzsxkwm".getBytes();

        /* renamed from: b  reason: collision with root package name */
        public static final int keyLen = kwEncryptKey.length;

        private static final long[] longMask = {1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048, 4096, 8192, 16384, 32768, 65536, 131072, 262144, 524288, 1048576, 2097152, 4194304, 8388608, 16777216, 33554432, 67108864, 134217728, 268435456,536870912,1073741824, 2147483648L, 4294967296L, 8589934592L, 17179869184L, 34359738368L, 68719476736L, 137438953472L, 274877906944L, 549755813888L, 1099511627776L, 2199023255552L, 4398046511104L, 8796093022208L, 17592186044416L, 35184372088832L, 70368744177664L, 140737488355328L, 281474976710656L, 562949953421312L, 1125899906842624L, 2251799813685248L, 4503599627370496L, 9007199254740992L, 18014398509481984L, 36028797018963968L, 72057594037927936L, 144115188075855872L, 288230376151711744L, 576460752303423488L, 1152921504606846976L, 2305843009213693952L, 4611686018427387904L, Long.MIN_VALUE};

        private static final int[] dirE = {57, 49, 41, 33, 25, 17, 9, 1, 59, 51, 43, 35, 27, 19, 11, 3, 61, 53, 45, 37, 29, 21, 13, 5, 63, 55, 47, 39, 31, 23, 15, 7, 56, 48, 40, 32, 24, 16, 8, 0, 58, 50, 42, 34, 26, 18, 10, 2, 60, 52, 44, 36, 28, 20, 12, 4, 62, 54, 46, 38, 30, 22, 14, 6};

        /* renamed from: f  reason: collision with root package name */
        private static final int[] dirF = {31, 0, 1, 2, 3, 4, -1, -1, 3, 4, 5, 6, 7, 8, -1, -1, 7, 8, 9, 10, 11, 12, -1, -1, 11, 12, 13, 14, 15, 16, -1, -1, 15, 16, 17, 18, 19, 20, -1, -1, 19, 20, 21, 22, 23, 24, -1, -1, 23, 24, 25, 26, 27, 28, -1, -1, 27, 28, 29, 30, 31, 30, -1, -1};

        /* renamed from: g  reason: collision with root package name */
        private static final char[][] dirG = {new char[]{14, 4, 3, 15, 2, '\r', 5, 3, '\r', 14, 6, '\t', 11, 2, 0, 5, 4, 1, '\n', '\f', 15, 6, '\t', '\n', 1, '\b', '\f', 7, '\b', 11, 7, 0, 0, 15, '\n', 5, 14, 4, '\t', '\n', 7, '\b', '\f', 3, '\r', 1, 3, 6, 15, '\f', 6, 11, 2, '\t', 5, 0, 4, 2, 11, 14, 1, 7, '\b', '\r'}, new char[]{15, 0, '\t', 5, 6, '\n', '\f', '\t', '\b', 7, 2, '\f', 3, '\r', 5, 2, 1, 14, 7, '\b', 11, 4, 0, 3, 14, 11, '\r', 6, 4, 1, '\n', 15, 3, '\r', '\f', 11, 15, 3, 6, 0, 4, '\n', 1, 7, '\b', 4, 11, 14, '\r', '\b', 0, 6, 2, 15, '\t', 5, 7, 1, '\n', '\f', 14, 2, 5, '\t'}, new char[]{'\n', '\r', 1, 11, 6, '\b', 11, 5, '\t', 4, '\f', 2, 15, 3, 2, 14, 0, 6, '\r', 1, 3, 15, 4, '\n', 14, '\t', 7, '\f', 5, 0, '\b', 7, '\r', 1, 2, 4, 3, 6, '\f', 11, 0, '\r', 5, 14, 6, '\b', 15, 2, 7, '\n', '\b', 15, 4, '\t', 11, 5, '\t', 0, 14, 3, '\n', 7, 1, '\f'}, new char[]{7, '\n', 1, 15, 0, '\f', 11, 5, 14, '\t', '\b', 3, '\t', 7, 4, '\b', '\r', 6, 2, 1, 6, 11, '\f', 2, 3, 0, 5, 14, '\n', '\r', 15, 4, '\r', 3, 4, '\t', 6, '\n', 1, '\f', 11, 0, 2, 5, 0, '\r', 14, 2, '\b', 15, 7, 4, 15, 1, '\n', 7, 5, 6, '\f', 11, 3, '\b', '\t', 14}, new char[]{2, 4, '\b', 15, 7, '\n', '\r', 6, 4, 1, 3, '\f', 11, 7, 14, 0, '\f', 2, 5, '\t', '\n', '\r', 0, 3, 1, 11, 15, 5, 6, '\b', '\t', 14, 14, 11, 5, 6, 4, 1, 3, '\n', 2, '\f', 15, 0, '\r', 2, '\b', 5, 11, '\b', 0, 15, 7, 14, '\t', 4, '\f', 7, '\n', '\t', 1, '\r', 6, 3}, new char[]{'\f', '\t', 0, 7, '\t', 2, 14, 1, '\n', 15, 3, 4, 6, '\f', 5, 11, 1, 14, '\r', 0, 2, '\b', 7, '\r', 15, 5, 4, '\n', '\b', 3, 11, 6, '\n', 4, 6, 11, 7, '\t', 0, 6, 4, 2, '\r', 1, '\t', 15, 3, '\b', 15, 3, 1, 14, '\f', 5, 11, 0, 2, '\f', 14, 7, 5, '\n', '\b', '\r'}, new char[]{4, 1, 3, '\n', 15, '\f', 5, 0, 2, 11, '\t', 6, '\b', 7, 6, '\t', 11, 4, '\f', 15, 0, 3, '\n', 5, 14, '\r', 7, '\b', '\r', 14, 1, 2, '\r', 6, 14, '\t', 4, 1, 2, 14, 11, '\r', 5, 0, 1, '\n', '\b', 3, 0, 11, 3, 5, '\t', 4, 15, 2, 7, '\b', '\f', 15, '\n', 7, 6, '\f'}, new char[]{'\r', 7, '\n', 0, 6, '\t', 5, 15, '\b', 4, 3, '\n', 11, 14, '\f', 5, 2, 11, '\t', 6, 15, '\f', 0, 3, 4, 1, 14, '\r', 1, 2, 7, '\b', 1, 2, '\f', 15, '\n', 4, 0, 3, '\r', 14, 6, '\t', 7, '\b', '\t', 6, 15, 1, 5, '\f', 3, '\n', 14, 5, '\b', 7, 11, 0, 4, '\r', 2, 11}};

        /* renamed from: h  reason: collision with root package name */
        private static final int[] dirH = {15, 6, 19, 20, 28, 11, 27, 16, 0, 14, 22, 25, 4, 17, 30, 9, 1, 7, 23, 13, 31, 26, 2, 8, 18, 12, 29, 5, 21, 10, 3, 24};
        private static final int[] dirI = {39, 7, 47, 15, 55, 23, 63, 31, 38, 6, 46, 14, 54, 22, 62, 30, 37, 5, 45, 13, 53, 21, 61, 29, 36, 4, 44, 12, 52, 20, 60, 28, 35, 3, 43, 11, 51, 19, 59, 27, 34, 2, 42, 10, 50, 18, 58, 26, 33, 1, 41, 9, 49, 17, 57, 25, 32, 0, 40, 8, 48, 16, 56, 24};
        private static final int[] dir = {56, 48, 40, 32, 24, 16, 8, 0, 57, 49, 41, 33, 25, 17, 9, 1, 58, 50, 42, 34, 26, 18, 10, 2, 59, 51, 43, 35, 62, 54, 46, 38, 30, 22, 14, 6, 61, 53, 45, 37, 29, 21, 13, 5, 60, 52, 44, 36, 28, 20, 12, 4, 27, 19, 11, 3};
        private static final int[] dirKey = {13, 16, 10, 23, 0, 4, -1, -1, 2, 27, 14, 5, 20, 9, -1, -1, 22, 18, 11, 3, 25, 7, -1, -1, 15, 6, 26, 19, 12, 1, -1, -1, 40, 51, 30, 36, 46, 54, -1, -1, 29, 39, 50, 44, 32, 47, -1, -1, 43, 48, 38, 55, 33, 52, -1, -1, 45, 41, 49, 35, 28, 31, -1, -1};
        private static final int[] dirL = {1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1};
        private static final long[] maskM = {0, 1048577, 3145731};
        private static long p = 0;
        private static long q;
        private static long r;
        private static int[] s = new int[2];
        private static byte[] t = new byte[8];
        private static int u;
        private static int v;
        private static int w;


        public static String encrypt(String text){
            byte[] cipherText=encrypt(text.getBytes(StandardCharsets.UTF_8),text.length(), kwEncryptKey,keyLen);
            return Base64.getEncoder().encodeToString(cipherText);
        }
        private static long magic(int[] arr, long magic) {
            long sum = 0;
            for (int i = 0; i < arr.length; i++) {
                if (arr[i] >= 0 && (longMask[arr[i]] & magic) != 0) {
                    sum |= longMask[i];
                }
            }
            return sum;
        }

        private static void magic(long j2, long[] arr, int magic) {
            long sum = magic(dir, j2);
            for (int i = 0; i < 16; i++) {
                sum = ((sum & (maskM[dirL[i]] ^ -1)) >> dirL[i]) | ((maskM[dirL[i]] & sum) << (28 - dirL[i]));
                arr[i] = magic(dirKey,sum);
            }
            if (magic == 1) {
                for (int i = 0; i < 8; i++) {
                    long j = arr[i];
                    int k = 15 - i;
                    arr[i] = arr[k];
                    arr[k] = j;
                }
            }
        }

        private static long magic(long[] arr, long magic) {
            p = magic(dirE,magic);
            s[0] = (int) (p & 4294967295L);
            s[1] = (int) ((p & -4294967296L) >> 32);
            for (int i = 0; i < arr.length; i++) {
                r = s[1];
                r = magic(dirF,r);
                r ^= arr[i];
                for (int j = 0; j < t.length; j++) {
                    t[j] = (byte) ((int) (255 & (r >> (j * 8))));
                }
                u = 0;
                int k = 7;
                while (true) {
                    w = k;
                    if (w < 0) {
                        break;
                    }
                    u <<= 4;
                    u |= dirG[w][t[w]];
                    k = w - 1;
                }
                r = u;
                r = magic(dirH,r);
                q = s[0];
                s[0] = s[1];
                s[1] = (int) (q ^ r);
            }
            v = s[0];
            s[0] = s[1];
            s[1] = v;
            p = (((long) s[0]) & 4294967295L) | ((((long) s[1]) << 32) & -4294967296L);
            p = magic(dirI,p);
            return p;
        }

        private static synchronized byte[] encrypt(byte[] text, int textLen, byte[] key, int keyLen) {
            byte[] cipherText;
            synchronized (KwEncrypt.class) {
                long x = 0;
                for (int i = 0; i < keyLen; i++) {
                    x |= ((long) key[i]) << (i << 3);
                }
                int ranLen = textLen >>3;
                long[] map = new long[16];
                for (int i = 0; i < 16; i++) {
                    map[i] = 0;
                }
                long[] ranArr = new long[ranLen];
                for (int i = 0; i < ranArr.length; i++) {
                    for (int j = 0; j < 8; j++) {
                        ranArr[i] = (((long) text[(i * 8) + j]) << (j * 8)) | ranArr[i];
                    }
                }
                long[] longMap = new long[((((ranArr.length + 1) * 8) + 1) / 8)];
                magic(x, map, 0);
                for (int i = 0; i < ranArr.length; i++) {
                    longMap[i] = magic(map, ranArr[i]);
                }
                int i10 = textLen % 8;
                int diffLen =ranLen<<3;
                int fillLen = textLen - diffLen;
                byte[] fillArr = new byte[fillLen];
                System.arraycopy(text, diffLen, fillArr, 0, fillLen);
                long fillMagic = 0;
                for (int i = 0; i < i10; i++) {
                    fillMagic |= ((long) fillArr[i]) << (i * 8);
                }
                longMap[ranLen] = magic(map, fillMagic);
                cipherText = new byte[(longMap.length * 8)];
                int currPos = 0;
                int encBase = 0;
                while (currPos < longMap.length) {
                    int encPos = encBase;
                    for (int j = 0; j < 8; j++) {
                        cipherText[encPos] = (byte) ((int) (0xff & (longMap[currPos] >> (j * 8))));
                        encPos++;
                    }
                    currPos++;
                    encBase = encPos;
                }
            }
            return cipherText;
        }

    }


}
