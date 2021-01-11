package xyz.thewind.music;

import org.junit.Test;
import xyz.thewind.music.util.HttpRequestHelper;
import xyz.thewind.music.util.Md5Encrypt;

import java.text.SimpleDateFormat;
import java.util.*;

import static xyz.thewind.music.util.HttpRequestHelper.downloadWebSiteUseGet;
import static xyz.thewind.music.util.Md5Encrypt.convertToMd5;

public class KgTest {
    private static Map<String,String> headerMap=new HashMap<>();
    @Test
    public void test1(){
//        System.out.println(getPlayUrl("811dbd4cdb806d009d7026a4c3fff0f4"));//周杰伦-稻香 SQ VIP 成功
//        System.out.println(getPlayUrl("daf0ba0d5d4e5fd27eefabc3f201dd40"));//毛不易-消愁 SQ VIP 失败
//        System.out.println(getPlayUrl("74350c0818dfced85dbe3d6c1dd3449d"));//薛之谦-天外来物 SQ 成功
//        System.out.println(getPlayUrl("8AEF65FE99B3608A68BE8E8E8525BCCF"));//毛不易-像我这样的人 试听 VIP 成功
        System.out.println(getPlayUrl("b84e4149d6ff8adce3cd24fe94c96fd3"));//
    }
    private static String getPlayUrl(String hash){
        SimpleDateFormat format=new SimpleDateFormat("yyyyMMdd");
        String openDate=format.format(new Date());
        String auth=requestResourceAuth(hash);
        String mid="0";
        String pidVersionSecret= "185672dd44712f60bb1736df5a377e82";
        String userId="0";
        String appid="3116";
        String key=convertToMd5(hash+pidVersionSecret+appid+mid+userId);
        String baseLink="http://trackercdnbj.kugou.com/i/v2/?" +
                "pid=411" +
                "&mid=" +mid+
                "&cmd=26" +
                "&token=" +
                "&hash=" +hash+
                "&area_code=1" +
                "&behavior=play" +
                "&appid=" +appid+
                "&module=" +
                "&vipType=65530" +
                "&ptype=0" +
                "&userid=" +userId+
                "&mtype=0" +
                "&open_time=" +openDate+
                "&album_id=971783" +
                "&pidversion=3001" +
                "&module_id=15" +
                "&key=" +key+
                "&version=10422" +
                "&auth=" +auth+
                "&album_audio_id=32144418" +
                "&with_res_tag=0";
        Map<String,String> headerMap=new HashMap<>();
        String response=downloadWebSiteUseGet(baseLink,headerMap);
        System.out.println(response);
        String downloadLink="";
        if(response.contains("\"url\":[\"")){
            downloadLink=response.substring(response.indexOf("\"url\":[\"")+"\"url\":[\"".length());
            downloadLink=downloadLink.substring(0,downloadLink.indexOf("\"")).replaceAll("\\\\","");
        }
        return downloadLink;
    }

    public static String encrypt(String text){
        String appKey="LnT6xpN3khm36zse0QzvmgTZ3waWdRSA";
        return convertToMd5(appKey+text+appKey);
    }
    private static String requestResourceAuth(String hash){
        String data="{\"data\":[{\"mixsongid\":\"0\",\"hash\":\""+hash+"\"}]}";
        //String data="{\"data\":[{\"mixsongid\":\"39612569\",\"hash\":\"3c3d93a5615fb42486cab22024945264\"},{\"mixsongid\":\"32144422\",\"hash\":\"f24e0a3e2a7973bd65b1c6a540832b59\"},{\"mixsongid\":\"39590741\",\"hash\":\"2a434e9e4a94e6beffb9e470f18765a3\"},{\"mixsongid\":\"32042821\",\"hash\":\"6aa46b972eca0dd84c22a976e06e3491\"},{\"mixsongid\":\"32042828\",\"hash\":\"0a62227caab66f54d43ec084b4bdd81f\"},{\"mixsongid\":\"32144418\",\"hash\":\"06e99d7bc5673553ede69455150daf56\"},{\"mixsongid\":\"32042824\",\"hash\":\"f6f88ee5161c728e252333b81df8524c\"},{\"mixsongid\":\"32029511\",\"hash\":\"c7bedc28088fcdb6d67e5df592834e54\"},{\"mixsongid\":\"32042819\",\"hash\":\"d7c92044dfd292f1f4ff6c35ae1edb7a\"},{\"mixsongid\":\"40288371\",\"hash\":\"6859f85541a3d58fa441d7b4b3e64f38\"},{\"mixsongid\":\"64473824\",\"hash\":\"0fff5535ce1862ec37d590688a586b9d\"},{\"mixsongid\":\"32100650\",\"hash\":\"03571660bc0bd02faa5994e19f97a005\"},{\"mixsongid\":\"32253510\",\"hash\":\"724af17cb0e375ec3e5c187e655b4d54\"},{\"mixsongid\":\"32218352\",\"hash\":\"95b48a0894fc2198b6e2b93c034aac72\"},{\"mixsongid\":\"32218354\",\"hash\":\"a338b1188d107d6d0cd8a9513f402439\"}]}";
        String timestamp=System.currentTimeMillis()/1000+"";
        Map<String,String> paramMap=new HashMap<>();
        paramMap.put("appid","3116");
        paramMap.put("dfid","0");
        paramMap.put("mid","0");
        paramMap.put("clientver","10422");
        paramMap.put("clienttime",timestamp);
        paramMap.put("uuid","0");
        String signature=encrypt(mapToStr(paramMap)+data);
        paramMap.put("signature",signature);
        String link="https://youth.kugou.com/api/tracker/v1/get_tracker_list?appid=3116&dfid=0&mid=0&clientver=10422&signature="+signature+"&clienttime="+timestamp+"&uuid=0";
        String response=HttpRequestHelper.downloadWebSiteUsePost(link,data,headerMap);
        String auth="";
        if(response.contains("\"auth\":\"")){
            auth=response.substring(response.indexOf("\"auth\":\"")+"\"auth\":\"".length());
            auth=auth.substring(0,auth.indexOf("\""));
        }
        return auth;
    }
    private static String mapToStr(Map<String, String> map) {
        if (map == null || map.isEmpty()) {
            return "";
        }
        List<String> arrayList = new ArrayList(map.keySet());
        Collections.sort(arrayList);
        StringBuilder sb = new StringBuilder();
        for (String str : arrayList) {
            if (!str.equals("")) {
                sb.append(str);
                sb.append("=");
                sb.append(map.get(str));
            }
        }
        return sb.toString();
    }
}
