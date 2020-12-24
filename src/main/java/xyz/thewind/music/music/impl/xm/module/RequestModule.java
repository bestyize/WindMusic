package xyz.thewind.music.music.impl.xm.module;

import xyz.thewind.music.music.impl.xm.enc.XmEncrypt;
import xyz.thewind.music.util.HttpRequestHelper;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestModule {
    private static final Map<String,String> headerMap=new HashMap<>();
    private static String api="https://www.xiami.com/api/search/searchSongs";

    static {
        headerMap.put("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36 Edg/87.0.664.66");
        headerMap.put("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");

    }


    public static String searchSong(String keyword,int offset,int num){
        int pageSize=num;
        int page=offset/pageSize+1;
        String sign= XmEncrypt.requestSign(api,getXmSigToken());
        String queryParams= null;
        try {
            queryParams = URLEncoder.encode("{\"key\":\""+keyword+"\",\"pagingVO\":{\"page\":"+page+",\"pageSize\":"+num+"}}","utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String requestLink=api+"?_q="+queryParams+"&_s="+sign;
        return HttpRequestHelper.downloadWebSiteUseGet(requestLink,headerMap);
    }

    public static String getXmSigToken(){
        List<String> cookies=getCookie();
        StringBuilder sb=new StringBuilder();
        String sign="";
        if(cookies!=null){
            for (String cookie:cookies){
                if(cookie.startsWith("xm_sg_tk=")){
                    sign=cookie.replace("xm_sg_tk=","");
                    sign=sign.substring(0,sign.indexOf("_"));
                }
                sb.append(cookie.substring(0,cookie.indexOf(";")+1));
            }
        }
        headerMap.put("Cookie",sb.toString());
        return sign;

    }

    public static List<String> getCookie(){
        Map<String, List<String>> headerMap=HttpRequestHelper.downloadHeaderUseGet("https://xiami.com/");
        return headerMap.get("set-cookie");
    }
}
