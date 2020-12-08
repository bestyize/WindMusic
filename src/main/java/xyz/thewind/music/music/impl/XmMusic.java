package xyz.thewind.music.music.impl;

import com.google.gson.annotations.SerializedName;
import xyz.thewind.music.model.SongBean;
import xyz.thewind.music.music.MusicHelper;
import xyz.thewind.music.util.GsonConverter;


import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;

import static xyz.thewind.music.util.HttpRequestHelper.downloadWebSiteUseGet;
import static xyz.thewind.music.util.Md5Encrypt.convertToMd5;
import static xyz.thewind.music.util.SongUtil.*;


public class XmMusic implements MusicHelper {
    @Override
    public List<SongBean> searchMusic(String keyword, int num) {
        String method = "mtop.alimusic.search.searchservice.searchsongs";
        String model = "\\\"model\\\":{\\\"key\\\":\\\"" + keyword.replaceAll("\"", "") + "\\\",\\\"pagingVO\\\":{\\\"page\\\":" + 1 + ",\\\"pageSize\\\":" + num + "}}";
        String response = xmHttpRequest(getXmUrl(method,model));
        return xmSongSearchRespProcess(response);
    }

    @Override
    public SongBean searchDownloadLink(SongBean songBean,String quality) {
        String method = "mtop.alimusic.music.songservice.getsongs";
        String model = "\\\"model\\\":{\\\"songIds\\\":[" + songBean.getSongId() + "]}";
        String response = xmHttpRequest(getXmUrl(method, model));
        List<SongBean> songBeanList=xmSongSearchRespProcess(response);
        if(songBeanList!=null&&songBeanList.size()>0){
            songBean.setDownloadLinkMap(songBeanList.get(0).getDownloadLinkMap());
        }
        return songBean;
    }


    @Override
    public List<SongBean> searchWithDownload(String keyword, int num) {
        return searchMusic(keyword,num);
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
     * @param response 请求结果
     * @return 解析的值
     */
    private List<SongBean> xmSongSearchRespProcess(String response){

        List<SongBean> list=new LinkedList<>();
        if(response.length()==0){
            return list;
        }
        try {
            SearchResponseBean xmSongRespBean= GsonConverter.fromJson(response,SearchResponseBean.class);
            if(xmSongRespBean!=null&&xmSongRespBean.data!=null&&xmSongRespBean.data.data!=null){
                List<SearchResponseBean.Data1.Data.SongDetail> songDetailList=xmSongRespBean.data.data.songDetailList;
                for (SearchResponseBean.Data1.Data.SongDetail detail:songDetailList){
                    Map<String,String> map=new HashMap<>();
                    for (SearchResponseBean.Data1.Data.SongDetail.ListenFiles files: detail.getListenFiles()){
                        map.put(files.getQuality(),files.getDownloadLink());
                    }
                    SongBean songBean=new SongBean(SRC_XM,
                            detail.getSongId(),
                            detail.getSongName(),
                            parseSingersId(detail.getSingerList()),
                            parseSingers(detail.getSingerList()),
                            String.valueOf(detail.getAlbumId()),
                            detail.getAlbumName(),
                            map.containsKey("SQ")?QUALITY_SQ:QUALITY_HQ,
                            detail.getAlbumPicUrl(),
                            detail.getLyricInfo().getLyricUrl(),
                            detail.getMvId()
                            );
                    songBean.setDownloadLinkMap(map);
                    list.add(songBean);
                }
            }
        }catch (Exception ignored){

        }

        return list;
    }

    private List<String> parseSingers(List<SearchResponseBean.Data1.Data.SongDetail.SingerVOs> singerList){
        List<String> singerIdList=new LinkedList<>();
        for (SearchResponseBean.Data1.Data.SongDetail.SingerVOs singer:singerList){
            singerIdList.add(singer.getSingerName());
        }
        return singerIdList;
    }

    private List<String> parseSingersId(List<SearchResponseBean.Data1.Data.SongDetail.SingerVOs> singerList){
        List<String> singerIdList=new LinkedList<>();
        for (SearchResponseBean.Data1.Data.SongDetail.SingerVOs singer:singerList){
            singerIdList.add(singer.getSingerId());
        }
        return singerIdList;
    }

    public String xmHttpRequest(String website) {
        String cookie= xmCookie.replaceAll("yize_uid", getUid());
        try {
            Map<String,String> headerMap=new HashMap<>();
            headerMap.put("User-Agent","Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.100 Safari/537.36");
            headerMap.put("Connection","Keep-Alive");
            headerMap.put("Cookie",cookie);
            headerMap.put("Cache-Control","no-cache");
            headerMap.put("Accept","/*");
            return downloadWebSiteUseGet(website,headerMap);
        } catch (Exception e) {
            return null;
        }
    }

    private static String xmToken = "";
    private static String xmCookie = "";

    private void updateXmToken() {
        //cna="mwGIFQabwA8CAdvoIVtV7N8b"; uidXM=14345449; _m_h5_tk=b9ee042ad4b616035d7864a2d41b8842_1560358851910; _m_h5_tk_enc=281d3d8188785edaf1b45e3f0f9fb19a
        long time = System.currentTimeMillis();
        String data = "{\"requestStr\":\"{\\\"header\\\":{\\\"platformId\\\":\\\"h5\\\",\\\"callId\\\":" + time + ",\\\"appVersion\\\":1000000,\\\"resolution\\\":\\\"600*1067\\\"},\\\"model\\\":{\\\"listId\\\":\\\"873784428\\\",\\\"isFullTags\\\":false,\\\"pagingVO\\\":{\\\"pageSize\\\":1000,\\\"page\\\":1}}}\"}";
        String signData = "" + "&" + time + "&23649156&" + data;
        String sign = convertToMd5(signData);
        String url = "https://h5api.m.xiami.com/h5/mtop.alimusic.music.list.collectservice.getcollectdetail/1.0/?jsv=2.4.0&appKey=23649156&t=" + time + "&sign=" + sign + "&api=mtop.alimusic.music.list.collectservice.getcollectdetail&v=1.0&type=originaljsonp&timeout=200000&dataType=originaljsonp&closeToast=true&callback=mtopjsonp1&data=%7B%22requestStr%22%3A%22%7B%5C%22header%5C%22%3A%7B%5C%22platformId%5C%22%3A%5C%22h5%5C%22%2C%5C%22callId%5C%22%3A" + time + "%2C%5C%22appVersion%5C%22%3A1000000%2C%5C%22resolution%5C%22%3A%5C%221920*375%5C%22%7D%2C%5C%22model%5C%22%3A%7B%5C%22listId%5C%22%3A%5C%22873784428%5C%22%2C%5C%22isFullTags%5C%22%3Afalse%2C%5C%22pagingVO%5C%22%3A%7B%5C%22pageSize%5C%22%3A1000%2C%5C%22page%5C%22%3A1%7D%7D%7D%22%7D";
        String cookie = getXiaMiCookie(url);
        if (cookie == null || cookie == "") {
            return;
        }
        cookie = cookie.replaceAll("\\[", "").replaceAll("\\]", "");
        String[] cookies = cookie.split(";");
        StringBuilder tempCookie = new StringBuilder();
        for (String c : cookies) {
            if (c.contains("_m_h5_tk=")) {
                xmToken = c.substring(c.indexOf("_m_h5_tk=") + "_m_h5_tk=".length(), c.lastIndexOf("_"));
            }
            tempCookie.append(c).append(";");
        }

        String cnaCookie = getXiaMiCookie("http://log.mmstat.com/eg.js");
        if (cnaCookie == null || cnaCookie.equals("")) {
            return;
        }
        cnaCookie = cnaCookie.replaceAll("\\[", "").replaceAll("\\]", "");
        String[] canCookies = cnaCookie.split(";");

        String cna = "";
        for (String c : canCookies) {
            if (c.contains("cna=")) {
                cna = c.replaceAll("cna=", "");
            }
        }
        xmCookie = tempCookie.toString().replaceAll("Path=/", "");
        //_xmCookie = "cna=\"" + cna + "\";uidXM=yize_uid;" + _xmCookie;
        xmCookie = "cna=" + cna + ";uidXM=yize_uid;" + xmCookie;
    }

    private String getXmUrl(String method, String model) {
        updateXmToken();
        String data;
        long time = System.currentTimeMillis();
        data = "{\"requestStr\":\"{\\\"header\\\":{\\\"platformId\\\":\\\"h5\\\",\\\"callId\\\":" + time + ",\\\"appVersion\\\":1000000,\\\"appId\\\":200,\\\"openId\\\":0,\\\"resolution\\\":\\\"2560x1440\\\"}," + model + "}\"}";
        String signData = xmToken + "&" + time + "&23649156&" + data;
        String sign = convertToMd5(signData);
        try {
            return "https://acs.m.xiami.com/h5/" + method + "/1.0/?jsv=2.4.0&appKey=23649156&t=" + time + "&sign=" + sign + "&v=1.0&AntiCreep=true&AntiFlood=true&type=originaljson&dataType=originaljsonp&api=" + method + "&data=" + URLEncoder.encode(data, "utf-8");
        } catch (Exception ignored) {
        }
        return "";
    }

    /**
     * 这里巨坑，发送https请求时，千万不要强行开代理，否则会报证书错误
     * @param website
     * @return
     */
    private String getXiaMiCookie(String website) {
        try {
            URL url = new URL(website);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(4000);
            conn.setReadTimeout(15000);
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.140 Safari/537.36 Edge/18.17763");
            conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            Map<String, List<String>> cookie = conn.getHeaderFields();
            conn.disconnect();
            return cookie.get("Set-Cookie").toString();
        } catch (Exception e) {
            System.out.println(e.getCause().toString());
            return "";
        }
    }

    private String getUid() {
        Random ran = new Random();
        return String.valueOf(6000000 + ran.nextInt(6000000));
    }

    private static class SearchResponseBean{
        String api;
        Data1 data;

        public String getApi() {
            return api;
        }

        public void setApi(String api) {
            this.api = api;
        }

        public Data1 getData() {
            return data;
        }

        public void setData(Data1 data) {
            this.data = data;
        }

        static class Data1{
            Data data;

            public Data getData() {
                return data;
            }

            public void setData(Data data) {
                this.data = data;
            }

            static class Data{
                @SerializedName("songs")
                List<SongDetail> songDetailList;

                public List<SongDetail> getSongs() {
                    return songDetailList;
                }

                public void setSongs(List<SongDetail> songDetailList) {
                    this.songDetailList = songDetailList;
                }

                static class SongDetail{
                    String songId;
                    String songName;
                    long albumId;
                    @SerializedName("albumLogo")
                    String albumPicUrl;
                    String albumName;
                    int albumSongCount;
                    @SerializedName("artistId")
                    long singerId;
                    @SerializedName("artistLogo")
                    String singerPic;
                    List<ListenFiles> listenFiles;
                    LyricInfo lyricInfo;
                    String mvId;
                    @SerializedName("singerVOs")
                    List<SingerVOs> singerList;
                    static class SingerVOs{
                        @SerializedName("artistStringId")
                        String singerId;
                        @SerializedName("artistLogo")
                        String singerPic;
                        @SerializedName("artistName")
                        String singerName;
                        String gender;

                        public String getSingerId() {
                            return singerId;
                        }

                        public void setSingerId(String singerId) {
                            this.singerId = singerId;
                        }

                        public String getSingerPic() {
                            return singerPic;
                        }

                        public void setSingerPic(String singerPic) {
                            this.singerPic = singerPic;
                        }

                        public String getSingerName() {
                            return singerName;
                        }

                        public void setSingerName(String singerName) {
                            this.singerName = singerName;
                        }

                        public String getGender() {
                            return gender;
                        }

                        public void setGender(String gender) {
                            this.gender = gender;
                        }
                    }

                    static class ListenFiles{

                        long fileSize;
                        String format;
                        @SerializedName("length")
                        long timeSpan;
                        @SerializedName("listenFile")
                        String downloadLink;
                        String quality;

                        public long getFileSize() {
                            return fileSize;
                        }

                        public void setFileSize(long fileSize) {
                            this.fileSize = fileSize;
                        }

                        public String getFormat() {
                            return format;
                        }

                        public void setFormat(String format) {
                            this.format = format;
                        }

                        public long getTimeSpan() {
                            return timeSpan;
                        }

                        public void setTimeSpan(long timeSpan) {
                            this.timeSpan = timeSpan;
                        }

                        public String getDownloadLink() {
                            return downloadLink;
                        }

                        public void setDownloadLink(String downloadLink) {
                            this.downloadLink = downloadLink;
                        }

                        public String getQuality() {
                            if(qualityMap==null){
                                qualityMap=new HashMap<>();
                                qualityMap.put("e","LQ");
                                qualityMap.put("f","LQ");
                                qualityMap.put("l","PQ");
                                qualityMap.put("h","HQ");
                                qualityMap.put("s","SQ");
                            }
                            return qualityMap.get(quality);
                        }

                        public void setQuality(String quality) {
                            this.quality = quality;
                        }
                    }

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

                    public long getAlbumId() {
                        return albumId;
                    }

                    public void setAlbumId(long albumId) {
                        this.albumId = albumId;
                    }

                    public String getAlbumPicUrl() {
                        return albumPicUrl;
                    }

                    public void setAlbumPicUrl(String albumPicUrl) {
                        this.albumPicUrl = albumPicUrl;
                    }

                    public String getAlbumName() {
                        return albumName;
                    }

                    public void setAlbumName(String albumName) {
                        this.albumName = albumName;
                    }

                    public int getAlbumSongCount() {
                        return albumSongCount;
                    }

                    public void setAlbumSongCount(int albumSongCount) {
                        this.albumSongCount = albumSongCount;
                    }

                    public long getSingerId() {
                        return singerId;
                    }

                    public void setSingerId(long singerId) {
                        this.singerId = singerId;
                    }

                    public String getSingerPic() {
                        return singerPic;
                    }

                    public void setSingerPic(String singerPic) {
                        this.singerPic = singerPic;
                    }

                    public List<ListenFiles> getListenFiles() {
                        return listenFiles;
                    }

                    public void setListenFiles(List<ListenFiles> listenFiles) {
                        this.listenFiles = listenFiles;
                    }

                    public LyricInfo getLyricInfo() {
                        return lyricInfo;
                    }

                    public void setLyricInfo(LyricInfo lyricInfo) {
                        this.lyricInfo = lyricInfo;
                    }

                    public String getMvId() {
                        return mvId;
                    }

                    public void setMvId(String mvId) {
                        this.mvId = mvId;
                    }

                    public List<SingerVOs> getSingerList() {
                        return singerList;
                    }

                    public void setSingerList(List<SingerVOs> singerList) {
                        this.singerList = singerList;
                    }

                    static class LyricInfo{
                        @SerializedName("lyricFile")
                        String lyricUrl;
                        String lyricId;

                        public String getLyricUrl() {
                            return lyricUrl;
                        }

                        public void setLyricUrl(String lyricUrl) {
                            this.lyricUrl = lyricUrl;
                        }

                        public String getLyricId() {
                            return lyricId;
                        }

                        public void setLyricId(String lyricId) {
                            this.lyricId = lyricId;
                        }
                    }

                }
            }

        }
    }
    private static Map<String,String> qualityMap;
}
