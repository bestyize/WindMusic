package xyz.thewind.music;

import org.junit.Test;
import xyz.thewind.music.model.SongBean;
import xyz.thewind.music.music.MusicDispatcher;
import xyz.thewind.music.util.GsonConverter;
import xyz.thewind.music.util.HttpRequestHelper;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MusicSourceTest {
    MusicDispatcher dispatcher=new MusicDispatcher();
    @Test
    public void test(){

        List<SongBean> songBeanList=dispatcher.searchDownDispatcher("MG","双笙",5);
        System.out.println(GsonConverter.toJson(songBeanList));
    }
    @Test
    public void cloudMusicTest(){
        List<SongBean> songBean=dispatcher.searchDownDispatcher("QQ","陈奕迅",5);
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

}
