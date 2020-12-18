package xyz.thewind.music.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.thewind.music.model.CommonResp;
import xyz.thewind.music.model.PlayerBean;
import xyz.thewind.music.model.SongBean;
import xyz.thewind.music.music.MusicDispatcher;
import xyz.thewind.music.service.RedisService;
import xyz.thewind.music.util.Base64Converter;
import xyz.thewind.music.util.GsonConverter;
import xyz.thewind.music.util.LyricHelper;
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
    public ResponseEntity<List<SongBean>> searchDown(String keyword,String num,String src){
        String songSrc=loadConfig().get("songSrc");
        if(songSrc!=null&&songSrc.length()>0){
            src=songSrc;
        }
        musicDispatcher.setConfigMap(loadConfig());
        List<SongBean> songBeanList=musicDispatcher.searchDownDispatcher(src,keyword,NumberHelper.parseNumber(num,10));
        return new ResponseEntity<>(songBeanList,HttpStatus.OK);
    }
    @RequestMapping("/api/getlyric")
    public ResponseEntity<String> searchLyric(String songid,String src){
        String songSrc=loadConfig().get("songSrc");
        if(songSrc!=null&&songSrc.length()>0){
            src=songSrc;
        }
        return new ResponseEntity<>(musicDispatcher.searchLyricDispatcher(src,songid),HttpStatus.OK);
    }
    @RequestMapping("/api/player")
    public ResponseEntity<List<PlayerBean>> player(String shareId){
        List<PlayerBean> playerBeanList=new ArrayList<>();
        if(shareId!=null){
            String[] pair =shareId.split("_");
            if(pair.length==2){
                String songSrc=pair[0];
                String songId=pair[1];
                musicDispatcher.setConfigMap(loadConfig());
                SongBean bean=musicDispatcher.downloadDispatcher(songSrc,songId,"PQ");
                if(bean!=null){
                    String pic=bean.getPicUrl();
                    if(pic==null||pic.length()<10){
                        pic="img/wind-icon-256.png";
                    }
                    String url=bean.getDownloadLinkMap().get("PQ");
                    if(url==null&&(url=bean.getDownloadLinkMap().get("LQ"))==null){
                        url="";
                    }
                    String author="";
                    if(bean.getSingersName()!=null){
                        int size=bean.getSingersName().size();
                        if(size==1){
                            author=bean.getSingersName().get(0);
                        }else if(size>=2){
                            author=bean.getSingersName().get(0)+"&"+bean.getSingersName().get(1);
                        }
                    }
                    String lyric=musicDispatcher.searchLyricDispatcher(songSrc,songId);
                    SongBean detail=musicDispatcher.songDetailDispatcher(songSrc,songId);
                    String title=LyricHelper.getSongName(songSrc,lyric);
                    String singerName=LyricHelper.getSingerName(songSrc,lyric);
                    if(detail!=null){
                        title=detail.getSongName();
                        List<String> singerNames=detail.getSingersName();
                        if(singerNames.size()==1){
                            singerName=singerNames.get(0);
                        }else if(singerNames.size()>1){
                            singerName=singerNames.get(0)+"&"+singerNames.get(1);
                        }
                        if(detail.getPicUrl()!=null){
                            pic=detail.getPicUrl();
                        }
                    }
                    PlayerBean playerBean=new PlayerBean(title ,singerName ,pic,url,lyric);
                    playerBeanList.add(playerBean);
                }

            }
        }
        return new ResponseEntity<>(playerBeanList,HttpStatus.OK);
    }
    @RequestMapping("/api/songdetail")
    public ResponseEntity<SongBean> getSongDetail(String src,String songid){
        SongBean bean=musicDispatcher.songDetailDispatcher(src,songid);
        return new ResponseEntity<>(bean,HttpStatus.OK);
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
    @RequestMapping("/api/getconfig")
    public ResponseEntity<CommonResp> getConfig(){
        HttpHeaders headers=new HttpHeaders();
        headers.add("Content-Type","application/json");
        return new ResponseEntity<>(new CommonResp().code(0).msg("拉取成功").obj(loadConfig()),headers,HttpStatus.OK);
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
