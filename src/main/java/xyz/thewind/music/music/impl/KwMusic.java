package xyz.thewind.music.music.impl;

import com.google.gson.annotations.SerializedName;
import xyz.thewind.music.model.SongBean;
import xyz.thewind.music.music.MusicHelper;
import xyz.thewind.music.util.GsonConverter;
import xyz.thewind.music.util.HttpRequestHelper;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

import static xyz.thewind.music.util.HttpRequestHelper.downloadWebSiteUseGet;
import static xyz.thewind.music.util.SongUtil.*;


public class KwMusic implements MusicHelper {
    private static final String SECRET_KEY = "ylzsxkwm";
    private static final HashMap<String, String> qualityMap;

    static {
        qualityMap=new HashMap<>();
        qualityMap.put(QUALITY_SQ,"&br=2000&format=flac");
        qualityMap.put(QUALITY_HQ,"&br=320&format=mp3");
        qualityMap.put(QUALITY_PQ,"&br=192&format=mp3");
        qualityMap.put(QUALITY_LQ,"&br=128&format=mp3");
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
                            detail.picUrl,"","");
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
     */

    private String getDownloadLinkById(String songid, String quality) {
        String downloadLink=null;
        try {
            String link = "http://nmobi.kuwo.cn/mobi.s?f=kuwo&q=";
            String encParams = base64Encrypt("&type=convert_url2" + qualityMap.get(quality) + "&sig=0&rid=" + songid + "&network=wifi");
            Map<String,String> headerMap=new HashMap<>();
            headerMap.put("User-Agent",USER_AGENT_ANDROID);
            String response= downloadWebSiteUseGet(link + encParams,headerMap);
            downloadLink= response.substring(response.indexOf("url=") + 4, response.lastIndexOf("sig="));
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

    /**
     * 酷我音乐解密算法
     */


    private static long bitTransform(long[] arr, int n, long l) {
        long l2 = 0;
        for (int i = 0; i < n; i++) {
            if (arr[i] < 0 || (l & arrayMask[(int) arr[i]]) == 0) {
                continue;
            }
            l2 |= arrayMask[i];
        }
        return l2;
    }

    private static long des64(long[] longs, long l) {
        long out;
        long SOut ;
        long[] pR = {0, 0, 0, 0, 0, 0, 0, 0};
        long[] pSource = {0, 0};
        int sbi ;
        long L;
        long R;
        out = bitTransform(arrayIP, 64, l);
        pSource[0] = 4294967295L & out;
        pSource[1] = (-4294967296L & out) >> 32;
        for (int i = 0; i < 16; i++) {
            R = pSource[1];
            R = bitTransform(arrayE, 64, R);
            R ^= longs[i];
            for (int j = 0; j < 8; j++) {
                pR[j] = 255 & R >> j * 8;
            }

            SOut = 0;
            for (sbi = 7; sbi > -1; sbi--) {
                SOut <<= 4;
                SOut |= matrixNSBox[sbi][(int) pR[sbi]];
            }
            R = bitTransform(arrayP, 32, SOut);
            L = pSource[0];
            pSource[0] = pSource[1];
            pSource[1] = L ^ R;

        }
        pSource = reserveArr(pSource);
        out = (-4294967296L) & pSource[1] << 32 | 4294967295L & pSource[0];
        out = bitTransform(arrayIP_1, 64, out);
        return out;
    }

    private static void subKeys(long l, long[] longs, long n) {
        long l2 = bitTransform(arrayPC_1, 56, l);
        for (int i = 0; i < 16; i++) {
            l2 = ((l2 & arrayLsMask[(int) arrayLs[i]]) << 28 -
                    arrayLs[i] | (l2 & ~arrayLsMask[(int) arrayLs[i]]) >> arrayLs[i]);
            longs[i] = bitTransform(arrayPC_2, 64, l2);
        }
        int j = 0;

        while (n == 1 && j < 8) {
            longs = replace(longs, j, 15 - j);
            j += 1;
        }

    }

    private static long[] encrypt(String msg) {
        byte[] msg2 = msg.getBytes();
        String msg3 = new String(msg2);
        long l = 7887891437440363641L;
        int j = (msg.getBytes().length) / 8;
        long[] arrLong1 = ab(16);
        subKeys(l, arrLong1, 0);
        long[] arrLong2 = ab(j);

        for (int m = 0; m < j; m++) {
            for (int n = 0; n < 8; n++) {
                arrLong2[m] |= (long) msg2[n + m * 8] << n * 8;
            }
        }

        long[] arrLong3 = ab((1 + 8 * (j + 1)) / 8);

        for (int i1 = 0; i1 < j; i1++) {
            arrLong3[i1] = des64(arrLong1, arrLong2[i1]);
        }

        byte[] arrByte1 = msg3.substring(j * 8).getBytes();
        long l2 = 0;
        for (int i1 = 0; i1 < msg2.length % 8; i1++) {
            l2 |= (long) arrByte1[i1] << i1 * 8;
        }
        arrLong3[j] = des64(arrLong1, l2);
        long[] arrByte2 = ab((8 * arrLong3.length));
        int i4 = 0;

        for (long value : arrLong3) {
            for (int i6 = 0; i6 < 8; i6++) {
                arrByte2[i4] = (255 & value >> i6 * 8);

                i4 += 1;
            }
        }

        return arrByte2;
    }

    private static String base64Encrypt(String msg) {
        long[] b1 = encrypt(msg);
        byte[] b2 = longTobyte(b1);
        return Base64.getEncoder().encodeToString(b2);
    }

    private static long[] reserveArr(long[] array) {
        long[] newArray = new long[array.length];
        for (int i = 0; i < array.length; i++) {
            newArray[i] = array[array.length - i - 1];

        }
        return newArray;
    }

    private static long[] replace(long[] array, int start, int end) {
        long[] newArr = new long[array.length];
        for (int i = 0; i < array.length; i++) {
            if (i == start) {
                newArr[i] = array[end];
            } else if (i == end) {
                newArr[i] = array[start];
            } else {
                newArr[i] = array[i];
            }
        }
        return newArr;
    }

    private static long[] ab(int number) {
        long[] array = new long[number];
        for (int i = 0; i < number; i++) {
            array[i] = 0;
        }
        return array;
    }

    private static byte[] longTobyte(long[] longs) {
        byte[] b = new byte[longs.length];
        for (int i = 0; i < b.length; i++) {
            b[i] = (byte) longs[i];
        }
        return b;
    }

    private static final long[] arrayE = {
            31, 0, 1, 2, 3, 4, -1, -1,
            3, 4, 5, 6, 7, 8, -1, -1,
            7, 8, 9, 10, 11, 12, -1, -1,
            11, 12, 13, 14, 15, 16, -1, -1,
            15, 16, 17, 18, 19, 20, -1, -1,
            19, 20, 21, 22, 23, 24, -1, -1,
            23, 24, 25, 26, 27, 28, -1, -1,
            27, 28, 29, 30, 31, 30, -1, -1,
    };

    private static final long[] arrayIP = {
            57, 49, 41, 33, 25, 17, 9, 1,
            59, 51, 43, 35, 27, 19, 11, 3,
            61, 53, 45, 37, 29, 21, 13, 5,
            63, 55, 47, 39, 31, 23, 15, 7,
            56, 48, 40, 32, 24, 16, 8, 0,
            58, 50, 42, 34, 26, 18, 10, 2,
            60, 52, 44, 36, 28, 20, 12, 4,
            62, 54, 46, 38, 30, 22, 14, 6,
    };

    private static final long[] arrayIP_1 = {
            39, 7, 47, 15, 55, 23, 63, 31,
            38, 6, 46, 14, 54, 22, 62, 30,
            37, 5, 45, 13, 53, 21, 61, 29,
            36, 4, 44, 12, 52, 20, 60, 28,
            35, 3, 43, 11, 51, 19, 59, 27,
            34, 2, 42, 10, 50, 18, 58, 26,
            33, 1, 41, 9, 49, 17, 57, 25,
            32, 0, 40, 8, 48, 16, 56, 24,
    };

    private static final long[] arrayLs = {1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1};

    private static final long[] arrayLsMask = {0, 0x100001, 0x300003};

    private static final long[] arrayMask = {1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048, 4096,
            8192, 16384, 32768, 65536, 131072, 262144, 524288, 1048576, 2097152, 4194304, 8388608, 16777216, 33554432,
            67108864, 134217728, 268435456, 536870912, 1073741824, (long) 2 << (31 - 1), (long) 2 << (32 - 1), (long) 2 << (33 - 1), (long) 2 << (34 - 1), (long) 2 << (35 - 1),
            (long) 2 << (36 - 1), (long) 2 << (37 - 1), (long) 2 << (38 - 1), (long) 2 << (39 - 1), (long) 2 << (40 - 1), (long) 2 << (41 - 1), (long) 2 << (42 - 1), (long) 2 << (43 - 1), (long) 2 << (44 - 1),
            (long) 2 << (45 - 1), (long) 2 << (46 - 1), (long) 2 << (47 - 1), (long) 2 << (48 - 1), (long) 2 << (49 - 1), (long) 2 << (50 - 1), (long) 2 << (51 - 1), (long) 2 << (52 - 1),
            (long) 2 << (53 - 1), (long) 2 << (54 - 1), (long) 2 << (55 - 1), (long) 2 << (56 - 1), (long) 2 << (57 - 1), (long) 2 << (58 - 1), (long) 2 << (59 - 1), (long) 2 << (60 - 1),
            (long) 2 << (61 - 1), (long) 2 << (62 - 1), (long) 2 << (63 - 1)};


    private static final long[] arrayP = {
            15, 6, 19, 20, 28, 11, 27, 16,
            0, 14, 22, 25, 4, 17, 30, 9,
            1, 7, 23, 13, 31, 26, 2, 8,
            18, 12, 29, 5, 21, 10, 3, 24,
    };

    private static final long[] arrayPC_1 = {
            56, 48, 40, 32, 24, 16, 8, 0,
            57, 49, 41, 33, 25, 17, 9, 1,
            58, 50, 42, 34, 26, 18, 10, 2,
            59, 51, 43, 35, 62, 54, 46, 38,
            30, 22, 14, 6, 61, 53, 45, 37,
            29, 21, 13, 5, 60, 52, 44, 36,
            28, 20, 12, 4, 27, 19, 11, 3,
    };

    private static final long[] arrayPC_2 = {
            13, 16, 10, 23, 0, 4, -1, -1,
            2, 27, 14, 5, 20, 9, -1, -1,
            22, 18, 11, 3, 25, 7, -1, -1,
            15, 6, 26, 19, 12, 1, -1, -1,
            40, 51, 30, 36, 46, 54, -1, -1,
            29, 39, 50, 44, 32, 47, -1, -1,
            43, 48, 38, 55, 33, 52, -1, -1,
            45, 41, 49, 35, 28, 31, -1, -1,
    };

    private static final long[][] matrixNSBox = {{
            14, 4, 3, 15, 2, 13, 5, 3,
            13, 14, 6, 9, 11, 2, 0, 5,
            4, 1, 10, 12, 15, 6, 9, 10,
            1, 8, 12, 7, 8, 11, 7, 0,
            0, 15, 10, 5, 14, 4, 9, 10,
            7, 8, 12, 3, 13, 1, 3, 6,
            15, 12, 6, 11, 2, 9, 5, 0,
            4, 2, 11, 14, 1, 7, 8, 13,}, {
            15, 0, 9, 5, 6, 10, 12, 9,
            8, 7, 2, 12, 3, 13, 5, 2,
            1, 14, 7, 8, 11, 4, 0, 3,
            14, 11, 13, 6, 4, 1, 10, 15,
            3, 13, 12, 11, 15, 3, 6, 0,
            4, 10, 1, 7, 8, 4, 11, 14,
            13, 8, 0, 6, 2, 15, 9, 5,
            7, 1, 10, 12, 14, 2, 5, 9,}, {
            10, 13, 1, 11, 6, 8, 11, 5,
            9, 4, 12, 2, 15, 3, 2, 14,
            0, 6, 13, 1, 3, 15, 4, 10,
            14, 9, 7, 12, 5, 0, 8, 7,
            13, 1, 2, 4, 3, 6, 12, 11,
            0, 13, 5, 14, 6, 8, 15, 2,
            7, 10, 8, 15, 4, 9, 11, 5,
            9, 0, 14, 3, 10, 7, 1, 12,}, {
            7, 10, 1, 15, 0, 12, 11, 5,
            14, 9, 8, 3, 9, 7, 4, 8,
            13, 6, 2, 1, 6, 11, 12, 2,
            3, 0, 5, 14, 10, 13, 15, 4,
            13, 3, 4, 9, 6, 10, 1, 12,
            11, 0, 2, 5, 0, 13, 14, 2,
            8, 15, 7, 4, 15, 1, 10, 7,
            5, 6, 12, 11, 3, 8, 9, 14,}, {
            2, 4, 8, 15, 7, 10, 13, 6,
            4, 1, 3, 12, 11, 7, 14, 0,
            12, 2, 5, 9, 10, 13, 0, 3,
            1, 11, 15, 5, 6, 8, 9, 14,
            14, 11, 5, 6, 4, 1, 3, 10,
            2, 12, 15, 0, 13, 2, 8, 5,
            11, 8, 0, 15, 7, 14, 9, 4,
            12, 7, 10, 9, 1, 13, 6, 3,}, {
            12, 9, 0, 7, 9, 2, 14, 1,
            10, 15, 3, 4, 6, 12, 5, 11,
            1, 14, 13, 0, 2, 8, 7, 13,
            15, 5, 4, 10, 8, 3, 11, 6,
            10, 4, 6, 11, 7, 9, 0, 6,
            4, 2, 13, 1, 9, 15, 3, 8,
            15, 3, 1, 14, 12, 5, 11, 0,
            2, 12, 14, 7, 5, 10, 8, 13,}, {
            4, 1, 3, 10, 15, 12, 5, 0,
            2, 11, 9, 6, 8, 7, 6, 9,
            11, 4, 12, 15, 0, 3, 10, 5,
            14, 13, 7, 8, 13, 14, 1, 2,
            13, 6, 14, 9, 4, 1, 2, 14,
            11, 13, 5, 0, 1, 10, 8, 3,
            0, 11, 3, 5, 9, 4, 15, 2,
            7, 8, 12, 15, 10, 7, 6, 12,}, {
            13, 7, 10, 0, 6, 9, 5, 15,
            8, 4, 3, 10, 11, 14, 12, 5,
            2, 11, 9, 6, 15, 12, 0, 3,
            4, 1, 14, 13, 1, 2, 7, 8,
            1, 2, 12, 15, 10, 4, 0, 3,
            13, 14, 6, 9, 7, 8, 9, 6,
            15, 1, 5, 12, 3, 10, 14, 5,
            8, 7, 11, 0, 4, 13, 2, 11,},
    };

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


}
