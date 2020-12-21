package xyz.thewind.music.music.impl;


import com.google.gson.annotations.SerializedName;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.thewind.music.model.SongBean;
import xyz.thewind.music.music.MusicHelper;
import xyz.thewind.music.util.Base64Converter;
import xyz.thewind.music.util.GsonConverter;
import xyz.thewind.music.util.HttpRequestHelper;
import xyz.thewind.music.util.Md5Encrypt;


import javax.xml.ws.spi.WebServiceFeatureAnnotation;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

import static xyz.thewind.music.util.SongUtil.*;


public class KgMusic implements MusicHelper {
    private static final Logger logger= LogManager.getLogger(KgMusic.class);
    private static final HashMap<Integer, String> qualityMap;

    private static final Map<String,String> configMap=new HashMap<>();

    static {
        qualityMap=new HashMap<>();
        qualityMap.put(999000,QUALITY_SQ);
        qualityMap.put(320000,QUALITY_HQ);
        qualityMap.put(192000,QUALITY_PQ);
        qualityMap.put(128000,QUALITY_LQ);
    }

    @Override
    public void setConfigMap(Map<String, String> configMaps) {
        if(configMaps!=null){
            configMap.clear();
            configMap.putAll(configMaps);
        }
    }

    @Override
    public List<SongBean> searchMusic(String keyword, int num) {
        String response=searchRequest(keyword,num);
        return songSearchRespProcess(response);
    }

    @Override
    public SongBean searchDownloadLink(SongBean songBean, String quality) {
        String songId=songBean.getSongId();
        if(songId!=null){
            String[] hash=songId.split("-");
            if(hash.length==1||quality.equals(QUALITY_LQ)){
                songBean=downloadResponseProcess(songBean,downloadRequest(hash[0]));
            }else if(quality.equals(QUALITY_SQ)){
                songBean=downloadResponseProcess(songBean,downloadRequest(hash[2]));
            }else if(quality.equals(QUALITY_HQ)){
                songBean=downloadResponseProcess(songBean,downloadRequest(hash[1]));
            }
        }
        return songBean;
    }

    @Override
    public List<SongBean> searchWithDownload(String keyword, int num) {
        List<SongBean> songBeanList=searchMusic(keyword,num);
        for (SongBean bean:songBeanList){
            String[] hashs=bean.getSongId().split("-");
            for (String hash:hashs){
                downloadResponseProcess(bean,downloadRequest(hash));
            }
        }
        return songBeanList;
    }

    public static String searchRequest(String keyword, int num){
        String response=null;
        try {
            String requestLink="http://mobilecdn.kugou.com/api/v3/search/song?format=json&keyword="+ URLEncoder.encode(keyword,"utf-8") +"&page=1&pagesize="+num;
            Map<String,String> headerMap=new HashMap<>();
            headerMap.put("User-Agent",USER_AGENT_ANDROID);
            response= HttpRequestHelper.downloadWebSiteUseGet(requestLink,headerMap);
        } catch (UnsupportedEncodingException e) {
            logger.error(e.toString());
        }
        return response;
    }

    /**
     *         this.songSrc = songSrc;
     *         this.songId = songId;
     *         this.songName = songName;
     *         this.singersId=singersId;
     *         this.singersName = singersName;
     *         this.albumId=albumId;
     *         this.albumName = albumName;
     *         this.quality=quality;
     *         this.picUrl = picUrl;
     *         this.lrcUrl = lrcUrl;
     *         this.mvUrl = mvUrl;
     * @param response
     * @return
     */
    private List<SongBean> songSearchRespProcess(String response){

        List<SongBean> list=new LinkedList<>();
        if(response.length()==0){
            return list;
        }
        try {
            SearchResponseBean responseBean= GsonConverter.fromJson(response,SearchResponseBean.class);
            List<SearchResponseBean.Data.SongDetail> songDetailList=responseBean.getData().getSongDetailList();
            for (SearchResponseBean.Data.SongDetail detail:songDetailList){
                SongBean songBean=new SongBean(
                        SRC_KG,
                        detail.getSongId(),
                        detail.getSongName(),
                        parseSingersId(),
                        parseSingerList(detail.getSingers()),
                        detail.getAlbumId(),
                        detail.getAlbumName(),
                        detail.getQuality(),
                        "",
                        "",
                        detail.getMvId()
                );
                list.add(songBean);
            }
        }catch (Exception e){
            logger.error("parse search response failed");
        }
        return list;
    }

    private List<String> parseSingerList(String singer){
        String[] singers=singer.split("，");
        return new LinkedList<>(Arrays.asList(singers));
    }
    private List<String> parseSingersId(){
        return new LinkedList<>();//kg音乐没有返回歌手ID
    }



    public SongBean downloadResponseProcess(SongBean songBean, String response){
        try {
            response=response.replace("<!--KG_TAG_RES_START-->","").replace("<!--KG_TAG_RES_END-->","");
            DownloadResponseBean responseBean= GsonConverter.fromJson(response,DownloadResponseBean.class);
            Map<String,String> map=songBean.getDownloadLinkMap();
            songBean.setDownloadLinkMap(map);
            if(responseBean.status==1){
                map.put(responseBean.getQuality(),responseBean.getUrl().get(1).replaceAll("\\\\",""));
                songBean.setQuality(responseBean.getQuality());
            }else {
                logger.warn("is vip song");
            }
        }catch (Exception e){
            logger.error("failed");
        }

        return songBean;
    }

    public static String downloadRequest(String hash){
        Map<String,String> headerMap=new HashMap<>();
        headerMap.put("User-Agent","Android511-AndroidPhone-10259-14-0-NetMusic-wifi");
        headerMap.put("KG-FAKE",configMap.getOrDefault("kg_uid","1717986917"));
        headerMap.put("KG-RC","1");
        headerMap.put("KG-RF","00877b56");
        headerMap.put("KG-THash","1509c84");
        headerMap.put("x-router","tracker.kugou.com");
        headerMap.put("Host","gateway.kugou.com");
        return HttpRequestHelper.downloadWebSiteUseGet(downloadLinkBuilder(hash,false),headerMap);
    }

    public static String downloadLinkBuilder(String hash,boolean usePid){

        String pidVersionSecret= "57ae12eb6890223e355ccfcb74edf70d";
        String uid=configMap.getOrDefault("kg_uid","1717986917");
        if(uid==null){
            uid="0";
        }
        String token=configMap.get("kg_token");
        if(token==null){
            token="0";
        }
        String pidVersion="3001";
        String appid="1005";//1005是安卓手机版kg音乐
        String mid="0";
        String baseLink="https://gateway.kugou.com/i/v2/?";
        String vipType="6";
        String dfid="0";

        StringBuilder sb=new StringBuilder();
        sb.append("pid=2&cmd=26&area_code=1&behavior=play&module=&ptype=0&mtype=0&album_id=39027869&version=10259&album_audio_id=272185068&with_res_tag=1");
        sb.append("&mid=").append(mid);
        sb.append("&vipType=").append(vipType);
        sb.append("&hash=").append(hash);
        sb.append("&dfid=").append(dfid);
        sb.append("&token=").append(token);
        sb.append("&appid=").append(appid);
        sb.append("&userid=").append(uid);
        if(usePid){
            sb.append("&pidversion=").append(pidVersion);
            String key= Md5Encrypt.convertToMd5(hash+pidVersionSecret+appid+mid+uid);
            sb.append("&key=").append(key);
        }else {
            sb.append("&key=").append(Md5Encrypt.convertToMd5(hash + "kgcloudv2" + appid + mid + uid));
        }
        return baseLink+sb.toString();
    }


    /**
     * 也可以先：https://wwwapi.kugou.com/yy/index.php?r=play/getdata&hash=0CCA78AF607DC52400D31C18BF036277&mid=12345
     * 获取album_id
     * 然后：https://wwwapi.kugou.com/yy/index.php?r=play/getdata&hash=0CCA78AF607DC52400D31C18BF036277&mid=12345&album_id=40602950
     * 获取歌词
     * @param lyricId
     * @return
     */
    @Override
    public String getLyric(String lyricId) {
        if(lyricId==null||lyricId.length()<32){
            return "";
        }
        if(lyricId.contains("-")){
            lyricId=lyricId.substring(0,lyricId.indexOf("-"));
        }

        String searchLink="http://krcs.kugou.com/search?ver=1&hash="+lyricId;
        String searchResponse=HttpRequestHelper.downloadWebSiteUseGet(searchLink,null);
        try {
                LyricSearchResponseBean bean=GsonConverter.fromJson(searchResponse,LyricSearchResponseBean.class);
                if(bean!=null&&bean.getCandidates()!=null&&bean.getCandidates().size()>0){
                    LyricSearchResponseBean.Lyric lyric=bean.getCandidates().get(0);
                    String requestUrl="http://lyrics.kugou.com/download?&fmt=lrc&ver=1&id="+lyric.getId()+"&accesskey="+lyric.getAccesskey();
                    LyricData lyricData=GsonConverter.fromJson(HttpRequestHelper.downloadWebSiteUseGet(requestUrl,null),LyricData.class);
                    if(lyricData!=null){
                        return Base64Converter.decode(lyricData.getContent());
                    }

                }
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public SongBean searchSongDetail(String songId) {
        String sid=songId;
        if(songId.length()<20){
            return null;
        }
        if(songId.contains("-")){
            songId=songId.substring(0,songId.indexOf("-"));
        }
        String url="https://wwwapi.kugou.com/yy/index.php?r=play/getdata&hash="+songId.toUpperCase(Locale.ROOT)+"&mid=12345";
        try {
            String response=HttpRequestHelper.downloadWebSiteUseGet(url,null);
            SongDetail.Data data=GsonConverter.fromJson(response,SongDetail.class).data;
            if(data!=null){
                SongBean bean=new SongBean("KG",sid);
                bean.setSongName(data.songName);
                bean.setPicUrl(data.picUrl);
                List<SongDetail.Data.Authors> authors= data.authors;
                List<String> singersName=new ArrayList<>();
                List<String> singerIds=new ArrayList<>();
                for (SongDetail.Data.Authors author:authors){
                    singerIds.add(author.singerId);
                    singersName.add(author.singerName);
                }
                bean.setSingersId(singerIds);
                bean.setSingersName(singersName);
                bean.setAlbumName(data.albumName);
                bean.setAlbumId(data.albumId);
                return bean;
            }
        }catch (Exception r){
            r.printStackTrace();
        }
        return null;
    }

    private static class SearchResponseBean{
        int status;
        String error;
        Data data;

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }

        public Data getData() {
            return data;
        }

        public void setData(Data data) {
            this.data = data;
        }

        static class Data{
            @SerializedName("info")
            List<SongDetail> songDetailList;

            public List<SongDetail> getSongDetailList() {
                return songDetailList;
            }

            public void setSongDetailList(List<SongDetail> songDetailList) {
                this.songDetailList = songDetailList;
            }

            static class SongDetail{
                int price;
                @SerializedName("singername")
                String singers;
                @SerializedName("songname")
                String songName;
                String songId;
                @SerializedName("hash")
                String pqHash;
                @SerializedName("320hash")
                String hqHash;
                @SerializedName("sqhash")
                String sqHash;
                @SerializedName("filesize")
                long pqSize;
                @SerializedName("320filesize")
                long hqSize;
                @SerializedName("sqfilesize")
                long sqSize;
                @SerializedName("album_id")
                String albumId;
                @SerializedName("album_name")
                String albumName;
                @SerializedName("mvhash")
                String mvId;
                @SerializedName("duration")
                int timeSpan;

                String quality;

                public int getPrice() {
                    return price;
                }

                public void setPrice(int price) {
                    this.price = price;
                }

                public String getSingers() {
                    return singers;
                }

                public void setSingers(String singers) {
                    this.singers = singers;
                }

                public String getSongName() {
                    return songName;
                }

                public void setSongName(String songName) {
                    this.songName = songName;
                }

                public String getSongId() {
                    return getPqHash()+"-"+getHqHash()+"-"+getSqHash();
                }

                public void setSongId(String songId) {
                    this.songId = songId;
                }

                public String getPqHash() {
                    return pqHash;
                }

                public void setPqHash(String pqHash) {
                    this.pqHash = pqHash;
                }

                public String getHqHash() {
                    return hqHash;
                }

                public void setHqHash(String hqHash) {
                    this.hqHash = hqHash;
                }

                public String getSqHash() {
                    return sqHash;
                }

                public void setSqHash(String sqHash) {
                    this.sqHash = sqHash;
                }

                public long getPqSize() {
                    return pqSize;
                }

                public void setPqSize(long pqSize) {
                    this.pqSize = pqSize;
                }

                public long getHqSize() {
                    return hqSize;
                }

                public void setHqSize(long hqSize) {
                    this.hqSize = hqSize;
                }

                public long getSqSize() {
                    return sqSize;
                }

                public void setSqSize(long sqSize) {
                    this.sqSize = sqSize;
                }

                public String getAlbumId() {
                    return albumId;
                }

                public void setAlbumId(String albumId) {
                    this.albumId = albumId;
                }

                public String getAlbumName() {
                    return albumName;
                }

                public void setAlbumName(String albumName) {
                    this.albumName = albumName;
                }

                public String getMvId() {
                    return mvId;
                }

                public void setMvId(String mvId) {
                    this.mvId = mvId;
                }

                public int getTimeSpan() {
                    return timeSpan;
                }

                public void setTimeSpan(int timeSpan) {
                    this.timeSpan = timeSpan;
                }

                public String getQuality() {
                    return (getSqHash()!=null&&getSqHash().length()>0)?QUALITY_SQ:(getHqHash()!=null&&getHqHash().length()>0)?QUALITY_HQ:QUALITY_PQ;
                }

                public void setQuality(String quality) {
                    this.quality = quality;
                }
            }
        }
    }


    private static class DownloadResponseBean{
        int status;
        long fileSize;
        List<String> url;
        String extName;
        int bitRate;
        @SerializedName("timeLength")
        int timeSpan;

        String quality;

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public long getFileSize() {
            return fileSize;
        }

        public void setFileSize(long fileSize) {
            this.fileSize = fileSize;
        }

        public List<String> getUrl() {
            return url;
        }

        public void setUrl(List<String> url) {
            this.url = url;
        }

        public String getExtName() {
            return extName;
        }

        public void setExtName(String extName) {
            this.extName = extName;
        }

        public int getBitRate() {
            return bitRate>320000?999000:bitRate;
        }

        public void setBitRate(int bitRate) {
            this.bitRate = bitRate;
        }

        public int getTimeSpan() {
            return timeSpan;
        }

        public void setTimeSpan(int timeSpan) {
            this.timeSpan = timeSpan;
        }
        //kg音乐部分音乐源bitRate不正确，这时候要用扩展名区分（扩展名和音乐也不完全对应）
        public String getQuality() {
            int bitRate=getBitRate();
            if(bitRate>320000){
                bitRate=999000;
            }else if(bitRate>192000){
                bitRate=320000;
            }else if(bitRate>128000){
                bitRate=192000;
            }else {
                bitRate=128000;
            }
            return getExtName().equals("flac")?QUALITY_SQ:qualityMap.get(bitRate);
        }

        public void setQuality(String quality) {
            this.quality = quality;
        }
    }

    private static class LyricSearchResponseBean{
        int errcode;
        List<Lyric> candidates;

        public int getErrcode() {
            return errcode;
        }

        public void setErrcode(int errcode) {
            this.errcode = errcode;
        }

        public List<Lyric> getCandidates() {
            return candidates;
        }

        public void setCandidates(List<Lyric> candidates) {
            this.candidates = candidates;
        }

        static class Lyric{
            String id;
            String accesskey;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getAccesskey() {
                return accesskey;
            }

            public void setAccesskey(String accesskey) {
                this.accesskey = accesskey;
            }
        }
    }
    private static class LyricData{
        String content;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

    private static class SongDetail{
        int status;
        Data data=new Data();

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public Data getData() {
            return data;
        }

        public void setData(Data data) {
            this.data = data;
        }

        static class Data{
            @SerializedName("hash")
            String songId;
            @SerializedName("timelength")
            int duration;
            @SerializedName("audio_name")
            String songName;
            @SerializedName("author_name")
            String singerName;
            @SerializedName("author_id")
            String singerId;
            @SerializedName("img")
            String picUrl;
            @SerializedName("album_name")
            String albumName;
            @SerializedName("album_id")
            String albumId;
            List<Authors> authors;

            public List<Authors> getAuthors() {
                return authors;
            }

            public void setAuthors(List<Authors> authors) {
                this.authors = authors;
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

            public String getSongId() {
                return songId;
            }

            public void setSongId(String songId) {
                this.songId = songId;
            }

            public int getDuration() {
                return duration;
            }

            public void setDuration(int duration) {
                this.duration = duration;
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

            public String getSingerId() {
                return singerId;
            }

            public void setSingerId(String singerId) {
                this.singerId = singerId;
            }

            public String getPicUrl() {
                return picUrl;
            }

            public void setPicUrl(String picUrl) {
                this.picUrl = picUrl;
            }

            static class Authors{
                @SerializedName("author_id")
                String singerId;
                @SerializedName("author_name")
                String singerName;

                public String getSingerId() {
                    return singerId;
                }

                public void setSingerId(String singerId) {
                    this.singerId = singerId;
                }

                public String getSingerName() {
                    return singerName;
                }

                public void setSingerName(String singerName) {
                    this.singerName = singerName;
                }
            }
        }
    }
}
