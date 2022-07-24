package xyz.thewind.music.music.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.Test;
import xyz.thewind.music.model.SongBean;
import xyz.thewind.music.music.IMusicApiModel;
import xyz.thewind.music.music.impl.kg.KgMusicModel;
import xyz.thewind.music.util.HttpRequestHelper;
import xyz.thewind.music.util.Md5Encrypt;

import java.net.URLEncoder;
import java.util.*;

public class KgTest {
    @Test
    public void testKg(){
//        MusicHelper helper=new KgMusic();
//        List<SongBean> songBeanList=helper.searchMusic("周杰伦",10);
//        System.out.println(GsonConverter.toJson(songBeanList));
        //requestBuild("0a62227caab66f54d43ec084b4bdd81f");
        IMusicApiModel helper=new KgMusicModel();
        List<SongBean> list=helper.searchWithDownload("消愁",1);
        System.out.println(list);
    }

    public void requestBuild(String id){
        String storeApi="http://media.store.kugou.com/v1/get_res_privilege";
        JsonObject params = new JsonObject();
        params.addProperty("relate", "1");
        params.addProperty("userid", "0");
        params.addProperty("vip", "1");
        params.addProperty("appid", "1000");
        params.addProperty("token", "");
        params.addProperty("behavior", "play");
        params.addProperty("area_code", "1");
        params.addProperty("clientver", "8990");
        JsonArray element = new JsonArray();
        JsonObject resource = new JsonObject();
        resource.addProperty("id", 0);
        resource.addProperty("type", "audio");
        resource.addProperty("hash", id);
        element.add(resource);
        params.add("resource", element);
        String requestBody=params.toString();
        String res=HttpRequestHelper.downloadWebSiteUsePost(storeApi,requestBody,null);
        System.out.println(res);
        try {
            JsonParser parse = new JsonParser();  //创建json解析器
            JsonObject json = (JsonObject) parse.parse(res);

            int max = 0;
            String url = "";

            JsonArray relateGoods = json.get("data").getAsJsonArray().get(0).getAsJsonObject().get("relate_goods").getAsJsonArray();
            for (int i = 0; i < relateGoods.size(); i++) {
                JsonElement vo = relateGoods.get(i);
                int bitRate = vo.getAsJsonObject().get("info").getAsJsonObject().get("bitrate").getAsInt();
                if(bitRate > max) {


                    IMusicApiModel helper=new KgMusicModel();
                    SongBean songBean=new SongBean("KG",vo.getAsJsonObject().get("hash").getAsString());
                   songBean=helper.searchDownloadLink(songBean,"SQ");
                    System.out.println(songBean);
                }
            }
            System.out.println(url);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void test(){
        IMusicApiModel helper=new KgMusicModel();
        SongBean songBean=new SongBean("KG","6f13080a62ec4a89f0747aa3d080f99d");
        songBean=helper.searchDownloadLink(songBean,"SQ");
        System.out.println(songBean);
    }

    public String getPlayUrl(String hash){
        String dfid="-";
        String pid="2";
        String mid="0";
        String cmd="26";
        String clienttime=Long.valueOf(System.currentTimeMillis() / 1000)+"";
        String token="fa044ef0ad5e094b2c10f1567c1686ad7ab84caa862e160cef2232e86f39e832";
        String uuid="0";
        String area_code="1";
        String behavior="play";
        String appid="1005";
        String module="";
        String vipType="6";
        String ptype="0";
        String userid="1538743078";
        String mtype="2";
        String album_id="2900867";
        String pidversion="3001";
        String pidVersionSecret="57ae12eb6890223e355ccfcb74edf70d";
        String clientver="10479";
        String version="10479";
        String album_audio_id="album_audio_id";

        Map<String,Object> map=new HashMap<>();
        map.put("dfid", dfid);
        map.put("hash",hash);
        map.put("mtype",mtype);
        map.put("album_id",album_id);
        map.put("album_audio_id",album_audio_id);
        map.put("module",module);
        map.put("behavior",behavior);
        map.put("cmd",cmd);

        map.put("uuid", uuid);
        map.put("clientver", clientver);
        map.put("clienttime", clienttime);

        map.put("pid",pid);
        map.put("appid", appid);
        map.put("mid", mid);
        map.put("version",version);
        map.put("token",token);
        map.put("vipType",vipType);
        map.put("userid",userid);
        map.put("area_code",area_code);
        map.put("dfid",dfid);
        map.put("ptype",ptype);
        map.put("pidversion",pidversion);
        map.put("key",Md5Encrypt.convertToMd5(hash+pidVersionSecret+appid+mid+userid));
        String appkey="OIlwieks28dk2k092lksi2UIkp";
        map.put("signature",getSignature(appkey,map));
        String reqLink="https://gateway.kugou.com/v3/url"+getGetRequestParams(map);
        Map<String,String> headerMap=new HashMap<>();
        headerMap.put("x-router","tracker.kugou.com");
        String response=HttpRequestHelper.downloadWebSiteUseGet(reqLink,headerMap);
        if(response.contains("http:")&&response.contains("\"")){
            response=response.substring(response.indexOf("\"url\":[\"")+"\"url\":[\"".length());
            response=response.substring(0,response.indexOf("\"")).replaceAll("\\\\","");
        }
        return response;

    }

    public String getGetRequestParams(Map<String,Object> map) {
        if (map == null || map.size() < 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("?");
        Set<String> keySet = map.keySet();
        for (String str : keySet) {
            sb.append(str);
            sb.append("=");
            sb.append(URLEncoder.encode(map.get(str).toString()));
            sb.append("&");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }



    private static String getSignature(String str, Map<String, Object> map) {
        if (map == null || map.size() < 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        Set<String> keySet = map.keySet();
        String[] strArr = keySet.toArray(new String[keySet.size()]);
        Arrays.sort(strArr, String::compareTo);
        for (String str3 : strArr) {
            sb.append(str3);
            sb.append("=");
            sb.append(map.get(str3));
        }
        return Md5Encrypt.convertToMd5(str + sb.toString() + str);
    }

    @Test
    public void test1() {
        doSearchMusic("周杰伦", 1);


    }


    private String doSearchMusic(String keyword, int num) {
        String url = buildSearchUrl(keyword, num);
        String resp = HttpRequestHelper.downloadWebSiteUseGet(url, null);
        System.out.println(resp);
        return resp;
    }

    private String buildSearchUrl(String keyword, int num) {
        String searchUrl = "https://complexsearchretry.kugou.com/v2/search/song";
        String appkey="OIlwieks28dk2k092lksi2UIkp";
        Map<String,Object> map = new HashMap<>();
        map.put("userid", "0");
        map.put("area_code", "1");
        map.put("appid", "1005");
        map.put("page", "1");
        map.put("token", "");
        map.put("privilegefilter", "0");
        map.put("requestid", UUID.randomUUID());
        map.put("pagesize", num + "");
        map.put("clienttime", System.currentTimeMillis() / 1000);
        map.put("iscorrection", "1");
        map.put("uuid", "0");
        map.put("keyword", keyword);
        map.put("mid", "0");
        map.put("dfid", "0");
        map.put("clientver", "99999");
        map.put("platform", "AndroidFilter");
        map.put("signature", getSignature(appkey, map));
        return searchUrl + getGetRequestParams(map);
    }





}
