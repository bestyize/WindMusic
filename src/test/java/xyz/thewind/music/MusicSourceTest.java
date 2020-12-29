package xyz.thewind.music;

import org.junit.Test;
import xyz.thewind.music.model.SongBean;
import xyz.thewind.music.music.MusicDispatcher;
import xyz.thewind.music.music.MusicHelper;
import xyz.thewind.music.music.impl.kg.KgMusic;
import xyz.thewind.music.music.impl.kw.KwMusic;
import xyz.thewind.music.music.impl.mg.MgMusic;
import xyz.thewind.music.music.impl.qq.QqMusic;
import xyz.thewind.music.music.impl.wy.WyMusic;
import xyz.thewind.music.music.impl.xm.XmMusic;
import xyz.thewind.music.util.GsonConverter;
import xyz.thewind.music.util.HttpRequestHelper;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static xyz.thewind.music.util.HttpRequestHelper.downloadWebSiteUseGet;
import static xyz.thewind.music.util.Md5Encrypt.convertToMd5;
import static xyz.thewind.music.music.common.SongUtil.*;

public class MusicSourceTest {
    MusicDispatcher dispatcher=new MusicDispatcher();
    @Test
    public void test(){

        List<SongBean> songBeanList=dispatcher.searchDownDispatcher("MG","周杰伦",20);
        System.out.println(GsonConverter.toJson(songBeanList));
    }
    @Test
    public void cloudMusicTest(){
        List<SongBean> songBean=dispatcher.searchDownDispatcher("QQ","周杰伦",5);
        System.out.println(GsonConverter.toJson(songBean));
    }


    @Test
    public void cloudMusicTest1() throws UnsupportedEncodingException {
        List<String> ids=new ArrayList<>();
        ids.add("478303470");
        System.out.println(getSongUrl(ids,"SQ"));
    }

    public String getSongUrl(List<String> ids,String quality) throws UnsupportedEncodingException {
        int br=999000;
        String url="http://music.163.com/api/song/enhance/player/url?ids="+ URLEncoder.encode(ids.toString(),"UTF-8")+"&br="+br;
        System.out.println(url);
        Map<String,String> headerMap=new HashMap<>();
        headerMap.put("Cookie","MUSIC_U=ba0a04e302da6a12399d1817fe4eae9dd7c6b7f4865bf4fd9b986b5c8c07893333a649814e309366;");
        return HttpRequestHelper.downloadWebSiteUseGet(url,headerMap);
    }
    @Test
    public void qqmusicLyricTest(){
        MusicHelper helper1=new QqMusic();
        System.out.println(helper1.getLyric("0039MnYb0qxYhV"));
        MusicHelper helper2=new WyMusic();
        System.out.println(helper2.getLyric("65766"));
        MusicHelper helper3=new MgMusic();
        System.out.println(helper3.getLyric("60054701923"));
        MusicHelper helper4=new KwMusic();
        System.out.println(helper4.getLyric("80488731"));
        MusicHelper helper5=new KgMusic();
        System.out.println(helper5.getLyric("1978DAF7ED27A7A53660D940F1D02378"));
        MusicHelper helper6=new XmMusic();
        System.out.println(helper6.getLyric("65766"));
    }
    @Test
    public void convertTimeTest(){
        MusicHelper helper=new KgMusic();
        List<SongBean> list=helper.searchWithDownload("薛之谦",5);
        System.out.println(GsonConverter.toJson(list));
        String lyric=helper.getLyric("a2544e395c12de34f6d3f6c75e8de955-1f2abb109cd8585eb9635bbb213e5f8d-23214810a242765c9e721150593a2992");
        System.out.println(lyric);

    }
    @Test
    public void xmTest(){
        MusicHelper helper=new XmMusic();
        List<SongBean> list=helper.searchWithDownload("双笙",2);
        System.out.println(GsonConverter.toJson(list));
        String lyric=helper.getLyric("1795766597");
        lyric=lyric.replaceAll("<[\\d]+>","");
        lyric=lyric.replaceAll("\\[","\n[");
        lyric=lyric.substring(2);
        System.out.println(lyric.substring(2));

    }
    @Test
    public void downloadTest(){
        String songSrc="WY";
        String songId="65766";
        SongBean songBean=new SongBean(songSrc,songId);
        SongBean bean=dispatcher.downloadDispatcher(songSrc,songId,"PQ");
        System.out.println(GsonConverter.toJson(bean));


    }
    @Test
    public void testKwInfo(){
        String songId="157578224";
        String requestLink = "http://www.kuwo.cn/api/www/music/musicInfo?mid=" +songId;
        Map<String,String> headerMap=new HashMap<>();
        headerMap.put("Cookie","kw_token=62COOIBCS34");
        headerMap.put("csrf","62COOIBCS34");
        //headerMap.put("Referer","https://kuwo.cn/search/list?key="+key);
        headerMap.put("Keep-Alive","close");
        System.out.print(downloadWebSiteUseGet(requestLink,headerMap));
    }

    @Test
    public void testQQ() throws UnsupportedEncodingException {
//002J4UUk29y8BY
//        String data="{\"detail\":{\"module\":\"music.pf_song_detail_svr\",\"method\":\"get_song_detail\",\"param\":{\"song_id\":102636799}}}";
        String songId="0002u6ZH3GSQPl";
        String data="{\"songinfo\":{\"module\":\"music.pf_song_detail_svr\",\"method\":\"get_song_detail\",\"param\":{\"song_mid\":\""+songId+"\"}}}";
        String encode=URLEncoder.encode(data,"UTF-8");
        String api="https://u.y.qq.com/cgi-bin/musicu.fcg";
        String response=HttpRequestHelper.downloadWebSiteUseGet(api+"?data="+encode,null);
        System.out.println(response);
    }

    private static final HashMap<String, String> qualityMap;

    static {
        qualityMap=new HashMap<>();
        qualityMap.put(QUALITY_SQ,"&br=2000&format=flac");
        qualityMap.put(QUALITY_HQ,"&br=320&format=mp3");
        qualityMap.put(QUALITY_PQ,"&br=192&format=mp3");
        qualityMap.put(QUALITY_LQ,"&br=128&format=mp3");
    }
    @Test
    public void testKw(){
        String songId=228908+"";
        KwMusic music=new KwMusic();
        SongBean songBean=new SongBean("KW",songId);
        System.out.println(music.searchDownloadLink(songBean,"HQ"));
    }

// new String[]{"MP4HV", "MP4HV"}, new String[]{"MP4UL", "MP4UL"}, new String[]{"MP4BD", "MP4BD"}}
    @Test
    public void testKwMv(){
        String quality="SQ";
        String mvId="228908";
        String link = "http://mobi.kuwo.cn/mobi.s?f=kuwo&q=";
        String encParams = KwEncrypt.encrypt("&type=convert_mv_url2&format=mp4&quality=MP4UL&sig=0&rid=" + mvId + "&network=wifi");
        Map<String,String> headerMap=new HashMap<>();
        headerMap.put("User-Agent",USER_AGENT_ANDROID);
        String response= downloadWebSiteUseGet(link + encParams,headerMap);
        if(response.contains("url=") && response.contains("sig=")){
            response=response.substring(response.indexOf("url=")+4,response.indexOf("sig="));
            System.out.println(response);
        }
    }
    @Test
    public void testKwLyric(){
        String api="http://newlyric.kuwo.cn/newlyric.lrc?";
        String params="&lrcx=1&olrc=1&type=lyric&req=1&rid=MUSIC_228908";
        String p=base64(params.getBytes(StandardCharsets.UTF_8),"yeelion");
        System.out.println(api+p);

    }

    public String base64(byte[] arr,String str){
        int i6;
        if (str != null && !str.equals("")) {
            byte[] bytes = str.getBytes();
            for (int i7 = 0; i7 < arr.length; i7 = i6) {
                i6 = i7;
                int i8 = 0;
                while (i8 < bytes.length && i6 < arr.length) {
                    arr[i6] = (byte) (arr[i6] ^ bytes[i8]);
                    i8++;
                    i6++;
                }
            }
        }
        return Base64.getEncoder().encodeToString(arr);

    }

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
            for (long l : arr) {
                r = s[1];
                r = magic(dirF, r);
                r ^= l;
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
                r = magic(dirH, r);
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
    @Test
    public void mgTest(){
        MgHelper.searchMusic("周杰伦",1,1);
    }


    private static class MgHelper{
        public static String searchMusic(String keyword,int page,int num) {
            try {
                keyword = keyword.trim();
                String userPhoneNum = Base64.getEncoder().encodeToString(genPhoneNumber().getBytes(StandardCharsets.UTF_8));
                String requestUrl=requestUrl(keyword,num,page);
                Map<String,String> requestHeader=requestHeader(keyword,userPhoneNum);
                return HttpRequestHelper.downloadWebSiteUseGet(requestUrl,requestHeader).trim();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }

        public static Map<String,String> requestHeader(String keyword,String msiDn){
            String deviceId=genDeviceId(32);
            HashMap<String,String> hashMap = new HashMap<>();
            hashMap.put("deviceId", deviceId);
            hashMap.put("appId", "yyapp2");
            hashMap.put("android_id", "afad815163f1d394");
            hashMap.put("ua", "Android_migu");
            hashMap.put("version", "7.0.7");
            hashMap.put("uiVersion", "A_music_3.4.0");
            //hashMap.put("channel", "0146951");
            String timestamp=System.currentTimeMillis()+"";
            String sig="6cdc72a439cef99a3418d2a78aa28c73";//咪咕签名的MD5
            String sign=convertToMd5(keyword+ sig+ "yyapp2d16148780a1dcc7408e06336b98cfd50"+deviceId+msiDn+timestamp);
            hashMap.put("sign",sign);
            hashMap.put("timestamp",timestamp);
            hashMap.put("msisdn",msiDn);
            return hashMap;
        }

        public static String requestUrl(String keyword,int num,int page) {
            HashMap<String,String> hashMap = new HashMap<>();
            hashMap.put("feature","1011000000");
            hashMap.put("pageNo", page+"");
            hashMap.put("pageSize", num+"");
            hashMap.put("sort","0");
            try {
                hashMap.put("text", URLEncoder.encode(keyword,"UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String searchSwitch= null;
            try {
                searchSwitch = URLEncoder.encode("{\"song\":1,\"album\":0,\"singer\":0,\"tagSong\":0,\"mvSong\":0,\"bestShow\":0,\"songlist\":0,\"lyricSong\":1}","utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            hashMap.put("searchSwitch", searchSwitch);
            hashMap.put("sid", getSid());
            hashMap.put("isCopyright","1");
            hashMap.put("isCorrect", "1");
            StringBuilder sb=new StringBuilder();
            for (String key:hashMap.keySet()){
                sb.append(key).append("=").append(hashMap.get(key)).append("&");
            }
            return "https://jadeite.migu.cn/music_search/v2/search/searchAll?"+sb.toString();
        }
        public static String getSid() {
            String uuid = UUID.randomUUID().toString();
            String uuid2 = UUID.randomUUID().toString();
            String replace = uuid.replace("-", "");
            return replace + uuid2.replace("-", "");
        }

        public static String genPhoneNumber(){
            int[] nums={0,1,2,3,4,5,6,7,8,9};
            StringBuilder sb=new StringBuilder(11);
            sb.append("188");
            Random ran=new Random(System.currentTimeMillis());
            for (int i=0;i<8;i++){
                sb.append(nums[ran.nextInt(nums.length)%nums.length]);
            }
            return sb.toString();
        }
        public static String genDeviceId(int len){
            char[] cs="01234567890ABCDEF".toCharArray();
            StringBuilder sb=new StringBuilder(11);
            Random ran=new Random(System.currentTimeMillis());
            for (int i=0;i<len;i++){
                sb.append(cs[ran.nextInt(cs.length)%cs.length]);
            }
            return sb.toString();
        }

    }
    @Test
    public void mmgtest(){
        String requestUrl="https://app.c.nf.migu.cn/MIGUM2.0/v2.1/content/listen-url?albumId=1133602125&contentId=600913000009295303&copyrightId=69926100937&netType=01&resourceType=2&toneFlag=SQ";
        Map<String,String> headerMap=new HashMap<>();
        headerMap.put("channel","0146951");
        System.out.println(downloadWebSiteUseGet(requestUrl,headerMap));
    }
}
