package xyz.thewind.music.music.impl;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.thewind.music.model.SongBean;
import xyz.thewind.music.music.MusicHelper;
import xyz.thewind.music.util.GsonConverter;
import xyz.thewind.music.util.PropertyReader;

import java.net.URLEncoder;
import java.util.*;

import static xyz.thewind.music.util.HttpRequestHelper.downloadWebSiteUseGet;
import static xyz.thewind.music.util.HttpRequestHelper.downloadWebSiteUsePost;
import static xyz.thewind.music.util.SongUtil.*;


public class QqMusic implements MusicHelper {
    private static final Logger logger= LogManager.getLogger(QqMusic.class);
    private static final HashMap<String, String> qualityMap;
    private static final String baseDownloadServer="http://ws.stream.qqmusic.qq.com/";

    private static final Map<String,String> configMap=new HashMap<>();

    static {
        qualityMap=new HashMap<>();
        qualityMap.put(QUALITY_SQ,"F000songid.flac");
        qualityMap.put(QUALITY_HQ,"M800songid.mp3");
        qualityMap.put(QUALITY_PQ,"M500songid.mp3");
        qualityMap.put(QUALITY_LQ,"C400songid.m4a");
    }

    @Override
    public void setConfigMap(Map<String, String> configMaps) {
        configMap.putAll(configMaps);
    }

    @Override
    public List<SongBean> searchMusic(String keyword, int num) {
        String response=qqSearchRequest(keyword,num);
        return qqSongSearchRespProcess(response);
    }

    @Override
    public SongBean searchDownloadLink(SongBean songBean, String quality) {
        String downloadLink=getDownloadLinkBySongId(songBean.getSongId(),quality);
        songBean.getDownloadLinkMap().put(quality,downloadLink);
        return songBean;
    }

    @Override
    public List<SongBean> searchDownloadLink(List<SongBean> downloadInfoList) {
        //List<DownloadInfo> downloadInfoList
        return getDownloadLinkBySongIdList(downloadInfoList);
    }

    @Override
    public List<SongBean> searchWithDownload(String keyword, int num) {
        List<SongBean> songBeanList=searchMusic(keyword,num);
        for (SongBean bean:songBeanList){
            searchDownloadLink(bean,QUALITY_SQ);
            searchDownloadLink(bean,QUALITY_HQ);           
            searchDownloadLink(bean,QUALITY_LQ);
            searchDownloadLink(bean,QUALITY_PQ);
        }
        return songBeanList;
    }

    private static String downloadLinkRequestParamsBuilder(List<SongBean> downloadInfoList) {
        String qqUin= configMap.getOrDefault("qqmusic_uin","0");
        DownloadRequestBean requestBean=new DownloadRequestBean();
        requestBean.queryvkey.param.setUin(qqUin);
        List<String> filenames=new LinkedList<>();
        List<String> musicfiles=new LinkedList<>();
        List<String> songmid=new LinkedList<>();
        List<Integer> songtype=new LinkedList<>();
        for (SongBean info:downloadInfoList){
            filenames.add(qualityMap.get(info.getQuality()).replace("songid",info.getSongId()));
            musicfiles.add(qualityMap.get(info.getQuality()).replace("songid",info.getSongId()));
            songmid.add(info.getSongId());
            songtype.add(1);
        }
        requestBean.queryvkey.param.setFilename(filenames);
        requestBean.queryvkey.param.setMusicfile(musicfiles);
        requestBean.queryvkey.param.setSongmid(songmid);
        requestBean.queryvkey.param.setSongtype(songtype);
        return new Gson().toJson(requestBean);
    }

    private String qqSearchRequest(String keyword, int num){
        String response="";
        try {
            String link="https://c.y.qq.com/soso/fcgi-bin/client_search_cp?p=1&n="+num+"&w="+ URLEncoder.encode(keyword,"utf-8") +"&format=json";
            Map<String,String> headerMap=new HashMap<>();
            headerMap.put("User-Agent",USER_AGENT_ANDROID);
            response=downloadWebSiteUseGet(link,headerMap);
        }catch (Exception e){
            logger.error("encode failed");
        }
        return response;

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
    private List<SongBean> qqSongSearchRespProcess(String response) {
        List<SongBean> list = new LinkedList<>();
        if (response.length() == 0) {
            return list;
        }
        try {
            SearchResponseBean searchResponseBean= GsonConverter.fromJson(response, SearchResponseBean.class);
            if(searchResponseBean.getCode()==0){
                List<SearchResponseBean.Data.Song.SongDetail> songDetailList=searchResponseBean.data.song.songDetailList;
                for (SearchResponseBean.Data.Song.SongDetail detail:songDetailList){
                    SongBean songBean=new SongBean(SRC_QQ,
                            detail.getSongId(),
                            detail.getSongName(),
                            parseSingersId(detail.getSingers()),
                            parseSingers(detail.getSingers()),
                            detail.getAlbumId(),
                            detail.getAlbumName(),
                            detail.getSqSize()>0?QUALITY_SQ:QUALITY_HQ,
                            "",
                            "",
                            detail.getMvId());
                    list.add(songBean);
                }
            }
        }catch (Exception ignored){

        }
        return list;
    }

    private List<String> parseSingers(List<SearchResponseBean.Data.Song.SongDetail.Singer> singers){
        List<String> singerList=new LinkedList<>();
        for (SearchResponseBean.Data.Song.SongDetail.Singer singer:singers){
            singerList.add(singer.getSingerName());
        }
        return singerList;
    }

    private List<String> parseSingersId(List<SearchResponseBean.Data.Song.SongDetail.Singer> singers){
        List<String> singerIdList=new LinkedList<>();
        for (SearchResponseBean.Data.Song.SongDetail.Singer singer:singers){
            singerIdList.add(singer.getSingerId());
        }
        return singerIdList;
    }

    public static String getDownloadLinkBySongId(String songid,String quality){
        SongBean downloadInfo=new SongBean(SRC_QQ,songid,quality);
        List<SongBean> downloadInfoList=new LinkedList<>();
        downloadInfoList.add(downloadInfo);
        String requestParams=downloadLinkRequestParamsBuilder(downloadInfoList);
        String response=downloadRequest(requestParams);
        String downloadLink="";
        DownloadBean downloadBean=GsonConverter.fromJson(response,DownloadBean.class);
        if(downloadBean!=null&&downloadBean.data!=null&&downloadBean.data.data.songDetailList.size()>0){
            List<DownloadBean.Data1.Data.SongDetail> songDetailList=downloadBean.data.data.songDetailList;
            DownloadBean.Data1.Data.SongDetail songDetail=songDetailList.get(0);
            downloadLink=songDetail.getDownloadLink();
        }
        return downloadLink;


    }

    public static List<SongBean> getDownloadLinkBySongIdList(List<SongBean> downloadInfoList){
        String requestParams=downloadLinkRequestParamsBuilder(downloadInfoList);
        String response=downloadRequest(requestParams);
        DownloadBean downloadBean=GsonConverter.fromJson(response,DownloadBean.class);
        if(downloadBean!=null&&downloadBean.data!=null&&downloadBean.data.data.songDetailList.size()>0){
            List<DownloadBean.Data1.Data.SongDetail> songDetailList=downloadBean.data.data.songDetailList;
            for (int i=0;i<songDetailList.size();i++){
                DownloadBean.Data1.Data.SongDetail detail= songDetailList.get(i);
                downloadInfoList.get(i).getDownloadLinkMap().put(downloadInfoList.get(i).getQuality(),detail.getDownloadLink());
            }
        }
        return downloadInfoList;


    }

    private static String downloadRequest(String params){
        String qqUin=configMap.getOrDefault("qqmusic_uin","0");
        String qqKey=configMap.getOrDefault("qqmusic_key","0");
        String qqCookie="qqmusic_key="+qqKey+"qqmusic_uin="+qqUin;
        Map<String,String> headerMap=new HashMap<>();
        headerMap.put("User-Agent","Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0)");
        headerMap.put("Host","u.y.qq.com");
        headerMap.put("Connection","Keep-Alive");
        headerMap.put("Cookie",qqCookie);
        return downloadWebSiteUsePost("https://u.y.qq.com/cgi-bin/musicu.fcg",params,headerMap);
    }


    private static class SearchResponseBean{
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
            String keyword;
            Song song;

            public String getKeyword() {
                return keyword;
            }

            public void setKeyword(String keyword) {
                this.keyword = keyword;
            }

            public Song getSong() {
                return song;
            }

            public void setSong(Song song) {
                this.song = song;
            }

            class Song{
                @SerializedName("curnum")
                int curNum;
                @SerializedName("curpage")
                int curPage;
                @SerializedName("list")
                List<SongDetail> songDetailList;

                public int getCurNum() {
                    return curNum;
                }

                public void setCurNum(int curNum) {
                    this.curNum = curNum;
                }

                public int getCurPage() {
                    return curPage;
                }

                public void setCurPage(int curPage) {
                    this.curPage = curPage;
                }

                public List<SongDetail> getSongDetailList() {
                    return songDetailList;
                }

                public void setSongDetailList(List<SongDetail> songDetailList) {
                    this.songDetailList = songDetailList;
                }

                class SongDetail{
                    @SerializedName("songmid")
                    String songId;
                    @SerializedName("songname")
                    String songName;
                    @SerializedName("vid")
                    String mvId;
                    @SerializedName("albummid")
                    String albumId;
                    @SerializedName("albumname")
                    String albumName;
                    @SerializedName("singer")
                    List<Singer> singers;
                    @SerializedName("sizeogg")
                    long lqSize;
                    @SerializedName("size128")
                    long pqSize;
                    @SerializedName("size320")
                    long hqSize;
                    @SerializedName("sizeflac")
                    long sqSize;
                    @SerializedName("interval")
                    long duration;

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

                    public String getMvId() {
                        return mvId;
                    }

                    public void setMvId(String mvId) {
                        this.mvId = mvId;
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

                    public List<Singer> getSingers() {
                        return singers;
                    }

                    public void setSingers(List<Singer> singers) {
                        this.singers = singers;
                    }

                    public long getLqSize() {
                        return lqSize;
                    }

                    public void setLqSize(long lqSize) {
                        this.lqSize = lqSize;
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

                    public long getDuration() {
                        return duration;
                    }

                    public void setDuration(long duration) {
                        this.duration = duration;
                    }

                    class Singer{
                        @SerializedName("mid")
                        String singerId;
                        @SerializedName("name")
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
    }

    private static class DownloadBean{
        int code;
        @SerializedName("queryvkey")
        Data1 data;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }
        static class Data1{
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
                long expiration;
                @SerializedName("midurlinfo")
                List<SongDetail> songDetailList;

                public long getExpiration() {
                    return expiration;
                }

                public void setExpiration(long expiration) {
                    this.expiration = expiration;
                }

                public List<SongDetail> getSongDetailList() {
                    return songDetailList;
                }

                public void setSongDetailList(List<SongDetail> songDetailList) {
                    this.songDetailList = songDetailList;
                }

                class SongDetail{
                    @SerializedName("filename")
                    String fileName;
                    @SerializedName("purl")
                    String downloadLink;
                    @SerializedName("songmid")
                    String songId;

                    public String getFileName() {
                        return fileName;
                    }

                    public void setFileName(String fileName) {
                        this.fileName = fileName;
                    }

                    public String getDownloadLink() {
                        return baseDownloadServer+downloadLink;
                    }

                    public void setDownloadLink(String downloadLink) {
                        this.downloadLink = downloadLink;
                    }
                }
            }
        }




    }

    private static class DownloadRequestBean{
        Queryvkey queryvkey=new Queryvkey();

        public Queryvkey getQueryvkey() {
            return queryvkey;
        }

        public void setQueryvkey(Queryvkey queryvkey) {
            this.queryvkey = queryvkey;
        }

        class Queryvkey{
            String method="CgiGetVkey";
            String module="vkey.GetVkeyServer";
            Param param=new Param();

            public String getMethod() {
                return method;
            }

            public void setMethod(String method) {
                this.method = method;
            }

            public String getModule() {
                return module;
            }

            public void setModule(String module) {
                this.module = module;
            }
            class Param{
                int checklimit=0;
                int ctx=1;
                int downloadfrom=0;
                List<String > filename;
                String guid="0";
                List<String> musicfile;
                String nettype="";
                String referer="y.qq.com";
                int scene=0;
                List<String> songmid;
                List<Integer> songtype;
                String uin;

                public int getChecklimit() {
                    return checklimit;
                }

                public void setChecklimit(int checklimit) {
                    this.checklimit = checklimit;
                }

                public int getCtx() {
                    return ctx;
                }

                public void setCtx(int ctx) {
                    this.ctx = ctx;
                }

                public int getDownloadfrom() {
                    return downloadfrom;
                }

                public void setDownloadfrom(int downloadfrom) {
                    this.downloadfrom = downloadfrom;
                }

                public List<String> getFilename() {
                    return filename;
                }

                public void setFilename(List<String> filename) {
                    this.filename = filename;
                }

                public String getGuid() {
                    return guid;
                }

                public void setGuid(String guid) {
                    this.guid = guid;
                }

                public List<String> getMusicfile() {
                    return musicfile;
                }

                public void setMusicfile(List<String> musicfile) {
                    this.musicfile = musicfile;
                }

                public String getNettype() {
                    return nettype;
                }

                public void setNettype(String nettype) {
                    this.nettype = nettype;
                }

                public String getReferer() {
                    return referer;
                }

                public void setReferer(String referer) {
                    this.referer = referer;
                }

                public int getScene() {
                    return scene;
                }

                public void setScene(int scene) {
                    this.scene = scene;
                }

                public List<String> getSongmid() {
                    return songmid;
                }

                public void setSongmid(List<String> songmid) {
                    this.songmid = songmid;
                }

                public List<Integer> getSongtype() {
                    return songtype;
                }

                public void setSongtype(List<Integer> songtype) {
                    this.songtype = songtype;
                }

                public String getUin() {
                    return uin;
                }

                public void setUin(String uin) {
                    this.uin = uin;
                }
            }
        }
    }
}
