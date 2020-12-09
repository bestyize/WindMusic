package xyz.thewind.music.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.thewind.music.model.CommonResp;
import xyz.thewind.music.model.SongBean;
import xyz.thewind.music.music.MusicDispatcher;
import xyz.thewind.music.service.RedisService;
import xyz.thewind.music.util.Base64Converter;
import xyz.thewind.music.util.GsonConverter;
import xyz.thewind.music.util.NumberHelper;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class MusicController {
    @Autowired
    private RedisService redisService;
    private static final MusicDispatcher musicDispatcher=new MusicDispatcher();
    @RequestMapping("/api/download")
    public ResponseEntity<String> download(String songid,String quality,String src,String batch){
        String response;
        HttpHeaders headers=new HttpHeaders();
        headers.add("Content-Type","application/json");
        String songSrc=loadConfig().get("songSrc");
        if(songSrc!=null&&songSrc.length()>0){
            src=songSrc;
        }
        musicDispatcher.setConfigMap(loadConfig());
        if(batch==null){
            SongBean songBean=musicDispatcher.downloadDispatcher(src,songid,quality);
            response=GsonConverter.toJson(songBean);
        }else {
            List<SongBean> songBeanList=musicDispatcher.downloadDispatcher(src,batch);
            response=GsonConverter.toJson(songBeanList);
        }
        return new ResponseEntity<>(response,headers, HttpStatus.OK);
    }
    @RequestMapping("/api/search")
    public ResponseEntity<List<SongBean>> search(String keyword,String num,String src){
        String songSrc=loadConfig().get("songSrc");
        if(songSrc!=null&&songSrc.length()>0){
            src=songSrc;
        }
        musicDispatcher.setConfigMap(loadConfig());
        return new ResponseEntity<>(musicDispatcher.searchDispatcher(src,keyword, NumberHelper.parseNumber(num,10)),HttpStatus.OK);
    }

    @RequestMapping("/api/searchdown")
    public ResponseEntity<List<SongBean>> searchDown1(String keyword,String num,String src){
        String songSrc=loadConfig().get("songSrc");
        if(songSrc!=null&&songSrc.length()>0){
            src=songSrc;
        }
        musicDispatcher.setConfigMap(loadConfig());
        List<SongBean> songBeanList=musicDispatcher.searchDownDispatcher(src,keyword,NumberHelper.parseNumber(num,10));
        return new ResponseEntity<>(songBeanList,HttpStatus.OK);
    }

    @RequestMapping("/api/configupdate")
    public ResponseEntity<CommonResp> configUpdate(String params){
        CommonResp commonResp=new CommonResp();
        if(updateConfig(params)){
            commonResp.code(0).msg("更新成功");
        }else {
            commonResp.msg("更新失败");
        }
        return new ResponseEntity<>(commonResp,HttpStatus.OK);
    }

    //qqmusic_uin=3011429848;qqmusic_key=EDKSKDJFAO10919WFKJKQF;kg_uid=12345;kg_token=1234;kg_pid_secret=123456;wy_music_u=ba0a04e302da6a12399d1817fe4eae9dd7c6b7f4865bf4fd9b986b5c8c07893333a649814e309366
    public boolean updateConfig(String params){
        if(params==null||params.length()==0||!redisService.isRedisActive()){
            return false;
        }
        try {
            String clearContent= Base64Converter.decode(params);
            String[] cookies=clearContent.split(";");
            for (String c:cookies){
                String[] splice=c.split("=");
                if(splice.length==2){
                    redisService.set(splice[0],splice[1]);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static List<String> configItemList=new ArrayList<>();

    public Map<String,String> loadConfig(){
        if(configItemList.size()==0){
            configItemList.add("qqmusic_uin");
            configItemList.add("qqmusic_key");
            configItemList.add("kg_uid");
            configItemList.add("kg_token");
            configItemList.add("kg_pid_secret");
            configItemList.add("wy_music_u");
            configItemList.add("songSrc");
        }
        Map<String,String> configMap=new HashMap<>();
        if(!redisService.isRedisActive()){
            return configMap;
        }
        for (String key:configItemList){
            String val=redisService.get(key);
            configMap.put(key,val);
        }
        return configMap;
    }


}
