package xyz.thewind.music;

import org.junit.Test;
import xyz.thewind.music.util.HttpRequestHelper;
import xyz.thewind.music.util.Md5Encrypt;

import java.util.HashMap;
import java.util.Map;

import static xyz.thewind.music.util.HttpRequestHelper.downloadWebSiteUseGet;
import static xyz.thewind.music.util.Md5Encrypt.convertToMd5;

public class KgTest {
    @Test
    public void testKgLite(){
        getDownload("0a049dd07b5dd0ae135894d8acfab825");
        getDownload("f24e0a3e2a7973bd65b1c6a540832b59");
    }

    private static void getDownload(String hash){
        String auth="3d3b7cf6b82cac63007f6d677b900acd";
        String mid="114579075579861257784010836589870151519";
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
                "&open_time=20201223" +
                "&album_id=971783" +
                "&pidversion=3001" +
                "&module_id=15" +
                "&key=" +key+
                "&version=10412" +
                "&auth=" +auth+
                "&album_audio_id=32144418" +
                "&with_res_tag=0" +
                "&IsFreePart=1";
        Map<String,String> headerMap=new HashMap<>();
        System.out.println(baseLink);
        System.out.println(downloadWebSiteUseGet(baseLink,headerMap));
    }

}
