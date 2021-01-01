package xyz.thewind.music.music.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.Test;
import xyz.thewind.music.model.SongBean;
import xyz.thewind.music.music.MusicHelper;
import xyz.thewind.music.music.impl.kg.KgMusic;
import xyz.thewind.music.util.GsonConverter;
import xyz.thewind.music.util.HttpRequestHelper;
import xyz.thewind.music.util.Md5Encrypt;

import java.util.List;

public class KgTest {
    @Test
    public void testKg(){
//        MusicHelper helper=new KgMusic();
//        List<SongBean> songBeanList=helper.searchMusic("周杰伦",10);
//        System.out.println(GsonConverter.toJson(songBeanList));
        requestBuild("0a62227caab66f54d43ec084b4bdd81f");
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


                    MusicHelper helper=new KgMusic();
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
}
