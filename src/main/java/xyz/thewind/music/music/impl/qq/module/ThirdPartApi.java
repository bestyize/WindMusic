package xyz.thewind.music.music.impl.qq.module;

import xyz.thewind.music.util.HttpRequestHelper;
import xyz.thewind.music.util.Md5Encrypt;

import java.util.*;
import static xyz.thewind.music.util.HttpRequestHelper.downloadWebSiteUseGet;

public class ThirdPartApi {

    public static String getPlayUrl(String songMid,String quality){
        return SixYinDumper.requestUrl(songMid,quality);
    }

    private static class QMDDumper {
        private static String host = "http://114.115.239.164/api/MusicLink/";
        private static final Map<String,String> qualityMap=new HashMap<>();
        static {
            qualityMap.put("LQ","_96Kbps");
            qualityMap.put("PQ","_128Kbps");
            qualityMap.put("HQ","_320Kbps");
            qualityMap.put("SQ","_flac");
        }

        public static String requestUrl(String songMid,String quality){
            String qualityTag=qualityMap.get(quality);
            String requestLink=host+encrypt(qualityTag+songMid);
            Map<String,String> headerMap=new HashMap<>();
            headerMap.put("User-Agent","Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.77 Safari/537.36");
            String response= downloadWebSiteUseGet(requestLink,headerMap);
            if(response.contains("\"link\":\"")){
                response=response.substring(response.indexOf("\"link\":\"")+"\"link\":\"".length());
                if(response.contains("\",")){
                    return response.substring(0,response.indexOf(",")-1);
                }
            }
            return "";
        }


        private static String decrypt(String str) {
            try {
                char[] charArray = str.toCharArray();
                ArrayList arrayList = new ArrayList();
                ArrayList arrayList2 = new ArrayList();
                char[] reverse = reverse(charArray);
                for (int i = 1; i <= reverse.length; i++) {
                    char c = reverse[i - 1];
                    if (c >= 'a' && c <= 'z') {
                        c = '0';
                    }
                    arrayList.add(Character.valueOf(c));
                    if (arrayList.size() == 3) {
                        arrayList2.add(Character.valueOf((char) Integer.parseInt(listToStr(arrayList))));
                    }
                    if (arrayList.size() == 6) {
                        arrayList.clear();
                    }
                }
                return listToStr(arrayList2);
            } catch (Exception e) {
                return "";
            }
        }

        private static String encrypt(String str) {
            char[] charArray = str.toCharArray();
            String str2 = "";
            for (char c : charArray) {
                int nextInt = new Random((long) c).nextInt(999);
                str2 = str2 + (getFullNumber(c) + getFullNumber(nextInt));
            }
            char[] charArray2 = str2.toCharArray();
            for (int i = 0; i < charArray2.length; i++) {
                if (charArray2[i] == '0') {
                    long j = (long) i;
                    if (new Random(j).nextInt(999) % 2 == 0) {
                        charArray2[i] = (char) (new Random(j).nextInt(26) + 97);
                    }
                }
            }
            return arrToStr(reverse(charArray2));
        }

        private static String getFullNumber(int i) {
            if (i < 10) {
                return "00" + i;
            } else if (i < 100) {
                return "0" + i;
            } else {
                return i + "";
            }
        }

        private static char[] reverse(char[] cArr) {
            for (int i = 0; i < cArr.length / 2; i++) {
                char c = cArr[(cArr.length - i) - 1];
                cArr[(cArr.length - i) - 1] = cArr[i];
                cArr[i] = c;
            }
            return cArr;
        }

        private static String arrToStr(char[] cArr) {
            String str = "";
            for (int i = 0; i < cArr.length; i++) {
                str = str + cArr[i];
            }
            return str;
        }

        private static String listToStr(List<Character> list) {
            String str = "";
            for (int i = 0; i < list.size(); i++) {
                str = str + list.get(i);
            }
            return str;
        }

    }

    private static class SixYinDumper{
        private static final String sixYinHost="https://api.sixyin.com";
        private static final Map<String,String> qualityMap=new HashMap<>();
        static {
            qualityMap.put("SQ","flac");
            qualityMap.put("HQ","320k");
            qualityMap.put("PQ","128k");
            qualityMap.put("LQ","128k");
        }

        public static String requestUrl(String songMid,String quality){
            String url="/url/tx/"+songMid+"/"+qualityMap.get(quality);
            String auth= Md5Encrypt.convertToMd5(url+"wycheck").substring(0,16);
            Map<String,String> headerMap=new HashMap<>();
            headerMap.put("user-agent","lx-music request");
            headerMap.put("lxm","0b2fcf4ff7cc722cf7f5f0ac080bf62c76ce74ccf4cdf6340ecf84b25d1c2b80d81088339d730cca7d5d5c813814881d6d01&1005000");
            headerMap.put("wycheck",auth);
            String response=downloadWebSiteUseGet(sixYinHost+url,headerMap);
            if(response.contains("\"data\":\"")){
                response=response.substring(response.indexOf("\"data\":\"")+"\"data\":\"".length());
                if(response.contains("\"")){
                    response=response.substring(0,response.indexOf("\""));
                }
            }
            return response;
        }
    }

    private static class MusicDownloadUIDumper{
        private static final String apiHost="http://198.55.120.246:3300/song/url";
        private static final Map<String,String> qualityMap=new HashMap<>();
        static {
            qualityMap.put("LQ","128");
            qualityMap.put("PQ","128");
            qualityMap.put("HQ","320");
            qualityMap.put("SQ","flac");
        }
        public static String requestUrl(String songMid,String quality){
            String response= HttpRequestHelper.downloadWebSiteUseGet(apiHost+"?id="+songMid+"&type="+qualityMap.get(quality),null);
            if(response.contains("\"data\":\"")){
                response=response.substring(response.indexOf("\"data\":\"")+"\"data\":\"".length());
                if(response.contains("\"")){
                    response=response.substring(0,response.indexOf("\""));
                }
            }
            return response;
        }

    }

    /**
     * 仙乐接口
     */
    private static class XianYueDumper{
        private static final String apiHost="http://king.junyuewl.com/icq.php";
        private static final Map<String,String> qualityMap=new HashMap<>();
        static {
            qualityMap.put("LQ","128");
            qualityMap.put("PQ","128");
            qualityMap.put("HQ","320");
            qualityMap.put("SQ","flac");
        }
        public static String requestUrl(String songMid,String quality){
            String response= HttpRequestHelper.downloadWebSiteUseGet(apiHost+"?songid="+songMid+"&type="+qualityMap.get(quality)+"&skey="+Md5Encrypt.convertToMd5(songMid+"&monster"),null);
            return response;
        }
    }
}
