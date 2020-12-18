package xyz.thewind.music;

import org.junit.Test;
import xyz.thewind.music.model.SongBean;
import xyz.thewind.music.music.MusicDispatcher;
import xyz.thewind.music.music.MusicHelper;
import xyz.thewind.music.music.impl.*;
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
        List<SongBean> songBean=dispatcher.searchDownDispatcher("QQ","薛之谦",5);
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



}
